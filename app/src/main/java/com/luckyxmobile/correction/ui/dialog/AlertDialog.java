package com.luckyxmobile.correction.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.luckyxmobile.correction.R;

public class AlertDialog extends CommonDialog{
    public AlertDialog(@NonNull Context context) {
        super(context);
    }

    private TextView alertContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alertContent = findViewById(R.id.alertContent);
    }

    public void setMessage(String text) {
        alertContent.setText(text);
    }

    public  void setMessage(int textId) {
        alertContent.setText(textId);
    }

    @Override
    public int getDialogContentLayout() {
        return R.layout.dialog_alert;
    }
}
