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
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.ui.activity.CropImageActivity;
import com.luckyxmobile.correction.utils.impl.FilesUtils;

import android.graphics.Point;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import org.litepal.LitePal;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImageUtil{
    public static final String TAG = "PhotoUtil";
    //判断是否具有拍照权限
    public static boolean hasPermission = false;
    //图片的最终路径
    public static String resultPath = "default";

    /**
     * 每次使用完resultPath需要重置
     *
     * @author zc
     */
    public static void resetResultPath() {
        resultPath = "default";
    }

    /**
     * 获取resultPath
     *
     * @author zc
     */
    public static String getResultPath() {
        return resultPath;
    }

    /**
     * 两张图片合并
     *
     * @param file1
     * @param file2
     */
    private static void merge(final String file1, final String file2) {
        ThreadPool.runnable = new Runnable() {
            @Override
            public void run() {
                String photoPath = Environment.getExternalStorageDirectory() + "/Correction/Topic";
                File photo1 = new File(photoPath, file1);
                File photo2 = new File(photoPath, file2);
                String time = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date());
                try {
                    Bitmap bitmap1 = BitmapFactory.decodeStream(new FileInputStream(photo1));
                    Bitmap bitmap2 = BitmapFactory.decodeStream(new FileInputStream(photo2));

                    Bitmap newBmp = newBitmap(bitmap1, bitmap2);

                    File result = new File(photoPath, "topic_" + time + ".jpeg");
                    if (!result.exists()) {
                        result.createNewFile();
                    }
                    save(newBmp, result, Bitmap.CompressFormat.JPEG, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        ThreadPool.singleThreadExecutor.execute(ThreadPool.runnable);
    }

    public static Bitmap newBitmap(Bitmap bmp1, Bitmap bmp2) {
        Bitmap retBmp;
        int width = bmp1.getWidth();
        if (bmp2.getWidth() != width) {
            //以第一张图片的宽度为标准，对第二张图片进行缩放。

            int h2 = bmp2.getHeight() * width / bmp2.getWidth();
            retBmp = Bitmap.createBitmap(width, bmp1.getHeight() + h2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(retBmp);
            Bitmap newSizeBmp2 = resizeBitmap(bmp2, width, h2);
            canvas.drawBitmap(bmp1, 0, 0, null);
            canvas.drawBitmap(newSizeBmp2, 0, bmp1.getHeight(), null);
        } else {
            //两张图片宽度相等，则直接拼接。

            retBmp = Bitmap.createBitmap(width, bmp1.getHeight() + bmp2.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(retBmp);
            canvas.drawBitmap(bmp1, 0, 0, null);
            canvas.drawBitmap(bmp2, 0, bmp1.getHeight(), null);
        }

        return retBmp;
    }

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
     * 保存图片到文件File。
     *
     * @param src     源图片
     * @param file    要保存到的文件
     * @param format  格式
     * @param recycle 是否回收
     * @return true 成功 false 失败
     */
    public static boolean save(Bitmap src, File file, Bitmap.CompressFormat format, boolean recycle) {
        if (isEmptyBitmap(src)) {
            return false;
        }

        OutputStream os;
        boolean ret = false;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            ret = src.compress(format, 100, os);
            if (recycle && !src.isRecycled()) {
                src.recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Bitmap对象是否为空。
     */
    public static boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
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

//    public static Bitmap convertTopicImageByWhichs(Context context, int topicId, List<String> whichs, int position, boolean fromPrint, boolean hideHighlightAreas){
//        Topic topic = LitePal.find(Topic.class,topicId);
//        TopicImagesHighlighter topicImagesHighlighter = FastJsonUtil.jsonToObject(topic.getTopic_original_picture(), TopicImagesHighlighter.class);
//        if (topicImagesHighlighter == null){
//            Log.d(TAG, "convertTopicImageByWhichs: "+"未找到该题目图片相关信息");
//            return null;
//        }
//        String contrastRadio = topicImagesHighlighter.getImageContrastRadioList().get(position);
//        String imagePath = topicImagesHighlighter.getPrimitiveImagePathList().get(position);
//        if (whichs == null){
//            whichs = new ArrayList<>();
//            whichs.add("no_path");
//        }
//
//        Bitmap bgBitmap = OpenCVUtil.setImageContrastRadioByPath(contrastRadio,imagePath);
//        Bitmap fgBitmap = Bitmap.createBitmap(bgBitmap.getWidth(),bgBitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(fgBitmap);
//        canvas.drawBitmap(bgBitmap,0,0,null);
//        canvas.drawBitmap(fgBitmap,0,0,null);
//
//        if (!fromPrint || hideHighlightAreas){
//            for (TopicImagesHighlighter.ImageSmear imagePaints : topicImagesHighlighter.getImageSmearsList().get(position)){
//                canvas.save();
//                String which = imagePaints.getWhichSmear();
//                int paintWidth = imagePaints.getBrushWidth();
//                Paint paint = createBrush(context,which,paintWidth,whichs.contains(which), hideHighlightAreas);
//                Path path = pointsToPath(imagePaints.getSmearPoints());
//                canvas.drawPath(path, paint);
//                canvas.restore();
//            }
//        }
//
//
//
//        Bitmap newBitmap = Bitmap.createBitmap(bgBitmap.getWidth(),bgBitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        canvas = new Canvas(newBitmap);
//        canvas.drawBitmap(bgBitmap,0,0,null);
//        canvas.drawBitmap(fgBitmap,0,0,null);
//        canvas.save();
//        canvas.restore();
//
//        return newBitmap;
//    }

    public static void GlideRadius(Context context, TopicImage topicImage, ImageView imageView, int radius) {

        Bitmap bitmap = OpenCVUtil.setImageContrastRadioByPath(topicImage.getContrast_radio(), topicImage.getPath());

        if (radius > 0) {
            //设置图片圆角角度
            radius = dip2px(context, radius);
            RoundedCorners roundedCorners = new RoundedCorners(radius);
            RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(0, 0);
            Glide.with(context).load(bitmap).apply(options).into(imageView);
        } else {
            Glide.with(context).load(bitmap).into(imageView);
        }

    }

    /**
     * @editor lg
     * @date 2019/07/25
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * @editor lg
     * @date 2019/07/25
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getScreenWidth(Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        if (outMetrics.widthPixels > 0){
            return outMetrics.widthPixels;
        }else{
            return  context.getWallpaperDesiredMinimumWidth();
        }

    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        if (outMetrics.heightPixels > 0){
            return outMetrics.heightPixels;
        }else{
            return  context.getWallpaperDesiredMinimumHeight();
        }
    }




    public static void openAlbum(Activity activity) {
        Intent selectIntent = new Intent(Intent.ACTION_GET_CONTENT);
        selectIntent.setType("image/*");
        activity.startActivityForResult(selectIntent, Constants.REQUEST_CODE_SELECT_ALBUM);
    }

    public static void openCamera(Activity activity) {
        Intent startCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FilesUtils.getInstance().getCacheFileUri());
        activity.startActivityForResult(startCameraIntent, Constants.REQUEST_CODE_TAKE_PHOTO);
    }
}
