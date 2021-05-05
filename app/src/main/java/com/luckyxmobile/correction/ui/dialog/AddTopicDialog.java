package com.luckyxmobile.correction.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.luckyxmobile.correction.R;

public class AddTopicDialog extends CommonDialog{

    public AddTopicDialog(@NonNull Context context) {
        super(context);
    }

    private Button fromCamera;
    private Button fromAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCanceledOnTouchOutside(true);
        fromCamera = findViewById(R.id.upload_topic_dialog_camera);
        fromAlbum = findViewById(R.id.upload_topic_dialog_album);
    }

    public void setFromCamera(Runnable runnable) {
        fromCamera.setOnClickListener(view -> runnable.run());
    }

    public void setFromAlbum(Runnable runnable) {
        fromAlbum.setOnClickListener(view -> runnable.run());
    }

    @Override
    public int getDialogContentLayout() {
        return R.layout.dialog_upload_topic;
    }
}
