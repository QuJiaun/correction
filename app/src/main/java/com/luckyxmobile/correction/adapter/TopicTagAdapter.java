package com.luckyxmobile.correction.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Tag;
import com.luckyxmobile.correction.model.bean.Topic;
import com.zhy.view.flowlayout.FlowLayout;

import androidx.annotation.ColorInt;

import org.litepal.LitePal;

public class TopicTagAdapter extends com.zhy.view.flowlayout.TagAdapter<Tag> {

    private final OnTagClickListener listener;
    private boolean isItemClickable = false;
    private boolean isShowUnchecked = false;
    private int textColor = 0;
    private int curTopicId;

    public TopicTagAdapter(OnTagClickListener listener) {
        super(LitePal.findAll(Tag.class));
        this.listener = listener;
    }

    public void setCurTopicId(int curTopicId) {
        this.curTopicId = curTopicId;
    }

    public void setItemClickable(boolean itemClickable) {
        isItemClickable = itemClickable;
    }

    public void setShowUnchecked(boolean showUnchecked) {
        isShowUnchecked = showUnchecked;
    }

    public void setTextColor(@ColorInt int textColor) {
        this.textColor = textColor;
    }

    @Override
    public View getView(FlowLayout parent, int position, Tag tag) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_item_tag, parent, false);

        CheckBox checkBox = (CheckBox) view;
        checkBox.setClickable(isItemClickable);
        if (isItemClickable && listener != null) {
            checkBox.setOnClickListener(view1 -> listener.onTagClick(curTopicId, tag));
        }
        if (textColor != 0) {
            checkBox.setTextColor(textColor);
        }
        checkBox.setText(tag.getTag_name());
        checkBox.setChecked(tag.getTopicSet().contains(curTopicId));
        if (!isShowUnchecked && !checkBox.isChecked()) {
            checkBox.setVisibility(View.GONE);
        }
        return checkBox;
    }

    public interface OnTagClickListener {
        void onTagClick(int topicId, Tag tag);
    }
}
