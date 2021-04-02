package com.luckyxmobile.correction.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.luckyxmobile.correction.R;
import com.zhy.view.flowlayout.FlowLayout;

import java.util.List;

public class TextTagAdapter extends com.zhy.view.flowlayout.TagAdapter<String>{

    int position = 0;

    public TextTagAdapter(List<String> array) {
        super(array);
    }

    @Override
    public View getView(FlowLayout parent, int position, String text) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_item_tag, parent, false);

        CheckBox checkBox = (CheckBox) view;
        checkBox.setText(text);
        checkBox.setChecked(this.position == position);

        checkBox.setTextColor(this.position == position?
                Color.WHITE : parent.getContext().getColor(R.color.text_unchecked));

        return checkBox;
    }

    public void setCurPosition(int position) {
        this.position = position;
    }
}
