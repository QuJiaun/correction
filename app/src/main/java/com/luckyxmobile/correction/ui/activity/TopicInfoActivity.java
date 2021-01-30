package com.luckyxmobile.correction.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.adapter.TopicInfoAdapter;
import com.luckyxmobile.correction.adapter.TopicTagAdapter;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.presenter.TopicInfoViewPresenter;
import com.luckyxmobile.correction.presenter.impl.TopicInfoViewPresenterImpl;
import com.luckyxmobile.correction.view.ITopicInfoView;
import com.zhy.view.flowlayout.TagFlowLayout;
import org.litepal.LitePal;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


@SuppressLint("NonConstantResourceId")
public class TopicInfoActivity extends AppCompatActivity implements ITopicInfoView ,
    TopicInfoAdapter.TopicInfoListener {

    public static final  String TAG = "TopicActivity";

    @BindView(R.id.topic_info_toolbar)
    Toolbar toolbar;

    private TopicInfoAdapter topicInfoAdapter;

    @BindView(R.id.topics_rv)
    RecyclerView topicsRv;
    @BindView(R.id.topic_text)
    LinearLayout topicTextLayout;
    @BindView(R.id.add_topic_text)
    EditText topicEditText;
    @BindView(R.id.topic_create_date)
    TextView topicCreateDateTv;

    @BindView(R.id.tag_layout)
    TagFlowLayout tagLayout;

    private Topic currentTopic;

    private TopicInfoViewPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        ButterKnife.bind(this);
        presenter = new TopicInfoViewPresenterImpl(this);

        // 初始化布局
        initView();

        presenter.initTopicInfo(currentTopic);
    }

    /**
     * 界面布局初始化，并设置初始参数
     */
    private void initView() {

        //获取唯一的错题id
        int topicId = getIntent().getIntExtra(Constants.TOPIC_ID, 0);

        currentTopic = LitePal.find(Topic.class, topicId);

        topicCreateDateTv.setText(new SimpleDateFormat("yyyy/MM/dd",
            Locale.getDefault()).format(currentTopic.getCreate_date()));

        toolbar.setTitle(currentTopic.getBook().getName());
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        if (TextUtils.isEmpty(currentTopic.getText())) {
            topicTextLayout.setVisibility(View.GONE);
        } else {
            topicTextLayout.setVisibility(View.VISIBLE);
            topicEditText.setText(currentTopic.getText());
        }
    }

    @Override
    public void setTopicImages(List<TopicImage> topicImages) {
        topicInfoAdapter = new TopicInfoAdapter(this, topicImages);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        topicsRv.setLayoutManager(mLayoutManager);
        topicsRv.setItemAnimator(new DefaultItemAnimator());
        topicsRv.setAdapter(topicInfoAdapter);
    }

    @Override
    public void setTopicTags() {
        TopicTagAdapter tagAdapter = new TopicTagAdapter(null);
        tagAdapter.setCurTopic(currentTopic);
        tagAdapter.setItemClickable(false);
        tagAdapter.setShowUnchecked(false);
        tagAdapter.setTextColor(getColor(R.color.white));
        tagLayout.setAdapter(tagAdapter);
    }

    @Override
    public void addTopicImageFinished(TopicImage topicImage) {
        topicInfoAdapter.addTopicImage(topicImage);
    }

    @Override
    public void setTopicTextFinished(String text) {
        if (TextUtils.isEmpty(text)) {
            topicTextLayout.setVisibility(View.GONE);
            topicEditText.setText("");
        } else {
            topicTextLayout.setVisibility(View.VISIBLE);
            topicEditText.setText(text);
        }
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
        Intent intent = new Intent(TopicInfoActivity.this, TagActivity.class);
        intent.putExtra(Constants.TOPIC_ID, currentTopic.getId());
        startActivity(intent);
    }

    @OnClick({R.id.topic_text, R.id.add_topic_text})
    public void onClickTopicText() {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_topic, menu);

        MenuItem item = findViewById(R.id.topic_menu_like);
        item.setChecked(currentTopic.isCollection());
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.topic_menu_like:
                item.setChecked(!item.isChecked());
                break;

            case R.id.topic_menu_add_text:
                presenter.showTopicTextDialog();
                break;

            case R.id.topic_menu_add_camera:

                break;

            case R.id.topic_menu_add_album:

                break;

            case R.id.topic_menu_show_original:
                item.setChecked(!item.isChecked());
                topicInfoAdapter.setShowOriginalImage(item.isChecked());
                break;
            default:
                break;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_CODE) {

            }
        }
    }
}
