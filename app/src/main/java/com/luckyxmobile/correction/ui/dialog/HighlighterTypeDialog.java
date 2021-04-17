package com.luckyxmobile.correction.ui.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.ui.views.CheckView;

import java.util.ArrayList;
import java.util.List;

public class HighlighterTypeDialog extends CommonBottomDialog implements View.OnClickListener {

    public HighlighterTypeDialog(@NonNull Context context) {
        super(context);
    }

    private List<CheckView> btnList = new ArrayList<>();
    private CheckView curChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btnList.add(findViewById(R.id.highlighter_blue));
        btnList.get(0).setTag(0x220);
        btnList.add(findViewById(R.id.highlighter_red));
        btnList.get(1).setTag(0x221);
        btnList.add(findViewById(R.id.highlighter_yellow));
        btnList.get(2).setTag(0x223);
        btnList.add(findViewById(R.id.highlighter_green));
        btnList.get(3).setTag(0x222);
        btnList.add(findViewById(R.id.highlighter_white));
        btnList.get(4).setTag(0x225);

        for (CheckView btn: btnList) {
            btn.setOnClickListener(this);
        }

        setNegativeButton(this::dismiss);
        setTitle("选择荧光笔");
    }

    public int getType() {
        return (int) curChecked.getTag();
    }

    public Drawable getIcon() {
        return curChecked.getCheckedImg();
    }

    public void setType(int type) {
        for (CheckView btn: btnList) {
            int tag = (int) btn.getTag();
            btn.setChecked(type==tag);
            if (btn.isChecked()) {
                curChecked = btn;
            }
        }
    }

    @Override
    public int getDialogContentLayout() {
        return R.layout.dialog_select_highlighter;
    }

    @Override
    public void onClick(View view) {
        setType((Integer) view.getTag());
    }
}
