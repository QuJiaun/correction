package com.luckyxmobile.correction.utils;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.core.content.FileProvider;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.model.DaoListener;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FilesUtils{

    private String PAPER_DIR = "/Paper";
    private String BOOK_DIR = "/Book";
    private String CACHE_DIR = "/Cache";

    private Handler handler;
    private ExecutorService executorService;
    private Application application;

    private static final FilesUtils filesUtils = new FilesUtils();

    public static FilesUtils getInstance(){
        return filesUtils;
    }

    public void  init(Application application){

        this.application = application;
        executorService = Executors.newFixedThreadPool(2);
        handler = new Handler(application.getMainLooper());

        String FILE_DIR = application.getExternalFilesDir("").getAbsolutePath();

        PAPER_DIR = initAppDir(FILE_DIR + PAPER_DIR);
        BOOK_DIR = initAppDir(FILE_DIR + BOOK_DIR);
        CACHE_DIR = initAppDir(FILE_DIR + CACHE_DIR);
    }

    private  String initAppDir(String dir){
        File fileDir = new File(dir);
        fileDir.mkdirs();
        return dir;
    }

    public String getBookDir(Book book) {
        if (book == null || book.getId() <= 0) {
            throw new RuntimeException("book is not save!");
        }

        return BOOK_DIR + "/" + book.getId();
    }

    public String getBookCoverPath(Book book) {
        return getBookDir(book) + "/" + book.getId() + ".jpeg";
    }

    public String getTopicDir(Topic topic) {
        if (topic == null || topic.getId() <= 0) {
            throw new RuntimeException("topic is not save!");
        }

        return getBookDir(LitePal.find(Book.class, topic.getBook_id())) + "/" + topic.getId();
    }

    public String getTopicImagePath(TopicImage topicImage) {

        if (topicImage == null || topicImage.getId() <= 0) {
            throw new RuntimeException("topicImage 为空 or 未绑定topic");
        }

        return getTopicDir(LitePal.find(Topic.class, topicImage.getTopic_id())) + "/" + topicImage.getId() + ".jpeg";
    }

    public String getCacheFilePath() {
        return CACHE_DIR + "/tmp.jpeg";
    }

    public String getTopicImageCachePath(TopicImage topicImage) {
        return CACHE_DIR + "/TopicImage_"+ topicImage.getId() +".jpeg";
    }

    public Uri getCacheFileUri() {
        File file = new File(getCacheFilePath());

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return getUri(file);
    }

    public boolean existsCache(TopicImage topicImage) {
        File cache = new File(getTopicImageCachePath(topicImage));
        return cache.exists();
    }

    public boolean saveCacheTopicImage(TopicImage topicImage, Bitmap bitmap) {

        File cache = new File(getTopicImageCachePath(topicImage));

        if (!cache.exists()) {
            try {
                cache.createNewFile();

                FileOutputStream fos  = new FileOutputStream(cache);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                fos.flush();
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    public boolean saveBitmap2TmpFile(Bitmap bitmap)  {

        File tmp = new File(CACHE_DIR, "tmp.jpeg");

        try {
            if (!tmp.exists()){
                tmp.createNewFile();
            }

            FileOutputStream fos  = new FileOutputStream(tmp);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean saveBookInfoFile(Book book) {

        File bookDir = new File(getBookDir(book));

        if (!bookDir.exists()){
            return bookDir.mkdirs();
        }

        return true;
    }

    public boolean saveTopicInfoFile(Topic topic) {

        File topicDir = new File(getTopicDir(topic));

        if (!topicDir.exists()){
            return topicDir.mkdirs();
        }

        return true;
    }

    public void deleteTopicImage(TopicImage topicImage, DaoListener listener) {

        executorService.submit(()->{
            deleteFile(topicImage.getPath());
            if (listener != null) {
                handler.post(listener::onDeleteFinished);
            }
        });
    }

    public void deleteTopicDir(Topic topic, DaoListener listener) {

        File topicDir = new File(getTopicDir(topic));

        if (topicDir.exists()){
            executorService.submit(()->{
                deleteFile(topicDir);
                if (listener != null) {
                    handler.post(listener::onDeleteFinished);
                }
            } );
        } else if (listener != null){
            listener.onDeleteFinished();
        }
    }

    public void deleteTopicDirList(List<Topic> topicList, DaoListener listener) {

        executorService.submit(()->{
            for (Topic topic : topicList) {
                deleteTopicDir(topic, null);
            }

            if (listener != null) {
                handler.post(listener::onDeleteFinished);
            }
        });
    }

    public void deleteBookDir(Book book, DaoListener listener) {
        File bookDir = new File(getBookDir(book));

        if (bookDir.exists()){
            executorService.submit(() ->{
                deleteFile(bookDir);
                if (listener != null) {
                    handler.post(listener::onDeleteFinished);
                }
            } );
        } else if (listener != null){
            listener.onDeleteFinished();
        }
    }

    private void deleteFile(String path) {
        deleteFile(new File(path));
    }

    private void deleteFile(File file) {

        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }

            for (File f : files) {
                if (f.isFile()) {
                    f.delete();
                } else if (file.isDirectory()) {
                    deleteFile(f);
                }
            }
            file.delete();
            return;
        } else {
            file.delete();
        }

    }

    public boolean copyFile(String oldPath$Name, String newPath$Name) {

        try {

            File oldFile = new File(oldPath$Name);
            if (!oldFile.exists()) {
                Log.e("--Method--", "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                Log.e("--Method--", "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                Log.e("--Method--", "copyFile:  oldFile cannot read.");
                return false;
            }

            FileInputStream fileInputStream = new FileInputStream(oldPath$Name);    //读入原文件
            FileOutputStream fileOutputStream = new FileOutputStream(newPath$Name);
            byte[] buffer = new byte[1024];
            int byteRead;
            while ((byteRead = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Uri getUri(File file) {

        //判断版本
        if (Build.VERSION.SDK_INT >= 24) {
            //如果在Android7.0以上,使用FileProvider获取Uri
            try {
                return FileProvider.getUriForFile(application, Constants.FILE_PROVIDER, file);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            //否则使用Uri.fromFile(file)方法获取Uri
            return Uri.fromFile(file);
        }

        return null;
    }

    public static String getTimeByDate(long date) {
        return getTimeByDate(new Date(date));
    }

    public static String getTimeByDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(date);
    }

    public static String getCurrentTime() {
        return getTimeByDate(new Date(System.currentTimeMillis()));
    }

    public String appVersionName() {

        PackageManager manager = application.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(application.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }

}
