package com.luckyxmobile.correction.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Topic;

public class ViewHolderTopicItem extends RecyclerView.ViewHolder{

    ConstraintLayout layout;
    ImageView topicImageView;
    ImageView collectBtn;
    ImageView checkBtn;
    TextView tagTv;
    TextView topicDate;

    public ViewHolderTopicItem(@NonNull View itemView) {
        super(itemView);

        layout = itemView.findViewById(R.id.item_topic_book_layout);
        topicImageView = itemView.findViewById(R.id.topic_image);
        collectBtn = itemView.findViewById(R.id.collect_button);
        checkBtn = itemView.findViewById(R.id.item_Checked);
        tagTv = itemView.findViewById(R.id.tag_layout_on_topic);
        topicDate = itemView.findViewById(R.id.topic_date);
    }

    /**
     * 点击事件监听接口
     */
    public interface OnItemListener {
        void onClickItem(Topic topic);
    }
}
