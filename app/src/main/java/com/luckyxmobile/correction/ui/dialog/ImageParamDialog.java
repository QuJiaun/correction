package com.luckyxmobile.correction.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.ImageParam;

import java.math.BigDecimal;

public class ImageParamDialog extends CommonBottomDialog {
    public ImageParamDialog(@NonNull Context context) {
        super(context);
    }

    private CheckBox noise;
    private CheckBox shadow;
    private TextView multiplyHint;
    private SeekBar multiply;
    private TextView brightnessHint;
    private SeekBar brightness;

    private ImageParam param;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        noise = findViewById(R.id.noise);
        shadow = findViewById(R.id.shadow);
        multiply = findViewById(R.id.multiply);
        multiplyHint= findViewById(R.id.multiplyHint);
        brightness = findViewById(R.id.brightness);
        brightnessHint = findViewById(R.id.brightnessHint);

        noise.setOnCheckedChangeListener((compoundButton, b) -> param.medianBlur = b);

        shadow.setOnCheckedChangeListener((compoundButton, b) -> param.adaptiveThreshold = b);

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

        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                param.brightness = div(i-10,10);
                brightnessHint.setText(param.brightness+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        setNegativeButton(this::dismiss);
        setTitle("参数");
    }

    public ImageParam getImageParam() {
        return param;
    }

    public void setImageParam(ImageParam param) {
        noise.setChecked(param.medianBlur);
        shadow.setChecked(param.adaptiveThreshold);
        multiply.setProgress((int) (param.multiply*10));
        multiplyHint.setText(param.multiply+"");
        brightness.setProgress((int)(param.brightness*10 + 10));
        brightnessHint.setText(param.brightness+"");

        this.param = param;
    }

    private double div(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, 1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    @Override
    public int getDialogContentLayout() {
        return R.layout.dialog_set_image_param;
    }
}
