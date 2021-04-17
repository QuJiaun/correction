package com.luckyxmobile.correction.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Paper;

public class PaperDialog extends CommonDialog{

    private EditText paperNameEt;
    private Paper paper;

    public PaperDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        paperNameEt = findViewById(R.id.paperNameEdt);
        setNegativeButton(R.string.cancel, this::dismiss);
        setTitle(R.string.new_test_page);
    }

    public void setPaper(Paper paper) {
        this.paper = paper==null? new Paper(): paper;

        String paperName = this.paper.getPaperName();
        if (!TextUtils.isEmpty(paperName)) {
            paperNameEt.setText(paperName);
            paperNameEt.setSelection(paperName.length());
        } else {
            paperNameEt.setText(null);
            paperNameEt.setSelection(0);
        }
    }

    public Paper getPaper() {
        paper.setPaperName(paperNameEt.getText().toString());
        return paper;
    }

    @Override
    public int getDialogContentLayout() {
        return R.layout.dialog_add_paper;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
