package com.luckyxmobile.correction.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Tag;

public class TagDialog extends CommonDialog{

    public TagDialog(@NonNull Context context) {
        super(context);
    }

    private EditText tagNameEt;
    private Tag tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tagNameEt = findViewById(R.id.tagNameEdt);
        setNegativeButton(R.string.cancel, this::dismiss);
        setTitle(R.string.tags);
    }

    public void setTag(Tag tag) {
        this.tag = tag==null?new Tag(null):tag;

        String tagName = this.tag.getTag_name();
        if (TextUtils.isEmpty(tagName)) {
            tagNameEt.setText(null);
            tagNameEt.setSelection(0);
        } else {
            tagNameEt.setText(tagName);
            tagNameEt.setSelection(tagName.length());
        }

    }

    public Tag getTag() {
        tag.setTag_name(tagNameEt.getText().toString());
        return tag;
    }

    @Override
    public int getDialogContentLayout() {
        return R.layout.dialog_add_tag;
    }
}
