package com.luckyxmobile.correction.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.adapter.BookDetailAdapter;
import com.luckyxmobile.correction.adapter.TopicTagAdapter;
import com.luckyxmobile.correction.adapter.ViewHolderTopicItem;
import com.luckyxmobile.correction.global.MySharedPreferences;
import com.luckyxmobile.correction.model.bean.Tag;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.presenter.BookDetailViewPresenter;
import com.luckyxmobile.correction.presenter.impl.BookDetailViewPresenterImpl;
import com.luckyxmobile.correction.ui.dialog.AddTopicImageDialog;
import com.luckyxmobile.correction.utils.DestroyActivityUtil;
import com.luckyxmobile.correction.view.IBookDetailView;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BookDetailActivity extends AppCompatActivity implements IBookDetailView
        , ViewHolderTopicItem.OnItemListener
        , AddTopicImageDialog.OnClickListener{

    public static final String TAG = "BookDetailActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.book_detail_tag_layout)
    TagFlowLayout tagFlowLayout;
    @BindView(R.id.recycler_correction)
    RecyclerView recyclerView;
    @BindView(R.id.book_topic_nothing)
    ImageView topicNothingImage;
    @BindView(R.id.loadBar)
    ProgressBar loadBar;

    private int book_id;
    private BookDetailAdapter adapter;
    private BookDetailViewPresenter presenter;
    private TopicTagAdapter topicTagAdapter;

    private MenuItem topicSortMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去除顶部标题栏
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_book_detail);
        ButterKnife.bind(this);

        // 获取Intent传过来的数据集
        book_id = getIntent().getIntExtra(Constants.BOOK_ID, -1);
        presenter = new BookDetailViewPresenterImpl(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.init(book_id);
        setTopicsSort(presenter.isNewest());
    }

    @Override
    public void setToolBar(String barName) {
        setSupportActionBar(toolbar);
        toolbar.setTitle(barName);
    }

    @Override
    public void setTagLayout() {
        topicTagAdapter = new TopicTagAdapter();
        topicTagAdapter.setCurTopicId(-1);
        topicTagAdapter.setTopicIdList(adapter.getTopics());
        topicTagAdapter.setItemClickable(true);
        topicTagAdapter.setShowUnchecked(true);
        tagFlowLayout.setAdapter(topicTagAdapter);
        tagFlowLayout.setOnSelectListener(selectPosSet -> {
            if (selectPosSet.isEmpty()) {
                adapter.onFilterListener(null);
            } else {
                List<Tag> tagList = new ArrayList<>();
                for (int pos : selectPosSet) {
                    tagList.add(topicTagAdapter.getItem(pos));
                }
                adapter.onFilterListener(tagList);
            }
            if (adapter.isDeleteMode()) {
                adapter.setDeleteMode(false);
            }
        });
    }

    @Override
    public void setTopicListRv(List<Topic> topicList) {

        if (topicList == null || topicList.isEmpty()) {
            topicNothingImage.setVisibility(View.VISIBLE);
        } else {
            topicNothingImage.setVisibility(View.GONE);
        }

        adapter = new BookDetailAdapter(this, topicList);
        LinearLayoutManager manager = new LinearLayoutManager(BookDetailActivity.this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
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
        getMenuInflater().inflate(R.menu.toolbar_menu_bookdetail, menu);
        topicSortMenuItem = menu.findItem(R.id.sort_time);
        setTopicsSort(presenter.isNewest());
        adapter.setDeleteMenuItem(menu.findItem(R.id.book_topic_delete));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //删除选中错题
            case R.id.book_topic_delete:
                deleteSelectedTopic();
                break;
            // 排序
            case R.id.sort_time:
                boolean isNewest = presenter.isNewest();
                presenter.setIsNewest(!isNewest);
                setTopicsSort(!isNewest);
                break;
            //在错题本内添加错题
            case R.id.add_topic_toolbar:
                if (adapter.isDeleteMode()) {
                    adapter.setDeleteMode(false);
                }
                AddTopicImageDialog.show(this);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (adapter != null && adapter.isDeleteMode()) {
            adapter.setDeleteMode(false);
        } else {
            super.onBackPressed();
        }
    }

    private void deleteSelectedTopic() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete)
                .setIcon(R.drawable.ic_delete_red_24dp)
                .setMessage(R.string.confirm_delete_topic)
                .setPositiveButton(R.string.ensure, (dialog, which) -> {
                    loadBar.setVisibility(View.VISIBLE);
                    presenter.removeTopicList(adapter.getTopicsByDelete());
                }).setNegativeButton(R.string.cancel, null).show();
    }

    private void setTopicsSort(boolean isNewest){
        if (topicSortMenuItem != null) {
            topicSortMenuItem.setIcon(isNewest?
                    getDrawable(R.drawable.ic_arrow_downward)
                    : getDrawable(R.drawable.ic_arrow_upward));
        }
    }


    @Override
    public void removeTopicListFinish(boolean isEmpty) {
        adapter.removeTopicList();
        loadBar.setVisibility(View.GONE);
        topicNothingImage.setVisibility(isEmpty?View.VISIBLE:View.GONE);
    }

    @Override
    public void onClickItem(Topic topic) {
        Intent intent = new Intent(this, TopicViewPageActivity.class);
        intent.putExtra(Constants.TOPIC_ID, topic.getId());
        startActivity(intent);
    }

    @Override
    public void addTopicFromCamera() {
        DestroyActivityUtil.addDestroyActivityToMap(this, TAG);
        MySharedPreferences.getInstance().putString(Constants.FROM_ACTIVITY, TAG);
        MySharedPreferences.getInstance().putInt(Constants.CURRENT_BOOK_ID, book_id == 1? -1: book_id);
        Intent intent = CropImageActivity.getCropImageActivityIntent(this, false, true);
        startActivity(intent);
    }

    @Override
    public void addTopicFromAlbum() {
        DestroyActivityUtil.addDestroyActivityToMap(this, TAG);
        MySharedPreferences.getInstance().putString(Constants.FROM_ACTIVITY, TAG);
        MySharedPreferences.getInstance().putInt(Constants.CURRENT_BOOK_ID, book_id == 1? -1: book_id);
        Intent intent = CropImageActivity.getCropImageActivityIntent(this, true, true);
        startActivity(intent);
    }
}
