package com.luckyxmobile.correction.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.model.bean.Highlighter;
import com.luckyxmobile.correction.model.bean.ImageParam;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import me.pqpo.smartcropperlib.utils.CropUtils;

import static org.opencv.imgproc.Imgproc.COLOR_RGB2HSV;

/**
 * @author ChangHao
 * @date 2019年8月8日
 * 用于opencv图像处理工具类
 */
public class OpenCVUtil {

    private String TAG = OpenCVUtil.class.getSimpleName();

    private final Scalar scalarMin = new Scalar(0, 43, 46);
    private final Scalar scalarMax = new Scalar(180, 255, 255);

    private Mat src;
    private Bitmap bitmap;

    private static final OpenCVUtil openCVUtil = new OpenCVUtil();

    private OpenCVUtil() {
    }

    public static OpenCVUtil getInstance() {
        return openCVUtil;
    }

    public OpenCVUtil init(Bitmap bitmap) {
        this.bitmap = bitmap;
        src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2GRAY);
        return this;
    }

    public Bitmap get(ImageParam param) {
        brightness(param.brightness);
        multiply(param.multiply);
        threshold(param.adaptiveThreshold);
        medianBlur(param.medianBlur);
        return get();
    }

    public Bitmap get() {
        Utils.matToBitmap(src, bitmap);
        src.release();
        return bitmap;
    }

    /**
     * 设置图片亮度
     * @param brightness <0 降低亮度， >0增加亮度
     * @return this
     */
    public OpenCVUtil brightness(double brightness) {
        Mat tmp = new Mat();
        Core.add(src, new Scalar(brightness), tmp);
        src.release();
        src = tmp;
        return this;
    }

    public OpenCVUtil multiply(double multiply) {
        Mat tmp = new Mat();
        Core.multiply(src, new Scalar(multiply), tmp);
        src.release();
        src = tmp;
        return this;
    }

    public OpenCVUtil threshold(boolean adaptive) {
        Mat tmp = new Mat();
        if (adaptive) {
            Imgproc.adaptiveThreshold(src, tmp, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 25, 10);
        } else {
            Imgproc.threshold(src, tmp, 170, 255, Imgproc.THRESH_OTSU);
        }
        src.release();
        src = tmp;
        return this;
    }

    public OpenCVUtil medianBlur(boolean medianBlur) {
        if (medianBlur) {
            Imgproc.medianBlur(src, src, 3);
        }
        return this;
    }

