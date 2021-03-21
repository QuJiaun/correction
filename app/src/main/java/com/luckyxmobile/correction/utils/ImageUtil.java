package com.luckyxmobile.correction.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;

import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.model.bean.TopicImage;

import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtil{

    public static Bitmap getImage(Uri uri, ContentResolver resolver) {

        if (uri == null || resolver == null) {
            return null;
        }

        Bitmap bitmap = null;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(resolver.openInputStream(uri), new Rect(), options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = 1;
            bitmap = BitmapFactory.decodeStream(resolver.openInputStream(uri), new Rect(), options);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     *
     * @param imagePath
     * @return bitmap
     * @author qjj
     */
    public static Bitmap getBitmapByImagePath(String imagePath) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        //获取资源图片
        return BitmapFactory.decodeFile(imagePath, opt);
    }

    /**
     * 获取图片的旋转角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                    default:
                        break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

        /**
         * 选择变换
         *
         * @param origin 原图
         * @param alpha  旋转角度，可正可负
         * @return 旋转后的图片
         */
    public static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }


    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        float scaleWidth = ((float) newWidth) / bitmap.getWidth();
        float scaleHeight = ((float) newHeight) / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bmpScale = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return colorChange(bmpScale);
    }

    public static Bitmap resizeBitmapByImageWordSize(Bitmap bitmap,int wordSize){
        int newWidth,newHeight;
        if (wordSize <= 0){
            wordSize = OpenCVUtil.calculateImageWordSize(bitmap);
        }
        //25是打印时要求的文字大小
        newHeight = 25*bitmap.getHeight()/wordSize;
        newWidth = bitmap.getWidth()*newHeight/bitmap.getHeight();

        return resizeBitmap(bitmap,newWidth,newHeight);
    }

    /**
     * @author rfa
     * @param bm 需要提高清晰度的图片的bitmap
     * @return 提高后的图片的bitmap
     */
    public static Bitmap colorChange(Bitmap bm)
    {
        //设置饱和度
        float saturation = 200 * 1.0F / 127;
        //创建新的bitmap
        Bitmap bitmap = Bitmap.createBitmap(bm.getWidth(),bm.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        //通过饱和度生成颜色矩阵
        ColorMatrix saturationMatrix = new ColorMatrix();
        saturationMatrix.setSaturation(saturation);
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.postConcat(saturationMatrix);

        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bm,0,0,paint);
        return bitmap;
    }

    public static Bitmap getBitmap(TopicImage topicImage) {
        return OpenCVUtil.setImageContrastRadioByPath(topicImage.getContrast_radio(), topicImage.getPath());
    }
}
