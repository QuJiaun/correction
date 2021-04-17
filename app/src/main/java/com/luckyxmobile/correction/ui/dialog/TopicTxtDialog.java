package com.luckyxmobile.correction.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.luckyxmobile.correction.R;

public class TopicTxtDialog extends CommonDialog{
    public TopicTxtDialog(@NonNull Context context) {
        super(context);
    }

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editText = findViewById(R.id.topic_text_et);

        setTitle(R.string.add_text);
        setNegativeButton(R.string.cancel, this::dismiss);
        setNeutralButton(R.string.clear, () -> {
            editText.setText(null);
            editText.setSelection(0);
            onToast("已清空");
        });
    }

    public void setTxt(String txt) {
        if (!TextUtils.isEmpty(txt)) {
            editText.setText(txt);
            editText.setSelection(txt.length());
        }
    }

    public String getTxt() {
        return editText.getText().toString();
    }

    @Override
    public int getDialogContentLayout() {
        return R.layout.dialog_set_topic_text;
    }
}
