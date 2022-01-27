package com.example.docscanner

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class PictureProcessing {
    fun tf(bitmap:Bitmap):Bitmap {
        //get picture
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

        val listDouble : MutableList<Double> = mutableListOf()
        for (i in 0 until grayed.rows())
            for (j in 0 until grayed.cols()) {
                listDouble.add(grayed.get(i,j)[0])
            }
        val median= listDouble.sorted()[listDouble.size/2]


        Imgproc.Canny(grayed, canned, max(0.0,(1-0.1)*median), min(255.0,(1+0.1)*median))
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
        var points: Array<Point> = arrayOf()
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
        for (i in points) {
            srcPoints.add(Point(i.x * ratio, i.y * ratio))
        }
        val width = max(
            ((srcPoints[0].x - srcPoints[1].x).pow(2) + (srcPoints[0].y - srcPoints[1].y).pow(2)).pow(
                0.5
            ),
            ((srcPoints[2].x - srcPoints[3].x).pow(2) + (srcPoints[2].y - srcPoints[3].y).pow(2)).pow(
                0.5
            )
        )
        val height = max(
            ((srcPoints[0].x - srcPoints[3].x).pow(2) + (srcPoints[0].y - srcPoints[3].y).pow(2)).pow(
                0.5
            ),
            ((srcPoints[2].x - srcPoints[1].x).pow(2) + (srcPoints[2].y - srcPoints[1].y).pow(2)).pow(
                0.5
            )
        )
        val doc = Mat(height.toInt(), width.toInt(), CvType.CV_8UC4)
        val srcMat = Mat(4, 1, CvType.CV_32FC2)
        val dstMat = Mat(4, 1, CvType.CV_32FC2)
        srcMat.put(
            0,
            0,
            srcPoints[0].x,
            srcPoints[0].y,
            srcPoints[3].x,
            srcPoints[3].y,
            srcPoints[2].x,
            srcPoints[2].y,
            srcPoints[1].x,
            srcPoints[1].y
        )
        dstMat.put(0, 0, 0.0, 0.0, width, 0.0, width, height, 0.0, height)
        val m = Imgproc.getPerspectiveTransform(srcMat, dstMat)
        Imgproc.warpPerspective(src, doc, m, doc.size())
        Imgproc.resize(doc, doc, src.size())
        val out: Bitmap = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888)


        Imgproc.cvtColor(doc,doc, Imgproc.COLOR_BGR2GRAY)
        Imgproc.adaptiveThreshold(doc,doc,255.0,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY,99,10.0)

        Imgproc.resize(canned, canned, src.size())
        Utils.matToBitmap(doc, out)
        return out
    }
}