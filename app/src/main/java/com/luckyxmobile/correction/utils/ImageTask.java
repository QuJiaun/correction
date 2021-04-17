package com.luckyxmobile.correction.utils;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.luckyxmobile.correction.model.bean.TopicImage;

import java.util.concurrent.ExecutorService;

public class ImageTask {

    private Handler handler;
    private ExecutorService service;
    private LruCache<Integer, Bitmap> imageCache;

    private static final ImageTask single = new ImageTask();

    private ImageTask(){

        int maxMemory=(int)(Runtime.getRuntime().maxMemory()/1024);
        int cacheSize = maxMemory/16;
        imageCache = new LruCache<Integer, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(Integer key, Bitmap value) {
                return value.getRowBytes()*value.getHeight()/1024;
            }

            @Override
            protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                if (oldValue != null && !oldValue.isRecycled()) {
                    oldValue.recycle();
                }
            }
        };

        service = ThreadUtils.getInstance().getService();
        handler = ThreadUtils.getInstance().getHandler();
    }

    public static ImageTask getInstance() {
        return single;
    }

    public Bitmap getBitmapImage(TopicImage topicImage) {
        return getImageCache(topicImage.getId());
    }

    public synchronized void loadTopicImage(ImageView view, TopicImage topicImage) {
        Bitmap bitmap = getImageCache(topicImage.getId()); //通过id获取缓存
        FilesUtils filesUtils = FilesUtils.getInstance();
        String cachePath = filesUtils.getTopicImageCachePath(topicImage);
        if (bitmap != null && !bitmap.isRecycled()) { //存在，直接加载
            Glide.with(view.getContext()).load(bitmap).into(view);
            Log.d("123456", topicImage.getId() + " loadTopicImage: 1");
        } else if (filesUtils.exists(cachePath)) {
            bitmap = BitmapUtils.getBitmap(topicImage.getPath());
            if (bitmap == null) {
                filesUtils.deleteCacheTopicImage(topicImage);
                loadTopicImage(view, topicImage);
            } else {
                bitmap = BitmapUtils.getBitmap(cachePath);
                imageCache.put(topicImage.getId(), bitmap);
                Glide.with(view.getContext()).load(bitmap).into(view);
                Log.d("123456", topicImage.getId() + " loadTopicImage: 2");
            }
        } else {
            service.submit(() -> {
                Bitmap cache = null;
                //都不存在，让线程池中的线程异步处理图片
                cache = BitmapUtils.getBitmap(topicImage); //处理图片
                if (cache != null) {
                    imageCache.put(topicImage.getId(), cache); //放入缓存
                    handler.post(() -> loadTopicImage(view, topicImage)); //重新加载
                    Log.d("123456", topicImage.getId() + " loadTopicImage: 3");
                }
            });
        }
    }

    public void clearTopicImage(TopicImage topicImage) {
        FilesUtils.getInstance().deleteCacheTopicImage(topicImage);
        Bitmap bitmap = getImageCache(topicImage.getId());
        if (bitmap != null) {
            bitmap.recycle();
            imageCache.remove(topicImage.getId());
        }
    }

    private Bitmap getImageCache(int id) {
        try {
            return imageCache.get(id);
        }catch (Exception e){
            return null;
        }
    }
}
