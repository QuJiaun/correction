package com.luckyxmobile.correction.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.luckyxmobile.correction.R;

public class SetTopicTextDialog extends AlertDialog.Builder {

    private EditText editText;
    private OnBtnClickListener listener;

    public SetTopicTextDialog(Context context) {
        super(context);
        listener = (OnBtnClickListener) context;

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_set_topic_text, null);
        setView(view);

        editText = view.findViewById(R.id.topic_text_et);

        setPositiveButton(R.string.ensure, (dialog, which) ->
            listener.onTopicTextBtnEnsure(editText.getText().toString())
        );

        setNegativeButton(R.string.cancel, null);
    }

    public void init(String text) {
        if (text != null) {
            editText.setText(text);
            editText.setSelection(text.length());
        }
    }

    public interface OnBtnClickListener {
        void onTopicTextBtnEnsure(String text);
    }
}
