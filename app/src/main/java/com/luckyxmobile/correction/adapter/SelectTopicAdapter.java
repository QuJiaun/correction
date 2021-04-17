package com.luckyxmobile.correction.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.BeanUtils;
import com.luckyxmobile.correction.model.bean.Paper;
import com.luckyxmobile.correction.model.bean.Tag;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.zj.myfilter.FiltrateBean;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class SelectTopicAdapter extends RecyclerView.Adapter<SelectTopicAdapter.ViewHolder> {

    private final Paper paper;

    private List<Topic> filterTopicList;
    private List<Topic> topicList;

    public SelectTopicAdapter(Paper paper) {
        this.paper = paper;
        filterTopicList = LitePal.findAll(Topic.class);
        topicList = LitePal.findAll(Topic.class);
    }

    public boolean isEmpty() {
        return filterTopicList.isEmpty();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item_topic_select, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder viewHolder, final int position) {

        Topic curTopic = filterTopicList.get(position);

        boolean isChecked = paper.getTopicSet().contains(curTopic.getId());

        setViewChecked(viewHolder, isChecked);

        TopicImage topicImage = BeanUtils.findTopicImageFirst(curTopic);

        Glide.with(viewHolder.itemView.getContext())
                .load(topicImage.getPath())
                .into(viewHolder.imageView);

        viewHolder.itemView.setOnClickListener(view -> {
            if (paper.getTopicSet().contains(curTopic.getId())) {
                paper.getTopicSet().remove(curTopic.getId());
                setViewChecked(viewHolder, false);
            } else {
                paper.getTopicSet().add(curTopic.getId());
                setViewChecked(viewHolder, true);
            }
        });

    }

    private void setViewChecked(ViewHolder viewHolder, boolean isChecked) {
        Context context = viewHolder.itemView.getContext();
        Drawable drawable = context.getDrawable(R.drawable.ic_checked);
        if (isChecked) {
            drawable.setTint(context.getColor(R.color.item_checked));
            viewHolder.itemView.setBackgroundResource(R.drawable.shape_box_check_bg);
        } else {
            drawable.setTint(context.getColor(R.color.item_checked_un));
            viewHolder.itemView.setBackgroundResource(R.drawable.shape_box_view);
        }
        viewHolder.checkedIv.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return filterTopicList.size();
    }

    public void onFilterListener(List<FiltrateBean> filtrateList) {

        List<Topic> tmp = new ArrayList<>();
        boolean isNullChecked = true;
        for (int i = 0; i < filtrateList.size(); i++) {
            FiltrateBean filtrateBean = filtrateList.get(i);
            for (FiltrateBean.Children children: filtrateBean.getChildren()) {
                //循环topic
                for (Topic topic : topicList) {
                    //已经包含了 跳出
                    if (tmp.contains(topic)) continue;
                    //选中的必须加入
                    if (paper.getTopicSet().contains(topic.getId())) {
                        tmp.add(topic);
                        continue;
                    }
                    //没有选中直接跳出
                    if (!children.isSelected()) continue;
                    isNullChecked = false;
                    if (i == 0) {
                        if (children.getId() == 1 && topic.isCollection()) {
                            tmp.add(topic);
                        } else if (children.getId() == topic.getBook_id()) {
                            tmp.add(topic);
                        }
                    } else if (i == 1) {
                        Tag tag = LitePal.find(Tag.class, children.getId());
                        if (tag.getTopicSet().contains(topic.getId())) {
                            tmp.add(topic);
                        }
                    }
                }
            }
        }

        if (isNullChecked) {
            filterTopicList = topicList;
        } else {
            filterTopicList = tmp;
        }

        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView checkedIv;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.topic_image);
            checkedIv = itemView.findViewById(R.id.item_Checked);
        }
    }
}
