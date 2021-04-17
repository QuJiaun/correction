package com.luckyxmobile.correction.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.adapter.BookDetailAdapter;
import com.luckyxmobile.correction.adapter.TopicTagAdapter;
import com.luckyxmobile.correction.adapter.ViewHolderTopicItem;
import com.luckyxmobile.correction.model.bean.Tag;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.presenter.BookDetailViewPresenter;
import com.luckyxmobile.correction.presenter.impl.BookDetailViewPresenterImpl;
import com.luckyxmobile.correction.ui.dialog.AddTopicDialog;
import com.luckyxmobile.correction.ui.dialog.AlertDialog;
import com.luckyxmobile.correction.utils.DestroyActivityUtil;
import com.luckyxmobile.correction.view.IBookDetailView;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BookDetailActivity extends AppCompatActivity implements IBookDetailView
        , ViewHolderTopicItem.OnItemListener{

    public static final String TAG = "BookDetailActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.book_detail_tag_layout)
    TagFlowLayout tagFlowLayout;
    @BindView(R.id.recycler_correction)
    RecyclerView recyclerView;
    @BindView(R.id.nothing_hint)
    ImageView topicNothingImage;
    @BindView(R.id.loadBar)
    ProgressBar loadBar;

    private int book_id;
    private BookDetailAdapter adapter;
    private BookDetailViewPresenter presenter;
    private TopicTagAdapter topicTagAdapter;

    private AlertDialog alertDialog;
    private AddTopicDialog addTopicDialog;

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
        topicTagAdapter.setTextColor(getColor(R.color.white));
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
        if (adapter == null) {
            adapter = new BookDetailAdapter(this);
            LinearLayoutManager manager = new LinearLayoutManager(BookDetailActivity.this);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(adapter);
        }
        adapter.setTopics(topicList);
        adapter.notifyDataSetChanged();
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
                showAddTopicDialog();
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
        if (alertDialog == null) {
            alertDialog = new AlertDialog(this);
            alertDialog.create();
            alertDialog.setTitle(toolbar.getTitle());
            alertDialog.setMessage(R.string.confirm_delete_topic);
            alertDialog.setPositiveButton(R.string.ensure, () -> {
                loadBar.setVisibility(View.VISIBLE);
                presenter.removeTopicList(adapter.getTopicsByDelete());
                alertDialog.dismiss();
            });
            alertDialog.setNegativeButton(R.string.cancel, () -> {
                adapter.setDeleteMode(false);
                alertDialog.dismiss();
            });
        }

        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
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

    private void showAddTopicDialog() {
        if (addTopicDialog == null) {
            addTopicDialog = new AddTopicDialog(this);
            addTopicDialog.create();
            addTopicDialog.setFromAlbum(() -> addTopicFrom(true));
            addTopicDialog.setFromCamera(() -> addTopicFrom(false));
        }

        if (!addTopicDialog.isShowing()) {
            addTopicDialog.show();
        }
    }

    public void addTopicFrom(boolean album) {
        DestroyActivityUtil.add(this);
        Intent intent = CropImageActivity.getIntent(this, album, true);
        intent.putExtra(Constants.FROM_ACTIVITY, TAG);
        intent.putExtra(Constants.BOOK_ID, book_id == 1?-1:book_id);
        startActivity(intent);
        if (addTopicDialog.isShowing()) {
            addTopicDialog.dismiss();
        }
    }
}
