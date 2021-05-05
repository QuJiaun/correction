package com.luckyxmobile.correction.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.google.android.material.textfield.TextInputLayout;
import com.luckyxmobile.correction.R;

public class EditTextDialog extends CommonDialog{

    public EditTextDialog(@NonNull Context context) {
        super(context);
    }

    private TextInputLayout textInputLayout;
    private EditText editText;

    private int maxLength = Integer.MAX_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textInputLayout = findViewById(R.id.text_input_layout);
        editText = findViewById(R.id.edit_text);
    }

    public String getText() {
        String result = editText.getText().toString();
        if (TextUtils.isEmpty(result)) {
            onError(R.string.empty_input);
        } else if (result.length() > maxLength) {
            onError(R.string.input_error);
        } else {
            return result;
        }
        return null;
    }

    public void setText(String text) {
        editText.setText(text);
        editText.setSelection(text==null?0:text.length());
    }

    public void setTextHint(@StringRes int textHint) {
        textInputLayout.setHint(getContext().getString(textHint));
    }

    public void setMaxLength(int length) {
        this.maxLength = length;
        textInputLayout.setCounterMaxLength(length);
    }

    @Override
    public int getDialogContentLayout() {
        return R.layout.dialog_edit_text;
    }
}
