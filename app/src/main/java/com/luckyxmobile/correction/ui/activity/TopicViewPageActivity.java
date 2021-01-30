package com.luckyxmobile.correction.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.viewpager.widget.ViewPager;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.adapter.TopicTagAdapter;
import com.luckyxmobile.correction.adapter.TopicViewPageAdapter;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.presenter.TopicViewPagePresenter;
import com.luckyxmobile.correction.view.ITopicViewPage;
import com.zhy.view.flowlayout.TagFlowLayout;
import java.util.List;

import butterknife.BindAnim;
import butterknife.BindView;
import butterknife.OnClick;

public class TopicViewPageActivity extends AppCompatActivity implements ITopicViewPage {

    public static final String TAG = "TopicViewPageActivity";

    @BindView(R.id.topic_view_pager)
    ViewPager topicViewPager;

    @BindView(R.id.topic_view_pager_tags)
    TagFlowLayout topicTagLayout;
    private TopicTagAdapter topicTagAdapter;

    @BindView(R.id.topic_view_pager_top_bar)
    LinearLayout topBarLayout;

    @BindView(R.id.progress_bar_topic_view_page)
    ProgressBar topicViewPageBar;

    @BindView(R.id.collect_button)
    ImageButton collectBtn;

    private TopicViewPageAdapter topicViewPageAdapter;

    @BindAnim(R.anim.layout_in_above) Animation aboveLayoutIn;
    @BindAnim(R.anim.layout_out_above) Animation aboveLayoutOut;
    @BindAnim(R.anim.layout_in_below) Animation belowLayoutIn;
    @BindAnim(R.anim.layout_out_below) Animation belowLayoutOut;

    private TopicViewPagePresenter presenter;

//    private Topic curTopic;
//    private int curTopicPosition;

    private boolean isFullScreen = false;
    private boolean isShowTag = true;

    private SharedPreferences preferences;
//    public static boolean IS_CLICK_SMEAR_BY = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_topic_view_page);
        initView();
    }

    @Override
    public void setTopicViewPage(List<Topic> topicList) {

        topicViewPageBar.setMax(topicList.size());
        topicViewPageAdapter = new TopicViewPageAdapter(this,topicList);
        topicViewPager.setAdapter(topicViewPageAdapter);
        topicViewPager.setCurrentItem(presenter.getCurTopicPosition());
        //滚动监听事件 刷新
        topicViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                presenter.curTopicChange(position);
                topicViewPageBar.setProgress(position+1,true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void setTopicCollectBtn(boolean isCollect) {
        collectBtn.setBackground(
                isCollect?getDrawable(R.drawable.ic_collect)
                        :getDrawable(R.drawable.ic_uncollect)
        );
    }

    @Override
    public void setTopicTagFlowLayout(Topic curTopic) {
        if (isShowTag) {
            topicTagAdapter.setCurTopic(curTopic);
            topicTagLayout.onChanged();
        }
    }

    @OnClick(R.id.topic_info_btn)
    public void onClickInfoBtn() {
        Intent intent = new Intent(this, TopicInfoActivity.class);
        intent.putExtra(Constants.TOPIC_ID, presenter.getCurTopicId());
        intent.putExtra(Constants.TOOLBAR_NAME,presenter.getCurTopicBookName());
        startActivity(intent);
    }


    @OnClick(R.id.collect_button)
    public void onClickCollectBtn() {
        presenter.topicCollectChange();
    }

    @OnClick(R.id.topic_image_edit_btn)
    public void onClickEditBtn() {

    }


    private void initView() {
        int book_id = getIntent().getIntExtra(Constants.BOOK_ID,0);
        int curTopicPosition = getIntent().getIntExtra(Constants.TOPIC_POSITION,0);


        preferences = getSharedPreferences(Constants.TABLE_SHARED_CORRECTION,MODE_PRIVATE);

        isShowTag = preferences.getBoolean(Constants.TABLE_SHOW_TAG_IN_TOPIC_VIEW_PAGE, true);

        topicTagLayout.setVisibility(isShowTag?View.VISIBLE:View.GONE);

        if (isShowTag) {
            topicTagAdapter = new TopicTagAdapter(null);
            topicTagAdapter.setItemClickable(false);
            topicTagAdapter.setShowUnchecked(false);
            topicTagLayout.setAdapter(topicTagAdapter);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, R.anim.activity_close);
        finish();
    }

    private void setFullScreen() {
        isFullScreen = !isFullScreen;
        if (isFullScreen) {
            topBarLayout.startAnimation(aboveLayoutIn);
            if (isShowTag) {
                topicTagLayout.setVisibility(View.VISIBLE);
            }
        } else {
            topBarLayout.startAnimation(aboveLayoutOut);
            if (isShowTag) {
                topicTagLayout.setVisibility(View.GONE);
            }
        }
    }

}