//    private static double getSpacePointToPoint(Point p1, Point p2) {
//        double a = p1.x - p2.x;
//        double b = p1.y - p2.y;
//        return Math.sqrt(a * a + b * b);
//    }
//
//    public static Bitmap crop(Bitmap bitmap, Point[] points) {
//
//        Point p0 = points[0];
//        Point p1 = points[1];
//        Point p2 = points[2];
//        Point p3 = points[3];
//
//        double space0 = getSpacePointToPoint(p0, p1);
//        double space1 = getSpacePointToPoint(p1, p2);
//        double space2 = getSpacePointToPoint(p2, p3);
//        double space3 = getSpacePointToPoint(p3, p0);
//
//        double imgWidth = (space0 + space2)/2;
//        double imgHeight = (space1 + space3)/2;
//
//        Mat result = new Mat();
//        Utils.bitmapToMat(bitmap, result);
//
//        Mat quad = Mat.zeros((int)imgWidth, (int)imgHeight, CvType.CV_8UC3);
//        MatOfPoint2f cornerMat = new MatOfPoint2f(p0, p1, p2, p3);
//        MatOfPoint2f quadMat = new MatOfPoint2f(new Point(imgWidth*0.4, imgHeight*1.6),
//                new Point(imgWidth*0.4, imgHeight*0.4),
//                new Point(imgWidth*1.6, imgHeight*0.4),
//                new Point(imgWidth*1.6, imgHeight*1.6));
//
//        Mat transmtx = Imgproc.getPerspectiveTransform(cornerMat, quadMat);
//        Imgproc.warpPerspective(result, quad, transmtx, quad.size());
//        Utils.matToBitmap(quad, bitmap);
//        result.release();
//        quad.release();
//        transmtx.release();
//        return bitmap;
//    }
//
//    public static Bitmap setRotate(Bitmap bitmap, int flip) {
//        Mat src = new Mat();
//        Mat tmp = new Mat();
//        Mat dst = new Mat();
//        Utils.bitmapToMat(bitmap, src);
//        Core.transpose(src, tmp);
//        Core.flip(src, dst, flip);
//        Utils.matToBitmap(dst, bitmap);
//
//        src.release();
//        tmp.release();
//        dst.release();
//        return bitmap;
//    }

    public List<Highlighter> HSV(String path, int wordSize) {

        Mat rgbMat = Imgcodecs.imread(path, Imgcodecs.IMREAD_COLOR);
        Mat hsvMat = new Mat();
        Mat des = new Mat();

        Imgproc.cvtColor(rgbMat, hsvMat, COLOR_RGB2HSV);
        Core.inRange(hsvMat, scalarMin, scalarMax, des);

        List<Highlighter> highlighterList = new ArrayList<>();

        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(des, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (MatOfPoint matOfPoint : contours) {
            Rect rect = Imgproc.boundingRect(matOfPoint);
            Highlighter highlighter = new Highlighter(Constants.PAINT_OCR);
            if (rect.width >= wordSize && rect.height < 120 && rect.height >= wordSize ) {
                highlighter.setRect(new android.graphics.Rect(rect.x, rect.y,
                        rect.x + rect.width, rect.y + rect.height));
                highlighterList.add(highlighter);
                Log.d(TAG, "HSV: " + highlighter.getRect().width() + ":" + highlighter.getRect().height());
            }
        }

        contours.clear();
        rgbMat.release();
        hsvMat.release();
        des.release();
        hierarchy.release();
        return highlighterList;
    }

    /**
     * 计算传入图片内文字的大小
     * @param bitmap 题目图标
     * @return 文字大小
     */
    public static int calculateImageWordSize(Bitmap bitmap){
        int imageWordSize = 40;
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2),new Point(-1,-1));
        Mat element2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(6,6),new Point(-1,-1));
        Imgproc.dilate(src, src, element1, new Point(-1, -1), 1);
        Imgproc.erode(src, src, element2, new Point(-1, -1), 1);
        Imgproc.cvtColor(src,src,Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(src,src,new Size(3,3),0);
//        Utils.matToBitmap(src,mBgBitmap);

        List<Integer> wordHeights = new ArrayList<>();
        List<MatOfPoint> contours=new ArrayList<>();

        Imgproc.findContours(src,contours,new Mat(),Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);

//        Imgproc.drawContours(src, contours, -1,new Scalar(255, 0, 255));

        //2.筛选那些面积小的
        for (int i = 0; i < contours.size(); i++){

            MatOfPoint2f matOfPoint2f = new MatOfPoint2f(contours.get(i).toArray());

            //轮廓近似，作用较小，approxPolyDP函数有待研究
            double epsilon = 0.001*Imgproc.arcLength(matOfPoint2f, true);
            Imgproc.approxPolyDP(matOfPoint2f,new MatOfPoint2f(), epsilon, true);
            RotatedRect rect = Imgproc.minAreaRect(matOfPoint2f);

            int width = rect.boundingRect().width;
            int height = rect.boundingRect().height;

            if ( (120>height && height > 25) && (120 > width && width > 20) ){
                wordHeights.add(height);
            }
        }

        int max = 0;int Height = 40;
        for (Integer height1:wordHeights){
            int num = 0;
            for (Integer height2:wordHeights){
                if (Math.abs(height1-height2) < 5){
                    num++;
                }
            }
            if (num > max){
                max = num;
                Height = height1;
                Log.d("ImageUtil",max+" * 轮廓-->"+Height);
            }
        }

        imageWordSize = (int) (Height * 1.1);

        imageWordSize = Math.max(imageWordSize, 25);

        Log.i("ImageUtil","轮廓 众数 "+Height);

        contours.clear();
        wordHeights.clear();

        return imageWordSize;

    }

}
