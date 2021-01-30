package com.luckyxmobile.correction.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.ui.views.ShowHighlighterView;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

public class TopicImageBannerAdapter extends BannerAdapter<TopicImage, TopicImageBannerAdapter.BannerViewHolder> {


    TopicImageBannerAdapter(List<TopicImage> topicImages) {
        super(topicImages);
    }

    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        ShowHighlighterView showHighlighterView = new ShowHighlighterView(parent.getContext());
        showHighlighterView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        return new BannerViewHolder(showHighlighterView);
    }

    @Override
    public void onBindView(BannerViewHolder holder, TopicImage data, int position, int size) {
        holder.view.init(data);
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {

        ShowHighlighterView view;

        BannerViewHolder(View view) {
            super(view);
            this.view = (ShowHighlighterView) view;
        }
    }
}
