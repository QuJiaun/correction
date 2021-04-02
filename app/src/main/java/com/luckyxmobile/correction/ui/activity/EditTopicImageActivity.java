package com.luckyxmobile.correction.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.global.MySharedPreferences;
import com.luckyxmobile.correction.model.BeanUtils;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.ImageParam;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.presenter.EditTopicImagePresenter;
import com.luckyxmobile.correction.presenter.impl.EditTopicImagePresenterImpl;
import com.luckyxmobile.correction.ui.dialog.SelectBookDialog;
import com.luckyxmobile.correction.ui.dialog.setImageParamDialog;
import com.luckyxmobile.correction.ui.dialog.SelectHighlighterDialog;
import com.luckyxmobile.correction.ui.dialog.SelectWidthDialog;
import com.luckyxmobile.correction.ui.views.CheckMenuItemView;
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
public class EditTopicImageActivity extends AppCompatActivity implements
        SelectBookDialog.OnClickListener, SelectHighlighterDialog.OnDialogListener,
        SelectWidthDialog.OnDialogListener, setImageParamDialog.OnDialogListener{

    public static final String TAG = "EditPhotoActivity";

    @BindView(R.id.drawing_view)
    DrawingView drawingView;
    @BindView(R.id.drawing_view_undo)
    CheckMenuItemView undoBtn;
    @BindView(R.id.drawing_view_redo)
    CheckMenuItemView redoBtn;
    @BindView(R.id.drawing_view_tool_highlighter)
    CheckMenuItemView highlighterBtn;
    @BindView(R.id.drawing_view_tool_erase)
    CheckMenuItemView eraseBtn;
    @BindView(R.id.drawing_view_tool_image_param)
    CheckMenuItemView radioBtn;

    private SelectBookDialog selectBookDialog;

    private EditTopicImagePresenter presenter;

    private TopicImage curTopicImage;

    private MySharedPreferences sharedPreferences;
    private String fromActivity;
    private int curBookId = -1;
    private int curTopicId = -1;

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

        DestroyActivityUtil.addDestroyActivityToMap(EditTopicImageActivity.this,TAG);

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
        undoBtn.setChecked(drawingView.undoAble());
        redoBtn.setChecked(drawingView.redoAble());
    }

    @OnClick({R.id.drawing_view_tool_highlighter,
            R.id.drawing_view_tool_erase,
            R.id.drawing_view_tool_width,
            R.id.drawing_view_tool_image_param})
    public void onClickTools(View view) {
        switch (view.getId()) {
            case R.id.drawing_view_tool_highlighter:
                new SelectHighlighterDialog(this, drawingView.getCurType())
                        .getDialog().show();
                eraseBtn.setChecked(false);
                break;

            case R.id.drawing_view_tool_width:
                new SelectWidthDialog(this, drawingView.getCurWidth())
                        .getDialog().show();
                break;

            case R.id.drawing_view_tool_erase:
                eraseBtn.setChecked(true);
                highlighterBtn.setChecked(false);
                drawingView.setCurPaint(Constants.PAINT_ERASE);
                break;

            case R.id.drawing_view_tool_image_param:
                new setImageParamDialog(this, curTopicImage)
                        .getDialog().show();
                break;
        }
    }

    @OnClick(R.id.drawing_view_return)
    public void onClickReturnBtn() {
        DestroyActivityUtil.destroyActivity(TAG);
    }

    @OnClick(R.id.drawing_view_ok)
    public void onClickOkBtn() {
        //修改TopicImage
        if (curTopicImage.getId() > 0) {
            List<String> highlighterList = BeanUtils.obj2Strings(drawingView.getHighlighterList());
            curTopicImage.setHighlighterList(highlighterList);
            curTopicImage.save();
            DestroyActivityUtil.destroyActivityALL();
            return;
        }

        //添加TopicImage
        selectBookDialog = new SelectBookDialog(this);
        selectBookDialog.initBookAll(curBookId);
        selectBookDialog.getDialog().show();
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


    @Override
    public void onSelectBookFinished(Book book, int imageType) {

        selectBookDialog.getDialog().dismiss();
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

        DestroyActivityUtil.destroyActivityALL();
    }


    @Override
    public void onEnsure(int curType, Drawable res) {
        drawingView.setCurPaint(curType);
        highlighterBtn.setCheckedImg(res);
        highlighterBtn.setChecked(true);
    }

    @Override
    public void onEnsure(int width) {
        drawingView.setCurPaintWidth(width);
        Toast.makeText(this, "宽度设置为" + width +" px", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onEnsure(ImageParam param) {
        drawingView.setImageParam(param);
    }
}
