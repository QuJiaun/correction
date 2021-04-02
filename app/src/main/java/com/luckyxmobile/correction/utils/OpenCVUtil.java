package com.luckyxmobile.correction.utils;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;

import com.luckyxmobile.correction.model.bean.ImageParam;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ChangHao
 * @date 2019年8月8日
 * 用于opencv图像处理工具类
 */
public class OpenCVUtil {

    private String TAG = OpenCVUtil.class.getSimpleName();

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
        multiply(param.multiply);
        threshold(param.adaptiveThreshold);
        medianBlur(param.medianBlur);
        return get();
    }

    public Bitmap get() {
        Utils.matToBitmap(src, bitmap);
        return bitmap;
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

    public static List<Rect> HSV(Bitmap bitmap) {

        List<Rect> rectList = new ArrayList<>();

        Mat rgbMat = new Mat();
        Mat grayMat = new Mat();
        Mat blur1 = new Mat();

        //将原始的bitmap转换为mat型.
        Utils.bitmapToMat(bitmap, rgbMat);
        //将图像转换为灰度
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(grayMat, blur1, new Size(5, 5), 0);
        //图片二值化
        Imgproc.threshold(blur1, blur1, 60, 255, Imgproc.THRESH_BINARY);
        //寻找图形的轮廓
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(blur1, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        //遍历每个图形的轮廓
        for (MatOfPoint c : contours) {

            MatOfPoint2f matOfPoint2f = new MatOfPoint2f(c.toArray());
            double peri = Imgproc.arcLength(matOfPoint2f,true);
            MatOfPoint2f approx = new MatOfPoint2f();
            //得到大概值
            Imgproc.approxPolyDP(matOfPoint2f,approx,0.04 * peri,true);

            approx.toList().size();

            if (approx.toList().size()==4){
                Rect rect = Imgproc.boundingRect(new MatOfPoint(approx.toArray()));
                rectList.add(rect);
            }
        }

        rgbMat.release();
        grayMat.release();
        blur1.release();

        return rectList;
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

            if ( (250>height && height > 25) && (250 > width && width > 20) ){
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

        return imageWordSize;

    }

}
