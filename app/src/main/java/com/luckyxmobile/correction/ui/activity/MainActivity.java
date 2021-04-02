package com.luckyxmobile.correction.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.adapter.HeadBookAdapter;
import com.luckyxmobile.correction.adapter.RecentTopicAdapter;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.presenter.MainViewPresenter;
import com.luckyxmobile.correction.presenter.impl.MainViewPresenterImpl;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.ui.dialog.AddTopicImageDialog;
import com.luckyxmobile.correction.ui.dialog.BookInfoDialog;
import com.luckyxmobile.correction.utils.PermissionsUtil;
import com.luckyxmobile.correction.view.MainView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindAnim;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.pqpo.smartcropperlib.SmartCropper;
import sakura.particle.Factory.ExplodeParticleFactory;
import sakura.particle.Factory.ParticleFactory;
import sakura.particle.Main.ExplosionSite;


/**
 * @author qjj
 */
@SuppressLint("NonConstantResourceId")
public class MainActivity extends AppCompatActivity implements MainView,
        HeadBookAdapter.OnHeadBookAdapterListener,
        BookInfoDialog.OnBtnClickListener,
        AddTopicImageDialog.OnClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private long lastClickTime = 0L;

    @BindView(R.id.tv_recent_topic)
    TextView recentTopicTv;
    @BindView(R.id.rv_head_book)
    RecyclerView headBookRv;
    @BindView(R.id.floating_button_add_topic)
    FloatingActionButton floatingActionButton;
    @BindAnim(R.anim.layout_in_below)
    Animation showAnim;
    @BindAnim(R.anim.layout_out_below)
    Animation hideAnim;

    private HeadBookAdapter headBookAdapter;
    private BookInfoDialog bookInfoDialog;
    private MainViewPresenter mainViewPresenter;
    private ExplosionSite explosionSite;

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

        setSupportActionBar(findViewById(R.id.toolbar));

        ButterKnife.bind(this);
        //申请权限
        PermissionsUtil.initRequestPermission(this);
        mainViewPresenter = new MainViewPresenterImpl(this);
        explosionSite = new ExplosionSite(this, new ExplodeParticleFactory());
        //初始化openCV
        initOpenCV();
        //初始化裁剪框
        SmartCropper.buildImageDetector(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainViewPresenter.init();
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


    @Override
    public void setHeadBookRv(List<Book> headBookList) {

        GridLayoutManager mLayoutManager = new GridLayoutManager(this,4);
        headBookAdapter = new HeadBookAdapter(this, headBookList);
        headBookRv.setLayoutManager(mLayoutManager);
        headBookRv.setItemAnimator(new DefaultItemAnimator());
        headBookRv.setNestedScrollingEnabled(false);//解决卡顿
        headBookRv.setAdapter(headBookAdapter);
    }

    @Override
    public void setRecentTopicRv(List<Topic> topicList) {

        RecyclerView recentTopicRv = findViewById(R.id.main_recycler_recent_topic);
        recentTopicTv.setVisibility(topicList.isEmpty()?View.GONE:View.VISIBLE);

        RecentTopicAdapter recentTopicAdapter = new RecentTopicAdapter(this, topicList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recentTopicRv.setLayoutManager(mLayoutManager);
        recentTopicRv.setItemAnimator(new DefaultItemAnimator());
        recentTopicRv.setNestedScrollingEnabled(false);//解决卡顿
        recentTopicRv.setAdapter(recentTopicAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_CODE_BOOK_COVER_IMAGE) {
                String imagePath = data.getStringExtra(Constants.IMAGE_PATH);
                if (bookInfoDialog == null) {
                    showBookInfoDialog(null);
                }
                bookInfoDialog.alterBookCover(imagePath);
            }
        }
    }

    @OnClick(R.id.add_book_main)
    public void onClickAddBook(){
        showBookInfoDialog(null);
    }

    private void showBookInfoDialog(Book book) {
        bookInfoDialog = new BookInfoDialog(this);
        bookInfoDialog.setBook(book);
        bookInfoDialog.create().show();
    }

    @OnClick(R.id.floating_button_add_topic)
    public void onClickAddTopic(){
        if (headBookAdapter.getBookList().size() > 1) {
            AddTopicImageDialog.show(this);
        } else {
            onToast("请先添加错题本");
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
    public void saveBookFinished(Book book) {
        headBookAdapter.addBook(book);
    }

    @Override
    public void updateBookFinished(Book book) {
        headBookAdapter.upBook(book);
    }

    @Override
    public void deleteBookFinished() {
        mainViewPresenter.init();
    }

    @Override
    public void onBookClickListener(Book book) {
        //防止多次点击
        if (System.currentTimeMillis() - lastClickTime < Constants.MIN_CLICK_DELAY_TIME){
            return;
        }

        lastClickTime = System.currentTimeMillis();
        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra(Constants.BOOK_ID, book.getId());
        startActivity(intent);
    }

    @Override
    public void onBookLongClickListener(View view, Book book) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.menu_long_click_book ,popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.item_book_info) {
                showBookInfoDialog(book);
            } else if (item.getItemId() == R.id.item_delete_book) {

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.confirm_delete)
                        .setIcon(R.drawable.ic_delete_red_24dp)
                        .setMessage(R.string.confirm_delete_notebook_hint)
                        .setPositiveButton(R.string.ensure, (dialog, which) ->{
                            explosionSite.addListener(view);
                            mainViewPresenter.removeBook(book);
                            explosionSite.explode(view);
                        } )
                        .setNegativeButton(R.string.cancel,null).show();
            }

            popupMenu.dismiss();
            return true;
        });

        popupMenu.show();
    }

    @Override
    public void onBookInfoDialogEnsure(Book book) {
        if (book.getName().length() > 15) {
            showBookInfoDialog(book);
            return;
        }

        if (book.getName() == null || book.getName().isEmpty()) {
            showBookInfoDialog(book);
            onToast(getString(R.string.empty_input));
            return;
        }

        if (book.getId() == 0) {
            mainViewPresenter.saveBook(book);
        }else {
            mainViewPresenter.alterBookInfo(book);
        }
    }

    @Override
    public void addTopicFrom(boolean album) {
        Intent intent = CropImageActivity.getIntent(this, album, true);
        intent.putExtra(Constants.FROM_ACTIVITY, TAG);
        startActivity(intent);
    }

    @Override
    public void onToast(String showLog) {
        Toast.makeText(getApplication(), showLog, Toast.LENGTH_SHORT).show();
    }
}
