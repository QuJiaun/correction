package com.luckyxmobile.correction.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.luckyxmobile.correction.R;

public class HighlighterWidthDialog extends CommonBottomDialog{
    public HighlighterWidthDialog(@NonNull Context context) {
        super(context);
    }

    private SeekBar seekBar;
    private int curWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        seekBar = findViewById(R.id.seekbar_width);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setCurWidth(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        setNegativeButton(this::dismiss);
        setTitle("设置宽度");
    }

    public void setCurWidth(int curWidth) {
        this.curWidth = curWidth;
        seekBar.setProgress(curWidth);
        setDialogHint(curWidth +" px");
    }

    public int getCurWidth() {
        return curWidth;
    }

    @Override
    public int getDialogContentLayout() {
        return R.layout.dialog_select_width;
    }
}
