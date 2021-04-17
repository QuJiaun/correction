package com.luckyxmobile.correction.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.BeanUtils;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.ImageParam;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.presenter.EditTopicImagePresenter;
import com.luckyxmobile.correction.presenter.impl.EditTopicImagePresenterImpl;
import com.luckyxmobile.correction.ui.dialog.ChooseBookDialog;
import com.luckyxmobile.correction.ui.dialog.HighlighterTypeDialog;
import com.luckyxmobile.correction.ui.dialog.HighlighterWidthDialog;
import com.luckyxmobile.correction.ui.dialog.ImageParamDialog;
import com.luckyxmobile.correction.ui.views.CheckView;
import com.luckyxmobile.correction.ui.views.DrawingView;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.utils.DestroyActivityUtil;
import com.luckyxmobile.correction.utils.GsonUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 编辑图片页面
 * @author qjj、
 * @date 2019/08/03
 */
public class EditTopicImageActivity extends AppCompatActivity{

    private final String TAG = "EditPhotoActivity";

    @BindView(R.id.drawing_view)
    DrawingView drawingView;
    @BindView(R.id.drawing_view_undo)
    CheckView undoBtn;
    @BindView(R.id.drawing_view_redo)
    CheckView redoBtn;
    @BindView(R.id.drawing_view_tool_highlighter)
    CheckView highlighterBtn;
    @BindView(R.id.drawing_view_tool_erase)
    CheckView eraseBtn;
    @BindView(R.id.drawing_view_tool_image_param)
    CheckView imageParamBtn;
    @BindView(R.id.drawing_view_tool_ocr)
    CheckView ocrBtn;

    private ChooseBookDialog chooseBookDialog;
    private ImageParamDialog imageParamDialog;
    private HighlighterTypeDialog highlighterTypeDialog;
    private HighlighterWidthDialog highlighterWidthDialog;

    private EditTopicImagePresenter presenter;

    private TopicImage curTopicImage;

    private String fromActivity;
    private int curBookId = -1;
    private int curTopicId = -1;

