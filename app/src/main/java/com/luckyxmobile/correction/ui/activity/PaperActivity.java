package com.luckyxmobile.correction.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintManager;
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
import com.luckyxmobile.correction.adapter.PrintPreviewAdapter;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.model.bean.Paper;
import com.luckyxmobile.correction.ui.dialog.EditTextDialog;
import com.luckyxmobile.correction.ui.dialog.ProgressDialog;
import com.luckyxmobile.correction.utils.FilesUtils;
import com.luckyxmobile.correction.utils.PdfUtils;

import org.litepal.LitePal;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaperActivity extends AppCompatActivity implements PaperAdapter.OnItemListener {


    private PaperAdapter paperAdapter;
    @BindView(R.id.paper_recyclerview)
    RecyclerView paperRv;
    @BindView(R.id.nothing_hint)
    ImageView paperNothing;
    @BindView(R.id.new_paper_btn)
    Button newPaperBtn;

    private EditTextDialog editTextDialog;
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
            final int mScrollThreshold = 100;
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
        showPaperDialog(null);
    }

    private void showPaperDialog(Paper paper) {
        if (editTextDialog == null) {
            editTextDialog = new EditTextDialog(this);
            editTextDialog.create();
            editTextDialog.setTitle(R.string.new_test_page);
            editTextDialog.setTextHint(R.string.test_page);
            editTextDialog.setMaxLength(15);
            editTextDialog.setNegativeButton(R.string.cancel, () -> {
                editTextDialog.dismiss();
            });
        }
        editTextDialog.setPositiveButton(R.string.ensure, ()-> {
            String paperName = editTextDialog.getText();
            if (null == paperName) return;
            Paper result = paper==null?new Paper():paper;
            result.setPaperName(paperName);
            if (result.isSaved()) {
                paperAdapter.refresh(result);
            } else {
                paperAdapter.addPaper(result);
            }
            result.save();
            setPaperNothing(paperAdapter.isEmpty());
            editTextDialog.dismiss();
        });
        editTextDialog.setNeutralButton(R.string.select_topics, ()->{
            String paperName = editTextDialog.getText();
            if (null == paperName) return;
            Paper result = paper==null?new Paper():paper;
            result.setPaperName(paperName);
            if (result.isSaved()) {
                paperAdapter.refresh(result);
            } else {
                paperAdapter.addPaper(result);
            }
            result.save();
            setPaperNothing(paperAdapter.isEmpty());
            editTextDialog.dismiss();
            Intent intent = new Intent(this, SelectTopicActivity.class);
            intent.putExtra(Constants.PAPER_ID, result.getId());
            startActivity(intent);
        });
        editTextDialog.setText(paper==null?null:paper.getPaperName());
        editTextDialog.show();
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
                    sharePaperPdf(paper);
                    break;

                case R.id.rename:
                    showPaperDialog(paper);
                    break;

                case R.id.print:
                    previewWindow(paper);
                    break;

                default: break;
            }

            return true;
        });
        paperAdapter.notifyDataSetChanged();
        popupMenu.show();
    }

    private void previewWindow(Paper paper) {

        String path = FilesUtils.getInstance().getPdfPath(paper);
        PdfUtils pdfUtils = PdfUtils.getInstance();

        new ProgressDialog(this) {
            @Override
            public boolean onPreExecute() {
                if (paper.getTopicSet() == null || paper.getTopicSet().isEmpty()) {
                    onToast("还没有添加题目哟...");
                    return false;
                } else {
                    if (FilesUtils.getInstance().exists(path)) {
                        onPostExecute(true);
                        return false;
                    }
                    return true;
                }
            }
            @Override
            public boolean doInBackground() throws Exception {
                pdfUtils.init(paper);
                return pdfUtils.start();
            }
            @Override
            public void onPostExecute(boolean result) {
                super.onPostExecute(result);
                if (!result) {
                    onToast("发生了点错误...");
                } else {
                    PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                    PrintAttributes.Builder builder = new PrintAttributes.Builder();
                    builder.setColorMode(PrintAttributes.COLOR_MODE_COLOR);
                    PrintPreviewAdapter adapter = new PrintPreviewAdapter(PaperActivity.this, path);
                    printManager.print(getString(R.string.app_name), adapter, builder.build());
                }
            }
        }.start();
    }

    private void sharePaperPdf(Paper paper) {
        String path = FilesUtils.getInstance().getPdfPath(paper);
        PdfUtils pdfUtils = PdfUtils.getInstance();

        new ProgressDialog(this) {
            @Override
            public boolean onPreExecute() {
                if (paper.getTopicSet() == null || paper.getTopicSet().isEmpty()) {
                    onToast("还没有添加题目哟...");
                    return false;
                } else {
                    if (FilesUtils.getInstance().exists(path)) {
                        onPostExecute(true);
                        return false;
                    }
                    return true;
                }
            }
            @Override
            public boolean doInBackground() throws Exception {
                pdfUtils.init(paper);
                return pdfUtils.start();
            }
            @Override
            public void onPostExecute(boolean result) {
                super.onPostExecute(result);
                if (!result) {
                    onToast("发生了点错误...");
                } else {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, FilesUtils.getInstance().getUri(new File(path)));
                    shareIntent.setType("application/pdf");
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivity(Intent.createChooser(shareIntent, "分享到"));
                }
            }
        }.start();
    }

    public void onToast(String log) {
        Toast.makeText(this, log, Toast.LENGTH_SHORT).show();
    }

}
