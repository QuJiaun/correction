package com.luckyxmobile.correction.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.adapter.TopicTagAdapter;
import com.luckyxmobile.correction.adapter.TopicViewPageAdapter;
import com.luckyxmobile.correction.global.MySharedPreferences;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.presenter.TopicViewPagePresenter;
import com.luckyxmobile.correction.presenter.impl.TopicViewPagePresenterImpl;
import com.luckyxmobile.correction.ui.views.MyPagerTransformer;
import com.luckyxmobile.correction.view.ITopicViewPage;
import com.zhy.view.flowlayout.TagFlowLayout;
import java.util.List;

import butterknife.BindAnim;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TopicViewPageActivity extends AppCompatActivity implements ITopicViewPage {

    public static final String TAG = "TopicViewPageActivity";

    @BindView(R.id.topic_view_pager)
    ViewPager topicViewPager;
    @BindView(R.id.topic_view_pager_tags)
    TagFlowLayout topicTagLayout;
    @BindView(R.id.topic_view_pager_top_bar)
    LinearLayout topBarLayout;
    @BindView(R.id.progress_bar_topic_view_page)
    ProgressBar topicViewPageBar;
    @BindView(R.id.collect_button)
    ImageButton collectBtn;

    @BindAnim(R.anim.layout_in_above)
    Animation aboveLayoutIn;
    @BindAnim(R.anim.layout_out_above)
    Animation aboveLayoutOut;
    @BindAnim(R.anim.layout_in_below)
    Animation belowLayoutIn;
    @BindAnim(R.anim.layout_out_below)
    Animation belowLayoutOut;

    private TopicViewPagePresenter presenter;
    private TopicViewPageAdapter topicViewPageAdapter;
    private TopicTagAdapter topicTagAdapter;
    private boolean isFullScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_topic_view_page);

        ButterKnife.bind(this);

        int curTopicId = getIntent().getIntExtra(Constants.TOPIC_ID,-1);
        presenter = new TopicViewPagePresenterImpl(this, curTopicId);

        if (MySharedPreferences.getInstance().getBoolean(Constants.VIEW_PAGE_FULL_SCREEN, false)) {
            topBarLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void setTopicTagLayout(boolean isShow, Topic topic) {
        topicTagLayout.setVisibility(isShow?View.VISIBLE:View.GONE);
        topicTagAdapter = new TopicTagAdapter();
        topicTagAdapter.setCurTopicId(topic.getId());
        topicTagAdapter.setItemClickable(false);
        topicTagAdapter.setShowUnchecked(false);
        topicTagLayout.setAdapter(topicTagAdapter);
    }

    @Override
    public void setProgressBar(int progress) {
        topicViewPageBar.setProgress(progress, true);
    }

    @Override
    public void setTopicViewPage(List<Topic> topicList, int curPosition) {
        topicViewPageBar.setMax(topicList.size());
        topicViewPageBar.setProgress(curPosition+1);

        topicViewPageAdapter = new TopicViewPageAdapter(this,topicList);
        topicViewPager.setAdapter(topicViewPageAdapter);
        topicViewPager.setCurrentItem(curPosition);
        topicViewPager.setPageTransformer(false, new MyPagerTransformer());
        //滚动监听事件 刷新
        topicViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                presenter.curTopicChange(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void setTopicCollectBtn(boolean isCollect) {
        if (isCollect) {
            collectBtn.setImageDrawable(getDrawable(R.drawable.ic_collect));
        } else {
            collectBtn.setImageDrawable(getDrawable(R.drawable.ic_uncollect));
        }
    }

    @Override
    public void setTopicTagFlowLayout(int curTopicId) {
        topicTagAdapter.setCurTopicId(curTopicId);
        topicTagLayout.onChanged();
    }

    @OnClick(R.id.topic_info_btn)
    public void onClickInfoBtn() {
        Intent intent = new Intent(this, TopicInfoActivity.class);
        intent.putExtra(Constants.TOPIC_ID, presenter.getCurTopicId());
        startActivity(intent);
    }


    @OnClick(R.id.collect_button)
    public void onClickCollectBtn() {
        presenter.topicCollectChange();
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
            topicTagLayout.setVisibility(View.VISIBLE);
        } else {
            topBarLayout.startAnimation(aboveLayoutOut);
            topicTagLayout.setVisibility(View.GONE);
        }
    }

}
