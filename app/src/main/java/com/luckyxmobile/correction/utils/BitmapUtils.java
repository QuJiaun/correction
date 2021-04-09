package com.luckyxmobile.correction.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.global.MySharedPreferences;
import com.luckyxmobile.correction.model.BeanUtils;
import com.luckyxmobile.correction.model.bean.Highlighter;
import com.luckyxmobile.correction.model.bean.ImageParam;
import com.luckyxmobile.correction.model.bean.TopicImage;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class BitmapUtils {

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

        if (bitmap == null) {
            return null;
        }
        return autoSize(bitmap);
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
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int wordSize){
        int newWidth,newHeight;
        if (wordSize <= 0){
            wordSize = OpenCVUtil.calculateImageWordSize(bitmap);
        }
        //25是打印时要求的文字大小
        newHeight = 25*bitmap.getHeight()/wordSize;
        newWidth = bitmap.getWidth()*newHeight/bitmap.getHeight();

        return resizeBitmap(bitmap,newWidth,newHeight);
    }

    public static Bitmap getBitmapInPdf(TopicImage topicImage, boolean showHighlighter) {
        Bitmap bitmap = getBitmap(topicImage.getPath());
        if (showHighlighter) {
            bitmap = getCanvasBitmap(topicImage, true);
        }
        return resizeBitmap(bitmap, topicImage.getWord_size());
    }

    public static Bitmap getBitmapInTopicInfo(TopicImage topicImage) {
        return getCanvasBitmap(topicImage, false);
    }

    private static Bitmap getCanvasBitmap(TopicImage topicImage, boolean isPrint) {
        Bitmap bitmap = getBitmap(topicImage);
        Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(bitmap2);
        mCanvas.drawBitmap(bitmap, 0,0, null);
        List<Highlighter> highlighters = BeanUtils.findAll(topicImage);

        for (Highlighter highlighter : highlighters) {
            mCanvas.save();
            if (isPrint) {
                PaintUtil.setPaintInPdf(highlighter.getWidth());
            } else {
                PaintUtil.setPaint(highlighter, true);
            }
            Rect rect = highlighter.getRect();
            if (rect != null) {
                mCanvas.drawRect(rect, PaintUtil.mPaint);
            } else {
                Path path = PaintUtil.pointsToPath(highlighter.getPointList());
                mCanvas.drawPath(path, PaintUtil.mPaint);
            }
            mCanvas.restore();
        }
        return bitmap2;
    }

    public static Bitmap getBitmap(TopicImage topicImage) {
        Bitmap bitmap = getBitmap(topicImage.getPath());
        if (topicImage.isOcr()) {
            return bitmap;
        }
        ImageParam param = GsonUtils.json2Obj(topicImage.getImageParam(), ImageParam.class);
        if (param == null) {
            param = new ImageParam();
        }
        return  OpenCVUtil.getInstance().init(bitmap).get(param);
    }

    public static int qualityBitmap(Bitmap bitmap, long sizeLimit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        // 循环判断压缩后图片是否超过限制大小
        int size = bos.toByteArray().length / 1024;
        while(size > sizeLimit && quality > 0) {
            bos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            quality -= 10;
            size = bos.toByteArray().length / 1024;
        }
        return quality;
    }

    public static Bitmap getBitmap(String imagePath) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, opt);
        return autoSize(bitmap);
    }

    private static Bitmap autoSize(Bitmap bitmap) {
        int bw = bitmap.getWidth();
        int bh = bitmap.getHeight();
        int screen_w = MySharedPreferences.getInstance().getInt(Constants.SCREEN_WIDTH, 1080);
        screen_w = Math.min(screen_w, 1440);
        bh = screen_w*bh / bw;
        return resizeBitmap(bitmap, screen_w, bh);
    }
}
