package com.luckyxmobile.correction.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.BeanUtils;
import com.luckyxmobile.correction.model.bean.Tag;
import com.luckyxmobile.correction.model.bean.Topic;
import com.zhy.view.flowlayout.FlowLayout;

import androidx.annotation.ColorInt;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class TopicTagAdapter extends com.zhy.view.flowlayout.TagAdapter<Tag> {

    private boolean isItemClickable = false;
    private boolean isShowUnchecked = false;
    private int textColor = 0;
    private int curTopicId;
    private List<Integer> topicIdList;

    public TopicTagAdapter() {
        super(LitePal.findAll(Tag.class));
    }

    public void setCurTopicId(int curTopicId) {
        this.curTopicId = curTopicId;
    }

    public void setTopicIdList(List<Topic> topicList) {
        this.topicIdList = new ArrayList<>();
        for (Topic topic : topicList) {
            topicIdList.add(topic.getId());
        }
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
        if (textColor != 0) {
            checkBox.setTextColor(textColor);
        }
        checkBox.setText(tag.getTag_name());
        if (curTopicId >= 0) {
            checkBox.setChecked(tag.getTopicSet().contains(curTopicId));
        } else if (topicIdList != null && !topicIdList.isEmpty()) {
            checkBox.setVisibility(View.GONE);
            for (int id : topicIdList) {
                if (tag.getTopicSet().contains(id)) {
                    checkBox.setVisibility(View.VISIBLE);
                    break;
                }
            }

        }

        if (!isShowUnchecked && !checkBox.isChecked()) {
            checkBox.setVisibility(View.GONE);
        }
        return checkBox;
    }
}
