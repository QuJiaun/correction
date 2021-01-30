package com.luckyxmobile.correction.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.utils.OpenCVUtil;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopicInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final Context context;
    private boolean isShowOriginalImage = false;
    private final TopicInfoListener listener;
    private final List<Integer> typeList = new ArrayList<>();
    private final Map<Integer, List<TopicImage>> topicMap = new LinkedHashMap<>();

    private boolean isEdit = false;

    public TopicInfoAdapter(Context context, List<TopicImage> imageList) {
        this.context = context;
        this.listener = (TopicInfoListener) context;

        for (TopicImage topicImage : imageList) {
            int type = topicImage.getType();

            if (!topicMap.containsKey(type)) {
                topicMap.put(type, new ArrayList<>());
            }
            topicMap.get(type).add(topicImage);
        }
    }

    public void setShowOriginalImage(boolean showOriginalImage) {
        isShowOriginalImage = showOriginalImage;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType > 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item_topic_images, parent, false);
            return new TopicInfoHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item_topic_label, parent, false);
            return new TopicLabelHolder(view);
        }
    }

    int currentType;

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = typeList.get(position);

        if (type > 0) {
            currentType = type;
            TopicLabelHolder viewHolder = (TopicLabelHolder) holder;
            viewHolder.topicLabelTv.setText(context.getString(type));
        } else {
            TopicImage topicImage = topicMap.get(currentType).get(-type);

            TopicInfoHolder viewHolder = (TopicInfoHolder) holder;
            if (isShowOriginalImage) {
                Glide.with(context).load(topicImage.getPath()).into(viewHolder.topicIv);
            } else {
                Glide.with(context).load(
                        OpenCVUtil.setImageContrastRadioByPath(
                                topicImage.getContrast_radio(), topicImage.getPath()
                        )
                ).into(viewHolder.topicIv);
            }


            viewHolder.removeTopicBtn.setVisibility(isEdit?View.VISIBLE:View.GONE);

            viewHolder.removeTopicBtn.setOnClickListener(view -> {
                notifyItemRemoved(position);
                listener.removeTopicImage(topicImage);
            });

            viewHolder.itemView.setOnClickListener(view -> listener.onClickTopicImage(topicImage));
        }
    }

    public void addTopicImage(TopicImage topicImage) {
        int type = topicImage.getType();

        if (!topicMap.containsKey(type)) {
            topicMap.put(type, new ArrayList<>());
        }

        topicMap.get(type).add(topicImage);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return typeList.get(position);
    }

    @Override
    public int getItemCount() {
        int count = topicMap.size();

        typeList.clear();

        for (int key : topicMap.keySet()) {
            typeList.add(key);


            int size = topicMap.get(key).size();
            count += size;

            for (int i = 0; i < size; i++) {
                typeList.add(-i);
            }
        }
        return count;
    }

    public static class TopicLabelHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_topic_label) TextView topicLabelTv;

        TopicLabelHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class TopicInfoHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_topic_image) ImageView topicIv;
        @BindView(R.id.item_remove_topic_image) ImageView removeTopicBtn;

        TopicInfoHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface TopicInfoListener{
        void removeTopicImage(TopicImage topicImage);
        void onClickTopicImage(TopicImage topicImage);
    }
}
