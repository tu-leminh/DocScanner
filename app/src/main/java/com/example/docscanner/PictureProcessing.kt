package com.example.docscanner

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Environment
import android.util.Log
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow


class PictureProcessing {
    fun tf(bitmap: Bitmap): Bitmap {

        val src = Mat()
        val srcSaved = Mat()
        Utils.bitmapToMat(bitmap, src)
        Utils.bitmapToMat(bitmap, srcSaved)

        val ratio = src.size().height / 500
        val size = Size(src.size().width / ratio, src.size().height / ratio)

        val resized = Mat()
        val grayed = Mat()
        val canned = Mat()

        Imgproc.resize(src, resized, size)
        Imgproc.cvtColor(resized, grayed, Imgproc.COLOR_BGR2GRAY)
        Imgproc.GaussianBlur(grayed, grayed, Size(5.0, 5.0), 0.0)

        val listDouble: MutableList<Double> = mutableListOf()
        for (i in 0 until grayed.rows())
            for (j in 0 until grayed.cols()) {
                listDouble.add(grayed.get(i, j)[0])
            }
        val median = listDouble.sorted()[listDouble.size / 2]

        Imgproc.Canny(
            grayed,
            canned,
            max(0.0, (1 - 0.33) * median),
            min(255.0, (1 + 0.33) * median)
        )
        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(
            canned,
            contours,
            hierarchy,
            Imgproc.RETR_LIST,
            Imgproc.CHAIN_APPROX_SIMPLE
        )

        var maxVal = 0.0
        var points: Array<Point> = arrayOf(

            Point(0.0, 0.0),
            Point(0.0, src.size().height / ratio),
            Point(src.size().width / ratio, src.size().height / ratio),
            Point(src.size().width / ratio, 0.0)
        )
        for (i in contours.indices) {
            val contourArea = Imgproc.contourArea(contours[i])
            if (maxVal < contourArea) {
                val c2f = MatOfPoint2f(*contours[i].toArray())
                val peri = Imgproc.arcLength(c2f, true)
                val approx = MatOfPoint2f()
                Imgproc.approxPolyDP(c2f, approx, 0.02 * peri, true)
                if (approx.toArray().size == 4) {
                    maxVal = contourArea
                    points = approx.toArray()
                }

            }
        }
        val srcPoints: MutableList<Point> = mutableListOf()
        var minVal = 1e9
        for (i in points) {
            srcPoints.add(Point(i.x * ratio, i.y * ratio))
            minVal = min(minVal, i.x * ratio + i.y * ratio)
        }
        while (srcPoints[0].x + srcPoints[0].y - minVal < 1e-3) {
            srcPoints.add(srcPoints[0])
            srcPoints.removeAt(0)
        }
        val width = max(
            ((srcPoints[0].x - srcPoints[3].x).pow(2) + (srcPoints[0].y - srcPoints[3].y).pow(2)).pow(
                0.5
            ),
            ((srcPoints[2].x - srcPoints[1].x).pow(2) + (srcPoints[2].y - srcPoints[1].y).pow(2)).pow(
                0.5
            )
        )
        val height = max(
            ((srcPoints[0].x - srcPoints[1].x).pow(2) + (srcPoints[0].y - srcPoints[1].y).pow(2)).pow(
                0.5
            ),
            ((srcPoints[2].x - srcPoints[3].x).pow(2) + (srcPoints[2].y - srcPoints[3].y).pow(2)).pow(
                0.5
            )
        )
        if (width > height) {
            srcPoints.add(srcPoints[0])
            srcPoints.removeAt(0)
        }
        val doc = Mat(height.toInt(), width.toInt(), CvType.CV_8UC4)
        val srcMat = Mat(4, 1, CvType.CV_32FC2)
        val dstMat = Mat(4, 1, CvType.CV_32FC2)
        srcMat.put(
            0,
            0,
            srcPoints[3].x,
            srcPoints[3].y,
            srcPoints[2].x,
            srcPoints[2].y,
            srcPoints[1].x,
            srcPoints[1].y,
            srcPoints[0].x,
            srcPoints[0].y
        )
        dstMat.put(0, 0, 0.0, 0.0, width, 0.0, width, height, 0.0, height)
        val m = Imgproc.getPerspectiveTransform(srcMat, dstMat)
        Imgproc.warpPerspective(src, doc, m, doc.size())
        val out: Bitmap = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888)
        Imgproc.cvtColor(doc, doc, Imgproc.COLOR_BGR2GRAY)
        Imgproc.adaptiveThreshold(
            doc, doc, 255.0,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY, 199, 10.0
        )
        Imgproc.resize(doc, doc, src.size())
        Utils.matToBitmap(doc, out)
        return out
    }



    companion object {
        fun toPdf(Pictures: ArrayList<Bitmap>,fileName: String) {
            val document = PdfDocument()
            val pageInfo = PageInfo.Builder(Pictures[0].width, Pictures[0].height, Pictures.size).create()
            val mediaStorageDir = File(Environment.getExternalStorageDirectory(), "DocScanner")
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("MAKE DIR", "failed to create directory")
                }
            }
            for (p in Pictures) {
                val page: PdfDocument.Page = document.startPage(pageInfo)
                val canvas: Canvas = page.canvas
                val paint = Paint()
                canvas.drawPaint(paint)
                canvas.drawBitmap(p, 0f, 0f, null)
                document.finishPage(page)
            }

            val file: File =
                File(mediaStorageDir.toString(), fileName + ".pdf")

            try {
                val use = FileOutputStream(file).use { out ->
                    document.writeTo(out)
                    document.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }


        }
    }
}