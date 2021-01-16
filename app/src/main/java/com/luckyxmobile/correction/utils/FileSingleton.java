package com.luckyxmobile.correction.utils;

import android.content.Context;
import android.util.Log;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSingleton {

    private final String TAG = FileSingleton.class.getSimpleName();

    private static final FileSingleton instance = new FileSingleton();

    private FileSingleton() {}

    public static FileSingleton getInstance() {
        return instance;
    }

    private Context context;
    private boolean isInit =false;

    private String PAPER_DIR = "/Paper";
    private String BOOK_DIR = "/Book";

    public void init(Context context) {
        this.context = context;

        isInit = true;

        File bookDir = context.getExternalFilesDir(BOOK_DIR);
        if (!bookDir.exists()) {
            if (!bookDir.mkdirs()) {
                Log.w(TAG, "book dir mkdirs fail");
                isInit = false;
            }
            BOOK_DIR = bookDir.getPath();
        }

        File paperDir = context.getExternalFilesDir(PAPER_DIR);
        if (!paperDir.exists()) {
            if (!paperDir.mkdirs()) {
                Log.w(TAG, "paper dir mkdirs fail");
                isInit = false;
            }
            PAPER_DIR = paperDir.getPath();
        }


    }

    private String getBOOK_DIR() {
        return BOOK_DIR;
    }

    private String getPAPER_DIR() {
        return PAPER_DIR;
    }

    public void saveBookCover(int id, String path) {

        File dir = new File(getBOOK_DIR() + "/" + id);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File oldFile = new File(path);

        File file = new File(dir.getPath() + "/" + id + ".jpeg");

    }

    public void deleteTopicById(int topicId) {

    }

    public void deleteBookById(int bookId) {

    }
}
