package com.luckyxmobile.correction.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.luckyxmobile.correction.R;

public abstract class CommonBottomDialog extends BottomSheetDialog {

    public CommonBottomDialog(@NonNull Context context) {
        super(context, R.style.CustomBottomDialog);
    }

    private FrameLayout dialogContent;
    private TextView titleTv;
    private TextView hintTv;
    private ImageButton negativeButton;
    private ImageButton positiveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_bottom_dialog);
        setCanceledOnTouchOutside(true);

        //使dialog宽度按设置的宽度显示,高度自适应
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        dialogContent = findViewById(R.id.dialogContent);
        titleTv = findViewById(R.id.dialogTitle);
        hintTv = findViewById(R.id.dialogHint);
        negativeButton = findViewById(R.id.negativeButton);
        positiveButton = findViewById(R.id.positiveButton);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        View contentView = LayoutInflater.from(getContext()).inflate(getDialogContentLayout(), null);
        dialogContent.addView(contentView, layoutParams);
    }

    public abstract int getDialogContentLayout();

    @Override
    public void setTitle(int titleId) {
        titleTv.setVisibility(View.VISIBLE);
        titleTv.setText(titleId);
    }

    @Override
    public void setTitle(CharSequence title) {
        titleTv.setVisibility(View.VISIBLE);
        titleTv.setText(title);
    }

    public void setPositiveButton(Runnable runnable) {
        positiveButton.setVisibility(View.VISIBLE);
        positiveButton.setOnClickListener(view -> runnable.run());
    }

    public void setNegativeButton(Runnable runnable) {
        negativeButton.setVisibility(View.VISIBLE);
        negativeButton.setOnClickListener(view -> runnable.run());
    }

    public void setDialogHint(String hint) {
        hintTv.setVisibility(View.VISIBLE);
        hintTv.setText(hint);
    }
}
