package com.luckyxmobile.correction.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.BeanUtils;
import com.luckyxmobile.correction.model.bean.Tag;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.utils.ImageTask;
import com.luckyxmobile.correction.utils.FilesUtils;

import java.util.ArrayList;
import java.util.List;


public class BookDetailAdapter extends RecyclerView.Adapter<ViewHolderTopicItem> {

    private String TAG = "BookDetailAdapter";
    private final Context mContext;
    private List<Topic> topics;
    private List<Topic> filterTopics;
    private List<Topic> topicsByDelete;

    private MenuItem deleteMenuItem;
    private boolean isDeleteMode = false;
    private ViewHolderTopicItem.OnItemListener listener;

    public BookDetailAdapter(Context context, List<Topic> topics){
        this.mContext = context;
        this.listener = (ViewHolderTopicItem.OnItemListener) context;
        this.topics = topics;
        this.filterTopics = new ArrayList<>(topics);
        this.topicsByDelete = new ArrayList<>();
    }

    public List<Topic> getTopicsByDelete() {
        return topicsByDelete;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void removeTopicList() {

        if (topicsByDelete.isEmpty()) return;

        topics.removeAll(topicsByDelete);
        topicsByDelete.clear();

        setDeleteMode(false);
    }

    public void setDeleteMenuItem(MenuItem deleteMenuItem) {
        this.deleteMenuItem = deleteMenuItem;
    }

    public void setDeleteMode(boolean deleteMode) {

        isDeleteMode = deleteMode;
        deleteMenuItem.setVisible(isDeleteMode);
        topicsByDelete.clear();
        notifyDataSetChanged();
    }

    public boolean isDeleteMode() {
        return isDeleteMode;
    }

    public void onFilterListener(List<Tag> tagList) {
        if (tagList == null) {
            topics = filterTopics;
            notifyDataSetChanged();
            return;
        }

        List<Topic> tmp = new ArrayList<>();
        for (Topic topic : filterTopics) {
            if (!tmp.contains(topic)) {
                for (Tag tag :tagList) {
                    if (tag.getTopicSet().contains(topic.getId())) {
                        tmp.add(topic);
                        break;
                    }
                }
            }
        }

        topics = tmp;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderTopicItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(mContext).inflate( R.layout.recycle_item_topic, parent, false);
        return new ViewHolderTopicItem(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderTopicItem holder, final int position) {
        // 获取错题对象
        Topic topic = topics.get(position);
        TopicImage topicImage = BeanUtils.findTopicImageFirst(topic);

        ImageTask.getInstance().loadTopicImage(holder.topicImage, topicImage);

        holder.topicDate.setText(FilesUtils.getTimeByDate(topic.getCreate_date()));
        holder.collectBtn.setVisibility(topic.isCollection()? View.VISIBLE:View.GONE);
        holder.tagTv.setText(BeanUtils.tagsToString(topic.getId()));
        holder.tagTv.setTextColor(mContext.getColor(R.color.text_checked));

        if (isDeleteMode) {
            holder.checkBtn.setVisibility(View.VISIBLE);
            setLayoutView(topic, holder);

        }else {
            holder.checkBtn.setVisibility(View.GONE);
            holder.layout.setBackgroundResource(R.drawable.shape_box_view);
        }

        holder.layout.setOnLongClickListener(v -> {
            setDeleteMode(!isDeleteMode);
            if (isDeleteMode) {
                topicsByDelete.add(topic);
            }
            return true;
        });


        holder.layout.setOnClickListener(v -> {
            if (!isDeleteMode) {
                listener.onClickItem(topic);
            } else {
                if (topicsByDelete.contains(topic)) {
                    topicsByDelete.remove(topic);
                } else {
                    topicsByDelete.add(topic);
                }
                setLayoutView(topic, holder);
            }
        });

    }

    private void setLayoutView(Topic topic, ViewHolderTopicItem holder) {
        if (topicsByDelete.contains(topic)) {
            holder.checkBtn.setImageDrawable(mContext.getDrawable(R.drawable.topic_delete_check));
            holder.layout.setBackgroundResource(R.drawable.shape_box_check_delete_bg);
        }else{
            holder.checkBtn.setImageDrawable(mContext.getDrawable(R.drawable.item_uncheck));
            holder.layout.setBackgroundResource(R.drawable.shape_box_view);
        }
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

}
