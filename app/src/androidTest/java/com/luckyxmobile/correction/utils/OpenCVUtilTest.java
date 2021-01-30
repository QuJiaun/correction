package com.luckyxmobile.correction.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.luckyxmobile.correction.utils.impl.FilesUtils;

import org.junit.Test;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class OpenCVUtilTest {

    private static final String TAG = "ImageUtilTest";

    @Test
    public void convertGray() throws IOException {
        Log.d(TAG, "convertGray: start convert!!");
        if (!OpenCVLoader.initDebug())
        {
            Log.e(TAG, "Cannot connect to OpenCV Manager");
        }

        Bitmap bitmap = OpenCVUtil.file2Bitmap(PdfUtilsTestTest.getImagePath());

//        byte pixels[] = new byte[bitmap1.getWidth() * bitmap1.getHeight() * 4];
//
        String filename = FilesUtils.getPdfDir() + "/" + FilesUtils.getCurrentTime() + ".png";
        File bmpfile = new File(filename);
        File filepath1 = new File(FilesUtils.getPdfDir());

//        File file = new File(sdCardDir, tmplName + ".jpg");
        if(!filepath1.exists()){
            filepath1.mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(bmpfile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
        Log.d(TAG, "convertGray: end convert!!");

    }
}