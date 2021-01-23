package com.luckyxmobile.correction.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.adapter.HeadBookAdapter;
import com.luckyxmobile.correction.adapter.RecentTopicAdapter;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.presenter.MainViewPresenter;
import com.luckyxmobile.correction.presenter.impl.MainViewPresenterImpl;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.ui.dialog.BookInfoDialog;
import com.luckyxmobile.correction.utils.impl.PermissionsUtil;
import com.luckyxmobile.correction.view.MainView;

import org.litepal.LitePal;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.List;


import androidx.recyclerview.widget.RecyclerView;
import me.pqpo.smartcropperlib.SmartCropper;


/**
 * @author qjj
 */
public class MainActivity extends AppCompatActivity implements  View.OnClickListener, MainView,
        HeadBookAdapter.OnHeadBookAdapterListener {

    public static final String TAG = "MainActivity";
    private long lastClickTime = 0L;

    private TextView recentTopicTv;

    private HeadBookAdapter headBookAdapter;

    private RecentTopicAdapter recentTopicAdapter;

    private BookInfoDialog bookInfoDialog;

    private MainViewPresenter mainViewPresenter;

    private final BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.i(TAG, "OpenCV loaded successfully");
            } else {
                super.onManagerConnected(status);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //申请权限
        PermissionsUtil.initRequestPermission(this);

        mainViewPresenter = new MainViewPresenterImpl(this);

        //初始化openCV
        initOpenCV();

        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();

        //删去没有在错题本内的错题
        LitePal.deleteAll(Topic.class, "book_id = ?", "0");

    }


    private void initOpenCV() {
        Log.i(TAG, "Trying to load OpenCV library");
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "Cannot connect to OpenCV Manager");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mOpenCVCallBack);
        } else {
            Log.i(TAG, "openCV successful");
            mOpenCVCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            System.out.println(Runtime.getRuntime().maxMemory());
        }
    }

    private void initView() {

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        findViewById(R.id.add_book_main).setOnClickListener(this);

        recentTopicTv = findViewById(R.id.tv_recent_topic);

        bookInfoDialog = new BookInfoDialog(this);
        bookInfoDialog.setAlterCoverButton(this);

        //初始化裁剪框
        SmartCropper.buildImageDetector(this);
    }

    @Override
    public void setHeadBookRv(List<Book> headBookList) {

        GridLayoutManager mLayoutManager = new GridLayoutManager(this,4);
        headBookAdapter = new HeadBookAdapter(this, headBookList);

        RecyclerView headBookRv = findViewById(R.id.rv_head_book);
        headBookRv.setLayoutManager(mLayoutManager);
        headBookRv.setItemAnimator(new DefaultItemAnimator());
        headBookRv.setNestedScrollingEnabled(false);//解决卡顿
        headBookRv.setAdapter(headBookAdapter);
    }

    @Override
    public void setRecentTopicRv(List<Topic> topicList) {

        if (topicList.isEmpty()) {
            recentTopicTv.setVisibility(View.GONE);
        }

        recentTopicAdapter = new RecentTopicAdapter(this, topicList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        RecyclerView recentTopicRv = findViewById(R.id.main_recycler_recent_topic);
        recentTopicRv.setLayoutManager(mLayoutManager);
        recentTopicRv.setItemAnimator(new DefaultItemAnimator());
        recentTopicRv.setNestedScrollingEnabled(false);//解决卡顿
        recentTopicRv.setAdapter(recentTopicAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            String imageType = data.getStringExtra(Constants.WHICH_IMAGE);


            if (Constants.IMAGE_BOOK_COVER.equals(data.getStringExtra(Constants.WHICH_IMAGE))){
                ((MainViewPresenterImpl) mainViewPresenter)
                        .alterBookCoverPath(data.getStringExtra(Constants.IMAGE_PATH));
            }

            if(Constants.REQUEST_CODE_SELECT_ALBUM == requestCode){

            }

            if (Constants.REQUEST_CODE_TAKE_PHOTO == requestCode){

            }
        }
    }

    //点击事件
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.add_book_main:
                mainViewPresenter.addBook();
                break;

            case R.id.floating_button_add_topic:
                //TODO:打开相机
                break;

            case R.id.alter_cover_image:
                //TODO：dialog 修改bookCover
                break;

            default:
                break;
        }
    }

    /**
     * 加载菜单选项
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_paper:
                startActivity(new Intent(MainActivity.this, PaperActivity.class));
                break;
            case R.id.main_set:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
                default:
                    break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void addBookFinished(Book book) {
        headBookAdapter.addBook(book);
    }

    @Override
    public void alterBookFinished(Book book) {
        headBookAdapter.alterBook(book);
    }

    @Override
    public void deleteBookFinished(Book book) {

        recentTopicAdapter.deleteTopic(book.getId());
        recentTopicAdapter.notifyDataSetChanged();

    }

    @Override
    public void OnToastFinished(String showLog) {
        Toast.makeText(this, showLog, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getMainViewContext() {
        return this;
    }

    @Override
    public void onBookClickListener(Book book) {
        //防止多次点击
        if (System.currentTimeMillis() - lastClickTime < Constants.MIN_CLICK_DELAY_TIME){
            return;
        }

        lastClickTime = System.currentTimeMillis();
        Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
        intent.putExtra(Constants.BOOK_ID,book.getId());
        startActivity(intent);
    }

    @Override
    public void onBookMenuRemove(Book book) {
        //删除错题本
        new AlertDialog.Builder(MainActivity.this)
            .setTitle(R.string.confirm_delete)
            .setIcon(R.drawable.ic_delete_red_24dp)
            .setMessage(R.string.confirm_delete_notebook_hint)
            .setPositiveButton(R.string.ensure, (dialog, which) -> mainViewPresenter.deleteBook(book))
            .setNegativeButton(R.string.cancel,null).show();
    }

    @Override
    public void onBookMenuAlter(Book book) {
        bookInfoDialog.build(book)
            .setPositiveButton(R.string.ensure, (dialogInterface, i) -> {
                if(TextUtils.isEmpty(bookInfoDialog.getBookName())){
                    Toast.makeText(this, getString(R.string.empty_input), Toast.LENGTH_SHORT).show();
                }else{//调用model层，在数据库修改错题本

                }
            }).show();
    }
}
