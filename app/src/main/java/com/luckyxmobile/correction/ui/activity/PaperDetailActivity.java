package com.luckyxmobile.correction.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.adapter.PaperDetailAdapter;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Paper;
import com.luckyxmobile.correction.ui.callback.ItemTouchCallback;

import org.litepal.LitePal;

import java.lang.reflect.Method;

/**
 * @author ChangHao
 */
public class PaperDetailActivity extends AppCompatActivity {

    public static final String TAG = "PaperDetailActivity";

    private PaperDetailAdapter paperDetailAdapter;
    private Paper curPaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_paper_detail);

        int paperId = getIntent().getIntExtra(Constants.PAPER_ID, -1);
        curPaper = LitePal.find(Paper.class, paperId);
        if (curPaper == null) {
            throw new RuntimeException(TAG + ": paper is null");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.test_page) + " - " +curPaper.getPaperName());
        setSupportActionBar(toolbar);

        setPaperDetailRv();

    }

    @Override
    protected void onResume() {
        super.onResume();
        paperDetailAdapter.refreshCurPaper(curPaper.getId());
        paperDetailAdapter.notifyDataSetChanged();
    }

    private void setPaperDetailRv() {
        RecyclerView paperDetailRv = findViewById(R.id.paper_detail_rv);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        paperDetailRv.setLayoutManager(manager);
        paperDetailAdapter = new PaperDetailAdapter();
        paperDetailAdapter.refreshCurPaper(curPaper.getId());
        paperDetailRv.setAdapter(paperDetailAdapter);
        //给复习卷的item设置长按拖动
        ItemTouchHelper.Callback callback = new ItemTouchCallback(paperDetailAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(paperDetailRv);

    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    /**
     * 加载菜单选项
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_paper_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.paper_print:

                break;
            case R.id.paper_regroup:
                Intent intent = new Intent(this, SelectTopicActivity.class);
                intent.putExtra(Constants.PAPER_ID, curPaper.getId());
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
