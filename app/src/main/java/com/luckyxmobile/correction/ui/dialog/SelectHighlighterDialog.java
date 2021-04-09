package com.luckyxmobile.correction.ui.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.ui.views.CheckView;
import java.util.ArrayList;
import java.util.List;

public class SelectHighlighterDialog implements View.OnClickListener {

    private BottomSheetDialog dialog;
    private OnDialogListener listener;

    private List<CheckView> btnList = new ArrayList<>();
    private CheckView curChecked;

    public SelectHighlighterDialog(Context context, int type) {
        dialog = new BottomSheetDialog(context);
        this.listener = (OnDialogListener) context;

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_select_highlighter, null);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(view.getWidth(), view.getHeight());
        FrameLayout bottom = dialog.findViewById(R.id.design_bottom_sheet);
        if (bottom != null) {
            bottom.setBackgroundResource(android.R.color.transparent);
        }

        view.findViewById(R.id.cancel_btn).setOnClickListener(view1 -> dialog.dismiss());
        view.findViewById(R.id.ensure_btn).setOnClickListener(view12 -> {
            listener.onEnsure((int)curChecked.getTag(), curChecked.getCheckedImg());
            dialog.dismiss();
        });

        btnList.add(view.findViewById(R.id.highlighter_blue));
        btnList.get(0).setTag(0x220);
        btnList.add(view.findViewById(R.id.highlighter_red));
        btnList.get(1).setTag(0x221);
        btnList.add(view.findViewById(R.id.highlighter_yellow));
        btnList.get(2).setTag(0x223);
        btnList.add(view.findViewById(R.id.highlighter_green));
        btnList.get(3).setTag(0x222);
        btnList.add(view.findViewById(R.id.highlighter_white));
        btnList.get(4).setTag(0x225);

        for (CheckView btn: btnList) {
            btn.setOnClickListener(this);
            int tag = (int) btn.getTag();
            if (type == tag) {
                curChecked = btn;
                btn.setChecked(true);
            } else {
                btn.setChecked(false);
            }
        }
    }

    @Override
    public void onClick(View view) {
        checkBtn(view.getId());
        curChecked = (CheckView) view;
    }

    private void checkBtn(int id) {
        for (CheckView view: btnList) {
            view.setChecked(id == view.getId());
        }
    }

    public BottomSheetDialog getDialog() {
        return dialog;
    }

    public interface OnDialogListener{
        void onEnsure(int curType, Drawable res);
    }
}
