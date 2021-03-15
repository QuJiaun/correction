package com.luckyxmobile.correction.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.global.MySharedPreferences;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.presenter.EditTopicImagePresenter;
import com.luckyxmobile.correction.presenter.impl.EditTopicImagePresenterImpl;
import com.luckyxmobile.correction.ui.dialog.SelectBookDialog;
import com.luckyxmobile.correction.ui.views.CheckMenuItemView;
import com.luckyxmobile.correction.ui.views.DrawingView;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.utils.DestroyActivityUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 编辑图片页面
 * @author qjj、
 * @date 2019/08/03
 */
public class EditTopicImageActivity extends AppCompatActivity implements SelectBookDialog.OnClickListener {

    public static final String TAG = "EditPhotoActivity";

    @BindView(R.id.drawing_view)
    DrawingView drawingView;
    @BindView(R.id.drawing_view_undo)
    CheckMenuItemView undoBtn;
    @BindView(R.id.drawing_view_redo)
    CheckMenuItemView redoBtn;

    @BindViews({R.id.drawing_view_tool_highlighter,
            R.id.drawing_view_tool_white_out,
            R.id.drawing_view_tool_erase,
            R.id.drawing_view_tool_width,
            R.id.drawing_view_tool_contrast_radio})
    List<CheckMenuItemView> toolItemList;

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

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.getBoolean("dialog_show", false)) {
            onClickOkBtn();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (selectBookDialog != null) {
            outState.putBoolean("dialog_show", selectBookDialog.getDialog().isShowing());
        }
        super.onSaveInstanceState(outState);
    }

    private void initViewDate() {

        DestroyActivityUtil.addDestroyActivityToMap(EditTopicImageActivity.this,TAG);

        String imagePath = getIntent().getStringExtra(Constants.IMAGE_PATH);

        sharedPreferences = MySharedPreferences.getInstance();
        fromActivity = sharedPreferences.getString(Constants.FROM_ACTIVITY, MainActivity.TAG);
        curBookId = sharedPreferences.getInt(Constants.CURRENT_BOOK_ID, -1);
        curTopicId = sharedPreferences.getInt(Constants.CURRENT_TOPIC_ID, -1);
        int curTopicImageId = sharedPreferences.getInt(Constants.CURRENT_TOPIC_IMAGE_ID, -1);

        if (curTopicImageId <= 0) {
            if (TextUtils.isEmpty(imagePath)) throw new RuntimeException("imagePath is null");
            curTopicImage = new TopicImage();
            curTopicImage.setPath(imagePath);
            curTopicImage.setContrast_radio(Constants.CONTRAST_RADIO_COMMON);
            curTopicImage.setWord_size(-1);
            curTopicImage.setHighlighterList(new ArrayList<>());

        } else{

            curTopicImage = LitePal.find(TopicImage.class, curTopicImageId);
            if (curTopicImage == null || TextUtils.isEmpty(curTopicImage.getPath())) throw new RuntimeException("imagePath is null");
        }

        drawingView.init(curTopicImage);
    }

    @OnClick({R.id.drawing_view_tool_highlighter,
            R.id.drawing_view_tool_white_out,
            R.id.drawing_view_tool_erase,
            R.id.drawing_view_tool_width,
            R.id.drawing_view_tool_contrast_radio})
    public void onClickTools(View view) {

    }

    private void setToolItemChecked(int viewId) {
        for (CheckMenuItemView view : toolItemList) {
            view.setChecked(view.getId() == viewId);
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
            curTopicImage.save();
            finishEdit();
            return;
        }

        //添加TopicImage
        selectBookDialog = new SelectBookDialog(this);

        selectBookDialog.initBookAll(curBookId);
        selectBookDialog.setTopicImageIv(drawingView.getImageBitmap());
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

        curTopicImage.setType(imageType);

        if (fromActivity.equals(TopicInfoActivity.TAG)) {

            presenter.addTopicImage2Topic(curTopicId, curTopicImage);

            Intent intent = new Intent(this, TopicInfoActivity.class);
            intent.putExtra(Constants.TOPIC_ID, curTopicId);
            startActivity(intent);

        } else if (fromActivity.equals(MainActivity.TAG) || fromActivity.equals(BookDetailActivity.TAG)) {

            presenter.saveTopicImage(book.getId(), curTopicImage);

            Intent intent = new Intent(this, BookDetailActivity.class);
            intent.putExtra(Constants.BOOK_ID, book.getId());
            startActivity(intent);
        }

        finishEdit();
    }

    private void finishEdit() {
        //删除缓存数据
        sharedPreferences.clear(Constants.FROM_ACTIVITY);
        sharedPreferences.clear(Constants.CURRENT_BOOK_ID);
        sharedPreferences.clear(Constants.CURRENT_TOPIC_ID);
        sharedPreferences.clear(Constants.CURRENT_TOPIC_IMAGE_ID);

        DestroyActivityUtil.destroyActivityALL();
    }

}
