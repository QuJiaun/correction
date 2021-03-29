package com.luckyxmobile.correction.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.ui.views.ShowHighlighterView;
import com.youth.banner.adapter.BannerAdapter;

import org.litepal.LitePal;

import java.util.List;

public class BannerTopicImageAdapter extends BannerAdapter<TopicImage, BannerTopicImageAdapter.BannerViewHolder> {

    BannerTopicImageAdapter(List<TopicImage> topicImages) {
        super(topicImages);
    }

    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_view_page_item, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindView(BannerViewHolder holder, TopicImage data, int position, int size) {

        holder.showHighlighterView.init(data);
        int typeRes = Constants.getTypeNameRes(data.getType());
        holder.hintTv.setText("IMAGE : "+ size + "/" + (position+1) +"    TYPE : " + holder.itemView.getContext().getString(typeRes));
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {

        ShowHighlighterView showHighlighterView;
        TextView hintTv;

        BannerViewHolder(View view) {
            super(view);
            this.showHighlighterView = view.findViewById(R.id.see_paints_click);
            this.hintTv = view.findViewById(R.id.hint_topic_image);
        }
    }
}
