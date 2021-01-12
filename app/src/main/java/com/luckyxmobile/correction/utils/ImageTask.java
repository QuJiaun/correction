package com.luckyxmobile.correction.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LruCache;
import com.luckyxmobile.correction.adapter.BookDetailAdapter;
import com.luckyxmobile.correction.model.bean.Topic;
import java.util.ArrayList;
import java.util.List;

public class ImageTask extends AsyncTask<Void, Integer, Boolean> {

    @SuppressLint("StaticFieldLeak")
    private  Context context;
    private List<String> whichShowPrint;
    private List<Integer> topicIds = new ArrayList<>();
    private BookDetailAdapter bookDetailAdapter;
    public LruCache<Integer, Bitmap> imageCache;

    public void setBookDetailAdapter(BookDetailAdapter bookDetailAdapter) {
        this.bookDetailAdapter = bookDetailAdapter;
    }

    public ImageTask(Context context, List<Topic> topics, List<String> whichShowPrint){
        this.context = context;
        this.whichShowPrint = whichShowPrint;

        for (Topic topic: topics){
            topicIds.add(topic.getId());
        }

        int maxMemory=(int)(Runtime.getRuntime().maxMemory()/1024);
        int cacheSize=maxMemory/8;
        imageCache = new LruCache<Integer, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(Integer key, Bitmap value) {
                return super.sizeOf(key, value);
            }
        };
    }

    public Bitmap getImageCache(int id) {
        try {
            return imageCache.get(id);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try {
            int i = 1;
            for (int id: topicIds) {
                Bitmap bitmap = ImageUtil.convertTopicImageByWhichs(context, id, whichShowPrint, 0, false, false);
                if (bitmap != null){
                    imageCache.put(id, bitmap);
                    if (i%4 == 0){
                        publishProgress(topicIds.indexOf(id));
                    }
                    i++;
                }

            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        for (int i = 3; i >= 0; i--) {
            if (bookDetailAdapter != null){
                bookDetailAdapter.notifyItemChanged(values[0]-i);
            }

        }

    }


    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (bookDetailAdapter != null){
            bookDetailAdapter.notifyDataSetChanged();
        }

    }
}
