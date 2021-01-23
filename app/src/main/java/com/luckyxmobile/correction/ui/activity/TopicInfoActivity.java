package com.luckyxmobile.correction.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.adapter.TopicInfoAdapter;
import com.luckyxmobile.correction.adapter.TopicTagAdapter;
import com.luckyxmobile.correction.model.bean.Tag;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.utils.ImageUtil;
import com.luckyxmobile.correction.view.ITopicInfoView;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;
import com.zhy.view.flowlayout.TagFlowLayout;
import org.litepal.LitePal;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author rfa
 * 错题详情界面
 *
 * LiuGen
 *  2019/07/24
 * 关于错题详情页面的信息筛选显示，修改按钮的优化
 *
 * @date 2019/07/30
 * 当前存在问题：
 * 1.点击查看大图
 * 2.图片显示——长图被压缩，短图被拉伸，效果不好
 * 3.从数据库读出原有文字描述回显的添加文字描述的对话框
 * 4.光标显示移动到文本末端
 * 5.活动的进入方式
 *
 * 2019/08/08
 * 当前存在的问题：
 * 如果拍摄的图片过小，在该页面显示的时候，
 * 图片会被不等比例拉伸，显示出来比较难看
 *
 * 2019/07/31
 * @author lg
 * 题目详情页面查看时默认隐藏工具栏
 *
 * 2019/08/28
 * @author qjj
 * 每个模块显示多张图片（recyclerView），规整代码（将topic进行唯一化）
 *
 * @date 2020/02/08
 * @author qjj
 * 规整
 */

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        ButterKnife.bind(this);

        // 初始化布局
        initView();

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
    public void setTopicTags(List<Tag> tags) {
        TopicTagAdapter tagAdapter = new TopicTagAdapter(tags, currentTopic);
        tagAdapter.setClickable(false);
        tagAdapter.setShowUnchecked(false);
        tagAdapter.setTextColor(getColor(R.color.white));
        tagLayout.setAdapter(tagAdapter);
    }

    @Override
    public void addTopicImageFinished(TopicImage topicImage) {
        topicInfoAdapter.addTopicImage(topicImage);
    }

    @Override
    public void addTopicTextFinished(String text) {
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

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.topic_menu_like:
                break;

            case R.id.topic_menu_add_text:
                break;

            case R.id.topic_menu_add_camera:
                break;

            case R.id.topic_menu_add_album:
                break;

            case R.id.topic_menu_show_original:
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
