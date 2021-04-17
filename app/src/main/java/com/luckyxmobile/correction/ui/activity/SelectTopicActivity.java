package com.luckyxmobile.correction.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.adapter.SelectTopicAdapter;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Paper;
import com.luckyxmobile.correction.model.bean.Tag;
import com.zj.myfilter.FiltrateBean;
import com.zj.myfilter.FlowPopWindow;
import org.litepal.LitePal;
import java.util.ArrayList;
import java.util.List;

/**
 * 选择错题
 *
 * @author yanghao
 */
public class SelectTopicActivity extends AppCompatActivity implements FlowPopWindow.OnConfirmClickListener {


    private ImageView nothingHint;
    private FlowPopWindow flowPopWindow;
    private List<FiltrateBean> filterList = new ArrayList<>();
    private SelectTopicAdapter selectTopicListAdapter;
    private Paper paper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_select);

        int paperId = getIntent().getIntExtra(Constants.PAPER_ID, -1);
        paper = LitePal.find(Paper.class, paperId);
        if (paper == null) throw new RuntimeException("SelectTopicActivity : paper is null");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.select_topics);
        toolbar.setTitle(toolbar.getTitle() + " - " + paper.getPaperName());
        setSupportActionBar(toolbar);
        nothingHint = findViewById(R.id.nothing_hint);

        findViewById(R.id.select_topic_finish_btn).setOnClickListener(view -> {
            paper.save();
            finish();
        });
        RecyclerView selectTopicRv = findViewById(R.id.select_topic_RecyclerView);
        selectTopicRv.setLayoutManager(new LinearLayoutManager(this));
        selectTopicListAdapter = new SelectTopicAdapter(paper);
        selectTopicRv.setAdapter(selectTopicListAdapter);
        if (selectTopicListAdapter.isEmpty()) {
            nothingHint.setVisibility(View.VISIBLE);
        } else {
            nothingHint.setVisibility(View.GONE);
        }
    }

    private void setFlowPopWindow() {
        FiltrateBean bookFiltrate = new FiltrateBean();
        bookFiltrate.setTypeName(getString(R.string.notebook));
        bookFiltrate.setChildren(new ArrayList<>());
        for (Book book : LitePal.findAll(Book.class)) {
            FiltrateBean.Children children = new FiltrateBean.Children();
            children.setId(book.getId());
            children.setValue(book.getName());
            bookFiltrate.getChildren().add(children);
        }

        FiltrateBean tagFiltrate = new FiltrateBean();
        tagFiltrate.setTypeName(getString(R.string.tag));
        tagFiltrate.setChildren(new ArrayList<>());
        for (Tag tag : LitePal.findAll(Tag.class)) {
            FiltrateBean.Children children = new FiltrateBean.Children();
            children.setId(tag.getId());
            children.setValue(tag.getTag_name());
            tagFiltrate.getChildren().add(children);
        }

        filterList.add(bookFiltrate);
        filterList.add(tagFiltrate);

        flowPopWindow = new FlowPopWindow(this, filterList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_topic, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.select_topic_filter) {
            if (flowPopWindow == null) {
                setFlowPopWindow();
            }
            flowPopWindow.setOnConfirmClickListener(this);
            flowPopWindow.showAsDropDown(findViewById(R.id.select_topic_filter));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfirmClick() {
        selectTopicListAdapter.onFilterListener(filterList);
    }
}
