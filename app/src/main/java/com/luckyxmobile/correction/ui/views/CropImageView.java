package com.luckyxmobile.correction.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.AttributeSet;

import com.luckyxmobile.correction.utils.BitmapUtils;
import com.luckyxmobile.correction.utils.OpenCVUtil;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.imgproc.Imgproc;

import me.pqpo.smartcropperlib.utils.CropUtils;

public class CropImageView extends me.pqpo.smartcropperlib.view.CropImageView {

    private Bitmap imageBitmap;

    public CropImageView(Context context) {
        super(context);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    @Override
    public void setImageBitmap(Bitmap imageBitmap) {
        super.setImageBitmap(imageBitmap);
        this.imageBitmap = imageBitmap;
    }

    @Override
    public Bitmap crop() {
        return super.crop();
    }

//    private static Bitmap crop(Bitmap srcBmp, Point[] cropPoints) {
//        if (srcBmp == null || cropPoints == null) {
//            throw new IllegalArgumentException("srcBmp and cropPoints cannot be null");
//        }
//        if (cropPoints.length != 4) {
//            throw new IllegalArgumentException("The length of cropPoints must be 4 , and sort by leftTop, rightTop, rightBottom, leftBottom");
//        }
//
//        org.opencv.core.Point[] points = new org.opencv.core.Point[4];
//
//        for (int i = 0; i < cropPoints.length; i++) {
//            points[i] = new org.opencv.core.Point(cropPoints[i].x, cropPoints[i].y);
//        }
//        return OpenCVUtil.crop(srcBmp, points);
//    }


    @Override
    public void setImageToCrop(Bitmap bmp) {
        setImageBitmap(bmp);
        setFullImgCrop();
    }

    public void autoScan() {
        //TODO 识别矩形 代办
    }

    /**
     * 旋转图片
     */
    public void setImageRotate(int rotate) {
        Bitmap bitmap = BitmapUtils.rotateBitmap(getImageBitmap(), rotate);
        setImageToCrop(bitmap);
    }

    public void destroy() {
        if (imageBitmap != null) {
            imageBitmap.recycle();
            imageBitmap = null;
        }
    }
}
