package com.luckyxmobile.correction.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.adapter.HeadBookAdapter;
import com.luckyxmobile.correction.adapter.RecentTopicAdapter;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.presenter.MainViewPresenter;
import com.luckyxmobile.correction.presenter.impl.MainViewPresenterImpl;
import com.luckyxmobile.correction.utils.ConstantsUtil;
import com.luckyxmobile.correction.utils.ImageUtil;
import com.luckyxmobile.correction.utils.impl.PermissionsUtil;
import com.luckyxmobile.correction.view.IMainView;
import com.wang.avi.AVLoadingIndicatorView;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import org.litepal.LitePal;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.Collections;
import java.util.List;

import me.pqpo.smartcropperlib.SmartCropper;


/**
 * @author qjj
 */
public class MainActivity extends AppCompatActivity implements  View.OnClickListener, IMainView ,
        HeadBookAdapter.OnHeadBookAdapterListener {

    public static final String TAG = "MainActivity";
    private long lastClickTime = 0L;
    private Toolbar toolbar;
    private SwipeRecyclerView headBook;
    private HeadBookAdapter headBookAdapter;
    private SwipeRecyclerView recentTopic;
    public static TextView recentTopicText;
    private WindowManager windowManager;
    private View view;
    private int StartY,StartX;
    private TranslateAnimation showAnim,hideAnim;//Fab组的滑动动画效果
    private FloatingActionsMenu floatActionMenu,floatingActionsMenuWindow;
    private  List<Book> headBookList;
    private RecentTopicAdapter recentTopicAdapter;
    private int recentIndex = 0;
    private NestedScrollView scrollView;
    private List<Topic> topics;

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

        //初始化动画
        initAnim();

        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        mSearchView.clearFocus();

        //删去没有在错题本内的错题
        LitePal.deleteAll(Topic.class, "book_id = ?", "0");

        //初始化错题本布局
        initHeadBook();

        //初始化最近错题布局
        initRecentTopics();

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

    private void initAnim(){
        showAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        showAnim.setDuration(500);
        hideAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        hideAnim.setDuration(500);
    }

    private void initView() {

        toolbar = findViewById(R.id.toolbar);
        TextView addBookBtn = findViewById(R.id.add_book_main);
        headBook = findViewById(R.id.rv_head_book);
        recentTopic = findViewById(R.id.main_recycler_recent_topic);
        floatActionMenu = findViewById(R.id.fab_menu);
        scrollView = findViewById(R.id.scrollView);
        recentTopicText = findViewById(R.id.tv_recent_topic);

        addBookBtn.setOnClickListener(this);

        setSupportActionBar(toolbar);

        initWindowFAB();//设置FAB的展开和关闭


        //初始化裁剪框
        SmartCropper.buildImageDetector(this);

    }

    @Override
    public void setHeadBookList(List<Book> headBookList) {
        this.headBookList = headBookList;
    }

    private void initHeadBook(){
        GridLayoutManager mLayoutManager = new GridLayoutManager(this,4);
        headBookAdapter = new HeadBookAdapter(this, headBookList);
        headBook.setLayoutManager(mLayoutManager);
        headBook.setItemAnimator(new DefaultItemAnimator());
        headBook.setNestedScrollingEnabled(false);//解决卡顿
        headBook.setAdapter(headBookAdapter);
    }

    private void initRecentTopics(){
        topics = LitePal.findAll(Topic.class);  //数据库中的topics
        Collections.reverse(topics);

        recentTopicAdapter = new RecentTopicAdapter(this, topics);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recentTopic.setLayoutManager(mLayoutManager);
        recentTopic.setFocusable(false);
        recentTopic.setItemAnimator(new DefaultItemAnimator());
        recentTopic.setNestedScrollingEnabled(false);//解决卡顿
        recentTopic.setAdapter(recentTopicAdapter);

    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            String imageType = data.getStringExtra(ConstantsUtil.WHICH_IMAGE);

            if (ConstantsUtil.IMAGE_BOOK_COVER.equals(imageType)){

            }else if (ConstantsUtil.IMAGE){

            }

            if (ConstantsUtil.IMAGE_BOOK_COVER.equals(data.getStringExtra(ConstantsUtil.WHICH_IMAGE))){
                ((MainViewPresenterImpl) mainViewPresenter)
                        .alterBookCoverPath(data.getStringExtra(ConstantsUtil.IMAGE_PATH));
            }

            if(ConstantsUtil.REQUEST_CODE_SELECT_ALBUM == requestCode){

            }

            if (ConstantsUtil.REQUEST_CODE_TAKE_PHOTO == requestCode){

            }
        }
    }

    //点击事件
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.add_book_main:
                floatingActionsMenuWindow.collapse();
                mainViewPresenter.addBook();
                break;

            case R.id.fab_camera://启动相机
                Intent intent1 = CropImageActivity.getJumpIntent(this, TAG, false,
                        ConstantsUtil.IMAGE_ORIGINAL, true, true, -1);
                startActivity(intent1);
                break;

            case R.id.fab_album: //打开图库
                Intent intent2 = CropImageActivity.getJumpIntent(this, TAG, true,
                        ConstantsUtil.IMAGE_ORIGINAL, true, true, -1);
                startActivity(intent2);

                break;

            default:
                break;

        }
        //使用完后重置
        ImageUtil.resetResultPath();
    }

    /**
     * 对于FAB的展开和关闭的设置
     * floatActionMenu.collapse();//关闭按钮组
     * floatActionMenu.expand();//展开按钮组
     */
    public void initWindowFAB(){
        windowManager  = (WindowManager) MainActivity.this.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.window_fab, null);//获取到新建window的布局
        view.setFocusableInTouchMode(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionsMenuWindow.collapse();
                windowManager.removeView(view);
                floatActionMenu.setVisibility(View.VISIBLE);
            }
        });

        floatingActionsMenuWindow = view.findViewById(R.id.fab_menu);
        FloatingActionButton fabCamera = view.findViewById(R.id.fab_camera);
        FloatingActionButton fabAlbum = view.findViewById(R.id.fab_album);
        //为FAB设置关闭时将window中的view移除
        floatingActionsMenuWindow.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {

            }
            @Override
            public void onMenuCollapsed() {
                floatingActionsMenuWindow.collapse();
                windowManager.removeView(view);
                floatActionMenu.setVisibility(View.VISIBLE);
            }
        });
        //为FAB设置展开时新建一个window
        floatActionMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                floatActionMenu.collapse();
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.gravity = Gravity.CENTER;
                if (Build.VERSION.SDK_INT > 18 && Build.VERSION.SDK_INT < 25){
                    layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
                } else {
                    layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
                }
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
                layoutParams.format = PixelFormat.RGBA_8888;
                floatingActionsMenuWindow.expand();
                windowManager.addView(view,layoutParams);
                floatActionMenu.setVisibility(View.GONE);
            }
            @Override
            public void onMenuCollapsed() {

            }
        });

        //FAB按钮组的点击事件
        fabCamera.setOnClickListener(this);
        fabAlbum.setOnClickListener(this);
    }

    //全局变量point记录手指位置
    private Point point =new Point();
    /**
     * @param ev
     * @return
     * 主要用于判断手指在屏幕上的滑动方向，注意不能在onTouchEvent中使用，因为viewPager会截获
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 一定要spuer，否则事件打住,不会在向下调用了
        super.dispatchTouchEvent(ev);
        switch (ev.getAction()) {
            // 记录用户手指点击的位置
            case MotionEvent.ACTION_DOWN:
                StartX = (int) ev.getRawX();
                point.x = (int) ev.getRawX();
                point.y = (int) ev.getRawY();
                StartY = (int) ev.getRawY();
                Log.d(TAG, "StartX = " + StartX+",StartY="+StartY);
                break;
            case MotionEvent.ACTION_UP:
                int upX = (int) ev.getRawX();
                int upY = (int) ev.getRawY();
                Log.d(TAG, "UpX = " + upX +",upY="+ upY);
                if(upY -StartY>80){
                    if(floatActionMenu.getVisibility()!=View.VISIBLE){
                        floatActionMenu.startAnimation(showAnim);
                        floatActionMenu.setVisibility(View.VISIBLE);
                    }
                }
                if(StartY- upY >80 && LitePal.findAll(Topic.class).size() > 5){
                    if(floatActionMenu.getVisibility()!=View.GONE){
                        floatActionMenu.startAnimation(hideAnim);
                        floatActionMenu.setVisibility(View.GONE);
                    }
                }
                break;
        }
        return false;// return false,继续向下传递，return true;拦截,不向下传递
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
        headBookAdapter.notifyDataSetChanged();
    }

    @Override
    public void alterBookFinished(Book book) {
        headBookAdapter.alterBook(book);
        headBookAdapter.notifyDataSetChanged();
    }

    @Override
    public void deleteBookFinished(Book book) {

        recentTopicAdapter.deleteTopic(book.getId());
        recentTopicAdapter.notifyDataSetChanged();
        topics = recentTopicAdapter.getTopics();

        headBookAdapter.deleteBook(book);
        headBookAdapter.notifyDataSetChanged();
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
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onBookClickListener(Book book, int position) {
        //防止多次点击
        if (System.currentTimeMillis() - lastClickTime < ConstantsUtil.MIN_CLICK_DELAY_TIME){
            return;
        }
        lastClickTime = System.currentTimeMillis();
        Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
        intent.putExtra(ConstantsUtil.BOOK_ID,book.getId());
        startActivity(intent);
    }

    @Override
    public void onBookMenuClickListener(int menuPosition, Book book) {
        if (menuPosition == HeadBookAdapter.MENU_DELETE) {
            //删除错题本
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.confirm_delete)
                    .setIcon(R.drawable.ic_delete_red_24dp)
                    .setMessage(R.string.confirm_delete_notebook_hint)
                    .setPositiveButton(R.string.ensure, (dialog, which) -> mainViewPresenter.deleteBook(book))
                    .setNegativeButton(R.string.cancel,null).show();

        } else if (menuPosition == HeadBookAdapter.MENU_BOOK_INFO) {
            //修改错题本 调用presenter层 修改错题本
            mainViewPresenter.alterBookInfo(book);
        }
    }
}
