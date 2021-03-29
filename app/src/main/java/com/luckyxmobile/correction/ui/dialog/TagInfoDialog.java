package com.luckyxmobile.correction.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Tag;

public class TagInfoDialog extends AlertDialog.Builder {

    private EditText tagNameEt;
    private Tag tag;

    public TagInfoDialog(Activity activity) {
        super(activity);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_tag, null);
        setView(view);

        tagNameEt = view.findViewById(R.id.tagNameEdt);

        setNegativeButton(R.string.cancel, null);
    }

    public void setTag(Tag tag) {
        if (tag == null) {
            tag = new Tag(null);
        }
        this.tag = tag;
        setTagNameEt();
    }

    public Tag getTag() {
        if (tag == null) {
            tag = new Tag(null);
        }
        tag.setTag_name(tagNameEt.getText().toString());
        return tag;
    }

    private void setTagNameEt() {
        if (tag.getTag_name() != null && !tag.getTag_name().isEmpty()) {
            tagNameEt.setText(tag.getTag_name());
            tagNameEt.setSelection(tag.getTag_name().length());
        }
    }

}