    public EditTopicImageActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 将activity设置为全屏显示（必须放在setContentView()前）
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_topic_image);

        ButterKnife.bind(this);

        presenter = new EditTopicImagePresenterImpl();

        //初始化页面数据
        initViewDate();

    }

    private void initViewDate() {

        DestroyActivityUtil.add(this);

        String imagePath = getIntent().getStringExtra(Constants.IMAGE_PATH);
        fromActivity = getIntent().getStringExtra(Constants.FROM_ACTIVITY);
        curBookId = getIntent().getIntExtra(Constants.BOOK_ID, -1);
        curTopicId = getIntent().getIntExtra(Constants.TOPIC_ID, -1);

        int curTopicImageId = getIntent().getIntExtra(Constants.TOPIC_IMAGE_ID, -1);

        if (curTopicImageId <= 0) {
            if (TextUtils.isEmpty(imagePath)) throw new RuntimeException("imagePath is null");
            curTopicImage = new TopicImage();
            curTopicImage.setPath(imagePath);
            curTopicImage.setImageParam(GsonUtils.obj2Json(new ImageParam()));
            curTopicImage.setWord_size(-1);
            curTopicImage.setHighlighterList(new ArrayList<>());
        } else{
            Button returnBtn = findViewById(R.id.drawing_view_return);
            returnBtn.setText("退出");
            curTopicImage = LitePal.find(TopicImage.class, curTopicImageId);
            if (curTopicImage == null || TextUtils.isEmpty(curTopicImage.getPath())) throw new RuntimeException("imagePath is null");
        }

        drawingView.init(curTopicImage);
        drawingView.setScrollListener(() -> {
            undoBtn.setChecked(drawingView.undoAble());
            redoBtn.setChecked(drawingView.redoAble());
        });

        ocrBtn.setChecked(curTopicImage.isOcr());
        imageParamBtn.setChecked(!curTopicImage.isOcr());
    }

    @OnClick({R.id.drawing_view_tool_highlighter,
            R.id.drawing_view_tool_erase,
            R.id.drawing_view_tool_width,
            R.id.drawing_view_tool_image_param,
            R.id.drawing_view_tool_ocr})
    public void onClickTools(View view) {
        switch (view.getId()) {
            case R.id.drawing_view_tool_highlighter:
                showHighlighterTypeDialog();
                break;

            case R.id.drawing_view_tool_width:
                showHighlighterWidthDialog();
                break;

            case R.id.drawing_view_tool_erase:
                eraseBtn.setChecked(true);
                highlighterBtn.setChecked(false);
                drawingView.setCurType(Constants.PAINT_ERASE);
                break;

            case R.id.drawing_view_tool_image_param:
                if (imageParamBtn.isChecked()) {
                    showImageParamDialog();
                } else {
                    Toast.makeText(this, "必须先关闭OCR", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.drawing_view_tool_ocr:
                if (ocrBtn.isChecked()) {
                    drawingView.removeOCR();
                    ocrBtn.setChecked(false);
                    imageParamBtn.setChecked(true);
                } else {
                    boolean result = drawingView.startOCR();
                    ocrBtn.setChecked(result);
                    imageParamBtn.setChecked(!result);
                }
                break;
        }
    }

    @OnClick(R.id.drawing_view_return)
    public void onClickReturnBtn() {
       onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        drawingView.recycle();
        DestroyActivityUtil.destroy(this);
    }

    @OnClick(R.id.drawing_view_ok)
    public void onClickOkBtn() {
        //修改TopicImage
        if (curTopicImage.getId() > 0) {
            List<String> highlighterList = BeanUtils.obj2Strings(drawingView.getHighlighterList());
            curTopicImage.setHighlighterList(highlighterList);
            curTopicImage.save();
            drawingView.saveDrawingImage();
            DestroyActivityUtil.clear();
            return;
        }

        if (chooseBookDialog == null) {
            chooseBookDialog = new ChooseBookDialog(this);
            chooseBookDialog.create();
            chooseBookDialog.setEnsureBtn(() ->
                    onFinished(chooseBookDialog.getCurBook(),
                    chooseBookDialog.getCurImageType())
            );
        }

        chooseBookDialog.setCurBook(curBookId);
        if (!chooseBookDialog.isShowing()) {
            chooseBookDialog.show();
        }
    }

    @OnClick(R.id.drawing_view_undo)
    public void onClickUndoBtn() {
        undoBtn.setChecked(drawingView.undo());
        redoBtn.setChecked(drawingView.redoAble());
    }

    @OnClick(R.id.drawing_view_redo)
    public void onClickRedoBtn() {
        redoBtn.setChecked(drawingView.redo());
        undoBtn.setChecked(drawingView.undoAble());
    }

    private void showHighlighterWidthDialog() {
        if (highlighterWidthDialog == null) {
            highlighterWidthDialog = new HighlighterWidthDialog(this);
            highlighterWidthDialog.create();
            highlighterWidthDialog.setPositiveButton(() -> {
                int width = highlighterWidthDialog.getCurWidth();
                drawingView.setCurPaintWidth(width);
                Toast.makeText(EditTopicImageActivity.this,
                        "宽度设置为" + width +" px", Toast.LENGTH_SHORT).show();
                highlighterWidthDialog.dismiss();
            });
        }

        if (!highlighterWidthDialog.isShowing()){
            highlighterWidthDialog.setCurWidth(drawingView.getCurWidth());
            highlighterWidthDialog.show();
        }
    }

    private void showHighlighterTypeDialog() {
        if (highlighterTypeDialog == null) {
            highlighterTypeDialog = new HighlighterTypeDialog(this);
            highlighterTypeDialog.create();
            highlighterTypeDialog.setPositiveButton(() -> {
                drawingView.setCurType(highlighterTypeDialog.getType());
                highlighterBtn.setCheckedImg(highlighterTypeDialog.getIcon());
                highlighterBtn.setChecked(true);
                eraseBtn.setChecked(false);
                highlighterTypeDialog.dismiss();
            });
        }

        if (!highlighterTypeDialog.isShowing()) {
            highlighterTypeDialog.setType(drawingView.getCurType());
            highlighterTypeDialog.show();
        }
    }


    private void showImageParamDialog() {
        if (imageParamDialog == null) {
            imageParamDialog = new ImageParamDialog(this);
            imageParamDialog.create();
            imageParamDialog.setPositiveButton(() -> {
                drawingView.setImageParam(imageParamDialog.getImageParam());
                imageParamDialog.dismiss();
            });
        }

        if (!imageParamDialog.isShowing()) {
            ImageParam imageParam = GsonUtils.json2Obj(curTopicImage.getImageParam(), ImageParam.class);
            if (imageParam == null) imageParam = new ImageParam();
            imageParamDialog.setImageParam(imageParam);
            imageParamDialog.show();
        }
    }

    private void onFinished(Book book, int imageType) {

        List<String> highlighterList = BeanUtils.obj2Strings(drawingView.getHighlighterList());
        curTopicImage.setHighlighterList(highlighterList);
        curTopicImage.setType(imageType);

        if (fromActivity.equals(TopicInfoActivity.TAG)) {
            presenter.addTopicImage2Topic(curTopicId, curTopicImage);

        } else if (fromActivity.equals(MainActivity.TAG) || fromActivity.equals(BookDetailActivity.TAG)) {
            presenter.saveTopicImage(book.getId(), curTopicImage);

            Intent intent = new Intent(this, BookDetailActivity.class);
            intent.putExtra(Constants.BOOK_ID, book.getId());
            startActivity(intent);
        }
        drawingView.saveDrawingImage();
        DestroyActivityUtil.clear();
    }

}
