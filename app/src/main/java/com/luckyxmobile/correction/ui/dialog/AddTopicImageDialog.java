package com.luckyxmobile.correction.ui.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.luckyxmobile.correction.R;

import java.lang.ref.WeakReference;

public class AddTopicImageDialog {

    public static void show(Activity activity) {

        WeakReference<Activity> weakReference = new WeakReference<>(activity);

        activity = weakReference.get();

        if (activity == null) return;

        OnClickListener listener = (OnClickListener) activity;

        View view = LayoutInflater.from(activity).inflate(R.layout.upload_topic_dialog,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity).setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        Button uploadCamera = view.findViewById(R.id.upload_topic_dialog_camera);
        Button uploadAlbum = view.findViewById(R.id.upload_topic_dialog_album);

        uploadCamera.setOnClickListener(view1 ->{
            listener.addTopicFrom(false);
            dialog.dismiss();
        } );

        uploadAlbum.setOnClickListener(view2 ->{
            listener.addTopicFrom(true);
            dialog.dismiss();
        } );
    }

    public interface OnClickListener {
        void addTopicFrom(boolean album);
    }
}
