package com.luckyxmobile.correction.utils;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.LruCache;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.luckyxmobile.correction.model.bean.TopicImage;

import java.util.concurrent.ExecutorService;

public class ImageTask {

    private Handler handler;
    private final ExecutorService service;
    private final LruCache<Integer, Bitmap> imageCache;

    private static final ImageTask single = new ImageTask();

    private ImageTask(){

        int maxMemory=(int)(Runtime.getRuntime().maxMemory()/1024);
        int cacheSize = maxMemory/16;
        imageCache = new LruCache<Integer, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(Integer key, Bitmap value) {
                return super.sizeOf(key, value);
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
        if (bitmap != null && !bitmap.isRecycled()) { //存在，直接加载
            Glide.with(view.getContext()).load(bitmap).into(view);
        } else if (FilesUtils.getInstance().existsCache(topicImage)) { //本地缓存存在，加载本地
            Glide.with(view.getContext())
                    .load(FilesUtils.getInstance().getTopicImageCachePath(topicImage))
                    .thumbnail(0.1f).into(view);
        }else { //都不存在，让线程池中的线程异步处理图片
            service.submit(() -> {
                Bitmap cache1 = BitmapUtils.getBitmap(topicImage); //处理图片
                if (cache1 != null) {
                    imageCache.put(topicImage.getId(), cache1); //放入缓存
                    FilesUtils.getInstance().saveCacheTopicImage(topicImage, cache1); //缓存到本地
                    handler.post(() -> loadTopicImage(view, topicImage)); //重新加载
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
