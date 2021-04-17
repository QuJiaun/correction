package com.luckyxmobile.correction.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.luckyxmobile.correction.R;

public abstract class CommonDialog extends Dialog {
    public CommonDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
    }

    private FrameLayout dialogContent;
    private TextView titleTv;
    private TextView hintTv;
    private Button negativeButton;
    private Button positiveButton;
    private Button neutralButton;

    private Animation inAboveAnim, outAboveAnim;

    private Runnable hideHint = () -> {
        hintTv.startAnimation(outAboveAnim);
        hintTv.setVisibility(View.GONE);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog);
        setCanceledOnTouchOutside(false);

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
        neutralButton = findViewById(R.id.neutralButton);
        inAboveAnim = AnimationUtils.loadAnimation(getContext(), R.anim.layout_in_above);
        outAboveAnim = AnimationUtils.loadAnimation(getContext(), R.anim.layout_out_above);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        View contentView = LayoutInflater.from(getContext()).inflate(getDialogContentLayout(), null);
        dialogContent.addView(contentView, layoutParams);
    }

    public abstract int getDialogContentLayout();

    @Override
    public void setTitle(@Nullable CharSequence title) {
        titleTv.setVisibility(View.VISIBLE);
        titleTv.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        titleTv.setVisibility(View.VISIBLE);
        titleTv.setText(titleId);
    }


    public void setPositiveButton(int textId, Runnable runnable) {
        positiveButton.setVisibility(View.VISIBLE);
        positiveButton.setText(textId);
        positiveButton.setOnClickListener(view -> runnable.run());
    }

    public void setNegativeButton(int textId, Runnable runnable) {
        negativeButton.setVisibility(View.VISIBLE);
        negativeButton.setText(textId);
        negativeButton.setOnClickListener(view -> runnable.run());
    }

    public void setNeutralButton(int textId, Runnable runnable) {
        neutralButton.setVisibility(View.VISIBLE);
        neutralButton.setText(textId);
        neutralButton.setOnClickListener(view -> runnable.run());
    }

    public void onToast(@StringRes int textId) {
        onToast(getContext().getString(textId));
    }

    public void onToast(String log) {
        Toast.makeText(getContext(), log, Toast.LENGTH_SHORT).show();
    }

    public void onError(@StringRes int textId) {
        onError(getContext().getString(textId));
    }

    public void onError(String error) {
        hintTv.setText(error);
        if (hintTv.getVisibility() != View.VISIBLE) {
            hintTv.setVisibility(View.VISIBLE);
            hintTv.startAnimation(inAboveAnim);
        }
        hintTv.removeCallbacks(hideHint);
        hintTv.postDelayed(hideHint, 1500);
    }

}
