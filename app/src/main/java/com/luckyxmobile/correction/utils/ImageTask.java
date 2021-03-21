package com.luckyxmobile.correction.utils;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.luckyxmobile.correction.model.bean.TopicImage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageTask {

    private String TAG = ImageTask.class.getSimpleName();
    private Handler handler;
    private final ExecutorService service;
    private final LruCache<Integer, Bitmap> imageCache;

    private static final ImageTask single = new ImageTask();

    private ImageTask(){

        int maxMemory=(int)(Runtime.getRuntime().maxMemory()/1024);
        int cacheSize=maxMemory/8;
        imageCache = new LruCache<Integer, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(Integer key, Bitmap value) {
                return super.sizeOf(key, value);
            }
        };

        service = Executors.newCachedThreadPool();
        handler = new Handler(Looper.getMainLooper());
    }

    public static ImageTask getInstance() {
        return single;
    }

    public void loadTopicImage(ImageView view, TopicImage topicImage) {
        Bitmap bitmap = getImageCache(topicImage.getId());

        if (bitmap != null) {
            Glide.with(view.getContext()).load(bitmap).centerCrop().thumbnail(0.1f).into(view);
        } else if (FilesUtils.getInstance().existsCache(topicImage)) {
            Glide.with(view.getContext())
                    .load(FilesUtils.getInstance().getTopicImageCachePath(topicImage))
                    .centerCrop()
                    .thumbnail(0.1f).into(view);
        }else {
            service.submit(() -> {
                Bitmap cache1 = ImageUtil.getBitmap(topicImage);
                if (cache1 != null) {
                    imageCache.put(topicImage.getId(), cache1);
                    FilesUtils.getInstance().saveCacheTopicImage(topicImage, cache1);
                    handler.post(() ->{
                        loadTopicImage(view, topicImage);
                    });
                }
            });
        }
    }

    public void clearTopicImage(TopicImage topicImage) {
        imageCache.remove(topicImage.getId());
    }

    private Bitmap getImageCache(int id) {
        try {
            return imageCache.get(id);
        }catch (Exception e){
            return null;
        }
    }
}
