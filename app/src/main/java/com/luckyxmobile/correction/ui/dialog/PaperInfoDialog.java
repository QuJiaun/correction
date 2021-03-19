package com.luckyxmobile.correction.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Paper;

public class PaperInfoDialog extends AlertDialog.Builder {

    private EditText paperNameEt;
    private Paper paper;

    public PaperInfoDialog(Activity activity) {
        super(activity);


        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_paper, null);
        setView(view);

        paperNameEt = view.findViewById(R.id.paperNameEdt);

        setNegativeButton(R.string.cancel, null);
    }

    public void setPaper(Paper paper) {
        if (paper == null) {
            paper = new Paper();
        }
        this.paper = paper;
        setPaperNameEt();
    }

    public Paper getPaper() {
        paper.setPaperName(paperNameEt.getText().toString());
        return paper;
    }

    private void setPaperNameEt() {
        if (paper.getPaperName() != null && !paper.getPaperName().isEmpty()) {
            paperNameEt.setText(paper.getPaperName());
            paperNameEt.setSelection(paper.getPaperName().length());
        }
    }

}


