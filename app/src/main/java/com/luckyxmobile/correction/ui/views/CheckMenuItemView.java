package com.luckyxmobile.correction.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.luckyxmobile.correction.R;

public class CheckMenuItemView extends LinearLayout{

    private boolean isChecked;
    Drawable checkedImg, uncheckedImg;
    int checkedColor, uncheckedColor;
    ImageView iconIv;
    TextView menuTv;

    public CheckMenuItemView(Context context) {
        super(context);
    }

    public CheckMenuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(R.layout.view_check_menu,this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckMenuItemView);

        checkedImg = typedArray.getDrawable(R.styleable.CheckMenuItemView_checked_img);
        uncheckedImg = typedArray.getDrawable(R.styleable.CheckMenuItemView_unchecked_img);
        checkedColor = typedArray.getColor(R.styleable.CheckMenuItemView_checked_text_color,getContext().getColor(R.color.checked_menu_text));
        uncheckedColor = typedArray.getColor(R.styleable.CheckMenuItemView_unchecked_text_color,getContext().getColor(R.color.unchecked_menu));

        String text = typedArray.getString(R.styleable.CheckMenuItemView_menu_text);
        isChecked = typedArray.getBoolean(R.styleable.CheckMenuItemView_default_checked, false);

        iconIv = view.findViewById(R.id.check_menu_icon);
        menuTv = view.findViewById(R.id.check_menu_text);

        if (text == null || text.isEmpty()) {
            menuTv.setVisibility(GONE);
        } else {
            menuTv.setText(text);
            menuTv.setTextColor(isChecked?checkedColor:uncheckedColor);
        }

        setChecked(isChecked);

        typedArray.recycle();
    }

    public void setCheckedImg(Drawable checkedImg) {
        this.checkedImg = checkedImg;
    }

    public Drawable getCheckedImg() {
        return checkedImg;
    }

    public void setUncheckedImg(Drawable uncheckedImg) {
        this.uncheckedImg = uncheckedImg;
    }

    public void setCheckedColor(int checkedColor) {
        this.checkedColor = checkedColor;
    }

    public void setUncheckedColor(int uncheckedColor) {
        this.uncheckedColor = uncheckedColor;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {

        isChecked = checked;
        if (isChecked || uncheckedImg == null) {
            iconIv.setImageDrawable(checkedImg);
        } else {
            iconIv.setImageDrawable(uncheckedImg);
        }
        menuTv.setTextColor(isChecked?checkedColor:uncheckedColor);
        invalidate();
    }
}
