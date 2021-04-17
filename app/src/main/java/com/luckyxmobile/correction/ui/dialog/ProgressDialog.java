package com.luckyxmobile.correction.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.utils.ThreadUtils;

import java.util.concurrent.ExecutorService;

public class ProgressDialog{

    private AlertDialog mAlertDialog;
    private TextView tvTip;

    private Handler handler;
    private ExecutorService service;

    public ProgressDialog(Context context) {
        init(context, null);
    }

    private void init(Context context, String tips) {
        View loadView = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null);
        tvTip = loadView.findViewById(R.id.tvTip);
        if (TextUtils.isEmpty(tips)) {
            tvTip.setText(R.string.loading);
        } else {
            tvTip.setText(tips);
        }
        mAlertDialog = new AlertDialog.Builder(context, R.style.progress_dialog_style).create();
        mAlertDialog.setView(loadView, 0, 0, 0, 0);
        mAlertDialog.setCanceledOnTouchOutside(false);

        handler = ThreadUtils.getInstance().getHandler();
        service = ThreadUtils.getInstance().getService();
    }

    @UiThread
    public void start() {
        if (mAlertDialog != null && !mAlertDialog.isShowing()) {
            if (onPreExecute()) {
                mAlertDialog.show();
                service.execute(() -> {
                    try {
                        boolean result = doInBackground();
                        handler.post(()->onPostExecute(result));
                    }catch (Exception e) {
                        handler.post(()->onPostExecute(false));
                    }
                });
            }
        }
    }

    @UiThread
    public void dismiss() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            handler.post(() -> mAlertDialog.dismiss());
        }
    }

    @MainThread
    public boolean onPreExecute() {
        return true;
    }

    @WorkerThread
    public boolean doInBackground() throws Exception{
        return true;
    }

    @MainThread
    public void onPostExecute(boolean result) {
        dismiss();
    }
}
