package com.luckyxmobile.correction.ui.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.ImageParam;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.ui.views.CheckMenuItemView;
import com.luckyxmobile.correction.utils.BitmapUtils;
import com.luckyxmobile.correction.utils.GsonUtils;
import com.luckyxmobile.correction.utils.OpenCVUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class setImageParamDialog {

    private BottomSheetDialog dialog;
    private OnDialogListener listener;

    private OpenCVUtil openCVUtil = OpenCVUtil.getInstance();
    private ImageParam param;

    private CheckBox noise;
    private CheckBox shadow;
    private TextView multiplyHint;
    private SeekBar multiply;

    public setImageParamDialog(Context context, TopicImage topicImage) {
        dialog = new BottomSheetDialog(context);
        this.listener = (OnDialogListener) context;
        param = GsonUtils.json2Obj(topicImage.getImageParam(), ImageParam.class);
        if (param == null) {
            param = new ImageParam();
        }

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_set_image_param, null);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(view.getWidth(), view.getHeight());
        FrameLayout bottom = dialog.findViewById(R.id.design_bottom_sheet);
        if (bottom != null) {
            bottom.setBackgroundResource(android.R.color.transparent);
        }

        view.findViewById(R.id.cancel_btn).setOnClickListener(view1 -> dialog.dismiss());
        view.findViewById(R.id.ensure_btn).setOnClickListener(view12 -> {
            listener.onEnsure(param);
            dialog.dismiss();
        });

        noise = view.findViewById(R.id.noise);
        shadow = view.findViewById(R.id.shadow);
        multiply = view.findViewById(R.id.multiply);
        multiplyHint= view.findViewById(R.id.multiplyHint);

        noise.setChecked(param.medianBlur);
        shadow.setChecked(param.adaptiveThreshold);
        multiply.setProgress((int) (param.multiply*10));
        multiplyHint.setText(param.multiply+"");

        noise.setOnCheckedChangeListener((compoundButton, b) -> {
            param.medianBlur = b;
        });

        shadow.setOnCheckedChangeListener((compoundButton, b) -> {
            param.adaptiveThreshold = b;
        });

        multiply.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                param.multiply = div(i,10);
                multiplyHint.setText(param.multiply+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public double div(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, 1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    public BottomSheetDialog getDialog() {
        return dialog;
    }

    public interface OnDialogListener{
        void onEnsure(ImageParam param);
    }
}
