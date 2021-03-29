package com.luckyxmobile.correction.ui.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.ui.views.CheckMenuItemView;

import java.util.ArrayList;
import java.util.List;

public class SelectWidthDialog{

    private BottomSheetDialog dialog;
    private OnDialogListener listener;

    private SeekBar seekBar;
    private TextView valueTv;
    private int curWidth;

    public SelectWidthDialog(Context context, int width) {
        dialog = new BottomSheetDialog(context);
        this.listener = (OnDialogListener) context;
        this.curWidth = width;

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_select_width, null);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(view.getWidth(), view.getHeight());
        FrameLayout bottom = dialog.findViewById(R.id.design_bottom_sheet);
        if (bottom != null) {
            bottom.setBackgroundResource(android.R.color.transparent);
        }

        view.findViewById(R.id.cancel_btn).setOnClickListener(view1 -> dialog.dismiss());
        view.findViewById(R.id.ensure_btn).setOnClickListener(view12 -> {
            listener.onEnsure(curWidth);
            dialog.dismiss();
        });

        seekBar = view.findViewById(R.id.seekbar);
        seekBar.setProgress(width);
        valueTv = view.findViewById(R.id.seekbar_value);
        valueTv.setText(curWidth+" px");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                curWidth = i;
                valueTv.setText(i +" px");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


    public BottomSheetDialog getDialog() {
        return dialog;
    }

    public interface OnDialogListener{
        void onEnsure(int width);
    }
}
