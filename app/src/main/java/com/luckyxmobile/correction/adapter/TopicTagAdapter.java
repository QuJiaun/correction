package com.luckyxmobile.correction.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Tag;
import com.luckyxmobile.correction.model.bean.Topic;
import com.zhy.view.flowlayout.FlowLayout;

import java.util.List;

import androidx.annotation.ColorInt;

public class TopicTagAdapter extends com.zhy.view.flowlayout.TagAdapter<Tag> {

    private boolean isClickable = false;
    private boolean isShowUnchecked = false;
    private int textColor = 0;
    private final Topic currentTopic;

    public TopicTagAdapter(List<Tag> datas, Topic topic) {
        super(datas);
        this.currentTopic = topic;
    }

    public void setClickable(boolean clickable) {
        isClickable = clickable;
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
        checkBox.setClickable(isClickable);
        if (textColor != 0) {
            checkBox.setText(textColor);
        }
        checkBox.setText(tag.getTag_name());
        checkBox.setChecked(tag.getTopicSet().contains(currentTopic));
        if (!isShowUnchecked && !checkBox.isChecked()) {
            checkBox.setVisibility(View.GONE);
        }
        return checkBox;
    }
}
