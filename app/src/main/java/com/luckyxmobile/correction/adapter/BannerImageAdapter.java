package com.luckyxmobile.correction.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.utils.ImageUtil;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

public class BannerImageAdapter extends BannerAdapter<TopicImage, BannerImageAdapter.BannerViewHolder> {

    private final Context context;

    BannerImageAdapter(Context context, List<TopicImage> topicImages) {
        super(topicImages);
        this.context = context;
    }

    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.banner_item_image, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindView(BannerViewHolder holder, TopicImage data, int position, int size) {

        ImageUtil.GlideRadius(context, data, holder.view, 8);
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {

        ImageView view;

        BannerViewHolder(View view) {
            super(view);
            this.view = (ImageView) view;
        }
    }
}
