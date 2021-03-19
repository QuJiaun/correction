package com.luckyxmobile.correction.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.adapter.PaperAdapter;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.model.bean.Paper;
import com.luckyxmobile.correction.ui.dialog.PaperInfoDialog;

import org.litepal.LitePal;

import java.lang.annotation.Annotation;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;

public class PaperActivity extends AppCompatActivity implements PaperAdapter.OnItemListener {


    private PaperAdapter paperAdapter;
    @BindView(R.id.paper_recyclerview)
    RecyclerView paperRv;
    @BindView(R.id.paper_nothing)
    ImageView paperNothing;
    @BindView(R.id.new_paper_btn)
    Button newPaperBtn;

    PaperInfoDialog paperInfoDialog;

    private Animation showAnim, hideAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper);
        ButterKnife.bind(this);
        setPaperListRv(LitePal.findAll(Paper.class));

        hideAnim = AnimationUtils.loadAnimation(this, R.anim.layout_out_below);
        showAnim = AnimationUtils.loadAnimation(this, R.anim.layout_in_below);
    }

    @Override
    protected void onResume() {
        super.onResume();
        paperAdapter.notifyDataSetChanged();
    }

    public void setPaperNothing(boolean isEmpty) {
        if (isEmpty) {
            paperNothing.setVisibility(View.VISIBLE);
        } else {
            paperNothing.setVisibility(View.GONE);
        }
    }

    public void setPaperListRv(List<Paper> paperList) {

        setPaperNothing(paperList.isEmpty());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        paperRv.setLayoutManager(layoutManager);
        paperAdapter = new PaperAdapter(this, paperList);
        paperRv.setAdapter(paperAdapter);
        paperRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int mScrollThreshold;
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isSignificantDelta = Math.abs(dy) > mScrollThreshold;
                if (isSignificantDelta) {
                    if (dy > 0) {
                        newPaperBtn.startAnimation(hideAnim);
                        newPaperBtn.setVisibility(View.GONE);
                    } else {
                        newPaperBtn.startAnimation(showAnim);
                        newPaperBtn.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @OnClick(R.id.new_paper_btn)
    public void onClickAddPaperBtn() {
        showPaperInfoDialog(null);
    }

    private void showPaperInfoDialog(Paper paper) {
        paperInfoDialog = new PaperInfoDialog(this);
        paperInfoDialog.setPaper(paper);
        paperInfoDialog.setNeutralButton(R.string.select_topics, (dialogInterface, i) -> {
            Paper tmp = paperInfoDialog.getPaper();
            String name = tmp.getPaperName();
            if (checkPaperName(name)) {
                if (tmp.getId() > 0) {
                    paperAdapter.refresh(paper);
                } else {
                    paperAdapter.addPaper(tmp);
                }
                tmp.save();
                Intent intent = new Intent(this, SelectTopicActivity.class);
                intent.putExtra(Constants.PAPER_ID, tmp.getId());
                startActivity(intent);
            }

            setPaperNothing(paperAdapter.isEmpty());
        });
        paperInfoDialog.setPositiveButton(R.string.ensure, (dialogInterface, i) -> {
            Paper tmp = paperInfoDialog.getPaper();
            String name = tmp.getPaperName();
            if (checkPaperName(name)) {
                if (tmp.getId() > 0) {
                    paperAdapter.refresh(paper);
                } else {
                    paperAdapter.addPaper(tmp);
                }
                tmp.save();
            }
            setPaperNothing(paperAdapter.isEmpty());
        });
        paperInfoDialog.show();
    }

    private boolean checkPaperName(String name) {
        if (name == null || name.length() <= 0) {
            onToast(getString(R.string.empty_input));
            return false;
        }

        if (name.length() > 15) {
            onToast(getString(R.string.input_error));
            return false;
        }

        return true;
    }

    @Override
    public void onItemClick(Paper paper) {
        Intent intent = new Intent(this, PaperDetailActivity.class);
        intent.putExtra(Constants.PAPER_ID, paper.getId());
        startActivity(intent);
    }

    @Override
    public void showPopupMenu(View view, Paper paper, int position) {

        PopupMenu popupMenu = new PopupMenu(this,view, Gravity.END|Gravity.BOTTOM);
        popupMenu.inflate(R.menu.menu_paper_item);
        //3.为弹出菜单设置点击监听
        popupMenu.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {
                case R.id.delete:
                    paperAdapter.remove(paper);
                    paper.delete();
                    setPaperNothing(paperAdapter.isEmpty());
                    break;

                case R.id.share:

                    break;

                case R.id.rename:
                    showPaperInfoDialog(paper);
                    break;

                case R.id.print:

                    break;

                default: break;
            }

            return true;
        });
        paperAdapter.notifyDataSetChanged();
        popupMenu.show();
    }

    public void onToast(String log) {
        Toast.makeText(this, log, Toast.LENGTH_SHORT).show();
    }

}
