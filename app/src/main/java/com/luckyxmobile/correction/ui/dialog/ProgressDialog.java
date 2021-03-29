package com.luckyxmobile.correction.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.luckyxmobile.correction.R;

public class ProgressDialog {

    private ProgressDialog() { }

    private static final ProgressDialog progressDialog = new ProgressDialog();

    public static ProgressDialog getInstance() {
        return progressDialog;
    }

    private AlertDialog mAlertDialog;
    private TextView tvTip;

    public ProgressDialog init(Context context) {
        View loadView = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null);
        tvTip = loadView.findViewById(R.id.tvTip);
        tvTip.setText(R.string.loading);

        mAlertDialog = new AlertDialog.Builder(context, R.style.progress_dialog_style).create();
        mAlertDialog.setView(loadView, 0, 0, 0, 0);
        mAlertDialog.setCanceledOnTouchOutside(false);
        return this;
    }


    public void show() {
        if (mAlertDialog != null) {
            mAlertDialog.show();
        }
    }

    public void show(String tip) {
        if (mAlertDialog != null) {
            if (!TextUtils.isEmpty(tip)) {
                tvTip.setText(tip);
            }
            show();
        }
    }

    public void dismiss() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}
