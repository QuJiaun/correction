package com.luckyxmobile.correction.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.adapter.TopicInfoAdapter;
import com.luckyxmobile.correction.adapter.TopicTagAdapter;
import com.luckyxmobile.correction.global.MySharedPreferences;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.presenter.TopicInfoViewPresenter;
import com.luckyxmobile.correction.presenter.impl.TopicInfoViewPresenterImpl;
import com.luckyxmobile.correction.ui.dialog.SetTopicTextDialog;
import com.luckyxmobile.correction.utils.DestroyActivityUtil;
import com.luckyxmobile.correction.view.ITopicInfoView;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.lang.reflect.Method;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


@SuppressLint("NonConstantResourceId")
public class TopicInfoActivity extends AppCompatActivity implements ITopicInfoView,
        TopicInfoAdapter.TopicInfoListener, SetTopicTextDialog.OnBtnClickListener {

    public static final  String TAG = TopicInfoActivity.class.getSimpleName();

    @BindView(R.id.topic_info_toolbar)
    Toolbar toolbar;

    private TopicInfoAdapter topicInfoAdapter;

    @BindView(R.id.topics_rv)
    RecyclerView topicsRv;
    @BindView(R.id.topic_text)
    TextView topicTextTv;
    @BindView(R.id.topic_create_date)
    TextView topicCreateDateTv;
    @BindView(R.id.tag_layout)
    TagFlowLayout tagLayout;

    private MenuItem collectionMenuItem;

    private int curTopicId;
    private TopicInfoViewPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        ButterKnife.bind(this);
        presenter = new TopicInfoViewPresenterImpl(this);

        //获取唯一的错题id
        curTopicId = getIntent().getIntExtra(Constants.TOPIC_ID, -1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.initTopicInfo(curTopicId);
    }

    @Override
    public void setToolbar(String toolbarName) {
        toolbar.setTitle(toolbarName);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public void setTopicCollection(boolean collection) {
        if (collectionMenuItem != null) {
            collectionMenuItem.setIcon(collection?
                    getDrawable(R.drawable.ic_collect):
                    getDrawable(R.drawable.ic_uncollect)
            );
        }
    }

    @Override
    public void setTopicText(String text) {
        if (TextUtils.isEmpty(text)) {
            topicTextTv.setVisibility(View.GONE);
        } else {
            topicTextTv.setVisibility(View.VISIBLE);
            topicTextTv.setText(text);
        }
    }

    @Override
    public void setTopicImages(List<TopicImage> topicImages) {

        topicInfoAdapter = new TopicInfoAdapter(this, topicImages);
        topicInfoAdapter.setRemoveMode(false);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        topicsRv.setLayoutManager(mLayoutManager);
        topicsRv.setItemAnimator(new DefaultItemAnimator());
        topicsRv.setAdapter(topicInfoAdapter);
        topicsRv.setNestedScrollingEnabled(false);
    }


    @Override
    public void setTopicTags(Topic topic) {
        TopicTagAdapter tagAdapter = new TopicTagAdapter();
        tagAdapter.setCurTopicId(topic.getId());
        tagAdapter.setItemClickable(false);
        tagAdapter.setShowUnchecked(false);
        tagAdapter.setTextColor(getColor(R.color.white));
        tagLayout.setAdapter(tagAdapter);
    }

    @Override
    public void setTopicCreateDate(String date) {
        topicCreateDateTv.setText(date);
    }

    private void showTopicTextDialog() {
        SetTopicTextDialog setTopicTextDialog = new SetTopicTextDialog(this);
        setTopicTextDialog.init(presenter.getCurTopic().getText());
        setTopicTextDialog.show();
    }

    @Override
    public void removeTopicImage(TopicImage topicImage) {
        presenter.removeTopicImage(topicImage);
    }

    @Override
    public void onClickTopicImage(TopicImage topicImage) {

    }

    @OnClick(R.id.set_tag)
    public void onClickSetTagBtn() {
        Intent intent = new Intent(TopicInfoActivity.this, TagManagerActivity.class);
        intent.putExtra(Constants.TOPIC_ID, presenter.getCurTopic().getId());
        startActivity(intent);
    }

    @OnClick(R.id.topic_text)
    public void onClickTopicText() {
        showTopicTextDialog();
    }

    @Override
    public void onTopicTextBtnEnsure(String text) {
        presenter.setTopicText(text);
        setTopicText(text);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            //显示menu icon与txt
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass()
                        .getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_topic, menu);
        collectionMenuItem = menu.findItem(R.id.topic_menu_like);
        menu.findItem(R.id.topic_menu_show_original).setChecked(
                MySharedPreferences.getInstance().getBoolean(Constants.SHOW_ORIGINAL, true));
        setTopicCollection(presenter.getCurTopic().isCollection());
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.topic_menu_like:
                presenter.setTopicCollection();
                break;

            case R.id.topic_menu_add_text:
                showTopicTextDialog();
                break;

            case R.id.topic_menu_add_camera:
                MySharedPreferences.getInstance().putString(Constants.FROM_ACTIVITY, TAG);
                MySharedPreferences.getInstance().putInt(Constants.CURRENT_BOOK_ID, presenter.getCurTopic().getBook_id());
                MySharedPreferences.getInstance().putInt(Constants.CURRENT_TOPIC_ID, presenter.getCurTopic().getId());
//                DestroyActivityUtil.addDestroyActivityToMap(this, TAG);

                startActivity(CropImageActivity.getCropImageActivityIntent(this, false, true));
                break;

            case R.id.topic_menu_add_album:
                MySharedPreferences.getInstance().putString(Constants.FROM_ACTIVITY, TAG);
                MySharedPreferences.getInstance().putInt(Constants.CURRENT_BOOK_ID, presenter.getCurTopic().getBook_id());
                MySharedPreferences.getInstance().putInt(Constants.CURRENT_TOPIC_ID, presenter.getCurTopic().getId());
//                DestroyActivityUtil.addDestroyActivityToMap(this, TAG);

                startActivity(CropImageActivity.getCropImageActivityIntent(this, true, true));
                break;

            case R.id.topic_menu_show_original:
                item.setChecked(!item.isChecked());
                MySharedPreferences.getInstance().putBoolean(Constants.SHOW_ORIGINAL, item.isChecked());
                topicInfoAdapter.setShowOriginalImage(item.isChecked());
                break;

            case R.id.topic_menu_edit:
                boolean removeMode = topicInfoAdapter.isRemoveMode();
                topicInfoAdapter.setRemoveMode(!removeMode);
                topicInfoAdapter.notifyDataSetChanged();
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (topicInfoAdapter.isRemoveMode()) {
            topicInfoAdapter.setRemoveMode(false);
            topicInfoAdapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }
}
