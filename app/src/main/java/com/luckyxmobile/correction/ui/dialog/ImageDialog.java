package com.luckyxmobile.correction.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.utils.ImageTask;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ImageDialog extends Dialog  {

    public ImageDialog(@NonNull Context context) {
        super(context, R.style.CustomBottomDialog);
    }



    private Banner<TopicImage, TopicImageBannerAdapter> banner;
    private TopicImageBannerAdapter adapter;

    private boolean originalImage = true;
    private List<TopicImage> topicImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_banner);
        setCanceledOnTouchOutside(true);

        //使dialog宽度按设置的宽度显示,高度自适应
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);

        banner = findViewById(R.id.banner);
        adapter = new TopicImageBannerAdapter(topicImages);
        banner.setAdapter(adapter);

    }

    public void setTopicImages(List<TopicImage> topicImages) {
        this.topicImages = topicImages;
        adapter.setDatas(topicImages);
        adapter.notifyDataSetChanged();
    }

    public void setImage(TopicImage topicImage, boolean originalImage) {
        if (this.originalImage != originalImage) {
            this.originalImage = originalImage;
            adapter.notifyDataSetChanged();
        }
        int index = 0;
        for (int i = 0; i < topicImages.size(); i++) {
            if (topicImage.getId() == topicImages.get(i).getId()) {
                index = i;
                break;
            }
        }
        banner.setCurrentItem(index+1, false);
    }

    private class TopicImageBannerAdapter extends BannerAdapter<TopicImage, ViewHolder> {

        public TopicImageBannerAdapter(List<TopicImage> datas) {
            super(datas);
        }

        @Override
        public void setDatas(List<TopicImage> datas) {
            super.setDatas(datas);
        }

        @Override
        public ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.dailog_image, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindView(ViewHolder holder, TopicImage data, int position, int size) {
            if (originalImage) {
                Glide.with(getContext()).load(data.getPath()).into(holder.imageView);
            } else {
                ImageTask.getInstance().loadTopicImage(holder.imageView, data);
            }
            holder.hintTv.setText("IMAGE : "+ (position+1) +"/" + size +"\t\tTYPE : " +
                    getContext().getString(Constants.getTypeNameRes(data.getType())));
            holder.itemView.setOnClickListener(view -> dismiss());
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView hintTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            hintTv = itemView.findViewById(R.id.hintTv);
        }
    }
}
