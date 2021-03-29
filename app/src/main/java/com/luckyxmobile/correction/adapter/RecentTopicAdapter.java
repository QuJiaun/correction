package com.luckyxmobile.correction.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.model.BeanUtils;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.ui.activity.TopicInfoActivity;
import com.luckyxmobile.correction.utils.ImageTask;
import com.luckyxmobile.correction.utils.FilesUtils;


import org.litepal.LitePal;

import java.util.List;

public class RecentTopicAdapter extends RecyclerView.Adapter<ViewHolderTopicItem> {

    private final List<Topic> topics;
    private final Context context;

    public RecentTopicAdapter(Context context, List<Topic> topics){

        this.context = context;
        this.topics = topics;
    }
    @NonNull
    @Override
    public ViewHolderTopicItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_item_topic, parent, false);
        return new ViewHolderTopicItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTopicItem holder, int position) {


        Topic topic = topics.get(position);

        TopicImage topicImage = BeanUtils.findTopicImageFirst(topic);

        ImageTask.getInstance().loadTopicImage(holder.topicImage, topicImage);

        holder.collectBtn.setVisibility(topic.isCollection()?View.VISIBLE:View.GONE);

        holder.tagTv.setText(LitePal.find(Book.class, topic.getBook_id()).getName());

        holder.topicDate.setText(FilesUtils.getTimeByDate(topic.getCreate_date()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TopicInfoActivity.class);
            intent.putExtra(Constants.TOPIC_ID, topic.getId());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {

        return topics.size();
    }

}
