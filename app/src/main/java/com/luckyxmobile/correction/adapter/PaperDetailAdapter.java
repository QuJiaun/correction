package com.luckyxmobile.correction.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.model.BeanUtils;
import com.luckyxmobile.correction.model.bean.Paper;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.ui.activity.TopicInfoActivity;
import com.luckyxmobile.correction.utils.ImageTask;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PaperDetailAdapter extends RecyclerView.Adapter<PaperDetailAdapter.ViewHolder> implements ItemTouchAdapter {

    private Paper curPaper;
    private List<Topic> topicList = new ArrayList<>();

    public void refreshCurPaper(int paperId) {
        this.curPaper = LitePal.find(Paper.class, paperId);
        topicList = new ArrayList<>();
        for (int topicId : curPaper.getTopicSet()){
            Topic topic = LitePal.find(Topic.class, topicId);
            if (topic != null) {
                topicList.add(topic);
            } else {
                curPaper.getTopicSet().remove(topicId);
            }
        }
        curPaper.save();
    }

    public List<Topic> getTopicList() {
        return topicList;
    }

    @NonNull
    @Override
    public PaperDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item_paper_topic, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PaperDetailAdapter.ViewHolder holder, final int position) {

        Topic topic = topicList.get(position);

        TopicImage topicImage = BeanUtils.findTopicImageFirst(topic);

        ImageTask.getInstance().loadTopicImage(holder.topicImageIv, topicImage);

        holder.topicIndexTv.setText(String.valueOf(position+1));

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), TopicInfoActivity.class);
            intent.putExtra(Constants.TOPIC_ID, topic.getId());
            view.getContext().startActivity(intent);
        });

        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                int holderPosition = holder.getAdapterPosition();
                int index = 0;
                if (holderPosition == fromPosition) {
                    index = toPosition + 1;
                } else {
                    if (holderPosition <= toPosition && holderPosition > fromPosition) {
                        index = holderPosition;
                    } else if (holderPosition < fromPosition && holderPosition >= toPosition) {
                        index = holderPosition + 2;
                    }
                }
                if (index > 0) {
                    holder.topicIndexTv.setText(String.valueOf(index));
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(topicList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        topicList.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 加载布局的Holder
     */
    static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        ImageView topicImageIv;
        TextView topicIndexTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            topicImageIv = itemView.findViewById(R.id.topic_image);
            topicIndexTv = itemView.findViewById(R.id.topic_index);

        }

        @Override
        public void onItemSelected() {
            itemView.setTranslationZ(10);
            itemView.setAlpha(0.9f);
        }

        @Override
        public void onItemClear() {
            itemView.setTranslationZ(0);
            itemView.setAlpha(1);
        }
    }
}
