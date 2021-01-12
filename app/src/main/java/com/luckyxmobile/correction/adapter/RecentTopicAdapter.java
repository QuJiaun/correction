package com.luckyxmobile.correction.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.ui.activity.MainActivity;
import com.luckyxmobile.correction.ui.activity.TopicInfoActivity;
import com.luckyxmobile.correction.ui.activity.TopicViewPageActivity;
import com.luckyxmobile.correction.utils.ConstantsUtil;
import com.luckyxmobile.correction.utils.ImageUtil;


import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class RecentTopicAdapter extends RecyclerView.Adapter<RecentTopicAdapter.ViewHolder> {

    private final List<Topic> topics;
    private final Context context;

    public RecentTopicAdapter(Context context, List<Topic> topics){

        this.context = context;
        this.topics = topics;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_item_recent_topic, parent, false);
        return new RecentTopicAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

//        Glide.with(context)
//                .load()
//                .into();

        holder.bookName.setText(topics.get(position).getBook_id());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO --跳转到TopicInfoActivity
            }
        });

    }

    @Override
    public int getItemCount() {

        return topics.size();
    }

    public void deleteTopic(int book_id){
        if (book_id != -1){
            Iterator<Topic> topicIterator = topics.iterator();
            while (topicIterator.hasNext()) {
                Topic topic = topicIterator.next();
                if (topic.getBook_id() == book_id){
                    topicIterator.remove();
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView topicImage;
        TextView bookName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            topicImage = itemView.findViewById(R.id.iv_recent_topic_image);
            bookName = itemView.findViewById(R.id.recent_topic_book_name);

        }
    }



}
