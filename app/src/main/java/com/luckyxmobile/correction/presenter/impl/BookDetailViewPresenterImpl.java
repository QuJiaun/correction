package com.luckyxmobile.correction.presenter.impl;

import android.content.Context;
import android.widget.Toast;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.global.MyPreferences;
import com.luckyxmobile.correction.model.TopicDao;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.impl.TopicDaoImpl;
import com.luckyxmobile.correction.presenter.BookDetailViewPresenter;
import com.luckyxmobile.correction.model.DaoListener;
import com.luckyxmobile.correction.view.IBookDetailView;

import org.litepal.LitePal;

import java.util.Collections;
import java.util.List;

public class BookDetailViewPresenterImpl implements BookDetailViewPresenter, DaoListener {


    private final Context context;

    private List<Topic> topicList;

    private boolean isNewest;

    private IBookDetailView bookDetailView;
    private TopicDao topicDao;

    public BookDetailViewPresenterImpl(Context context) {

        this.context = context;
        this.isNewest = MyPreferences.getInstance().getBoolean(Constants.IS_NEWEST_ORDER, true);

        bookDetailView = (IBookDetailView) context;
        topicDao = new TopicDaoImpl(this);

    }

    @Override
    public void init(int bookId) {
        Book curBook = LitePal.find(Book.class, bookId);
        if (curBook == null) throw new RuntimeException("BookDetailActivity : curBook is null");

        if (curBook.getId() == 1 || curBook.getName().equals(context.getString(R.string.favorites))) {
            topicList = topicDao.getTopicListByCollection(true);
        } else {
            topicList = topicDao.getTopicListByBookId(bookId);
        }

        if (!isNewest) {
            Collections.reverse(topicList);
        }

        bookDetailView.setToolBar(curBook.getName());
        bookDetailView.setTopicListRv(topicList);
        bookDetailView.setTagLayout();
    }

    @Override
    public void setIsNewest(boolean isNewest) {
        if (this.isNewest != isNewest) {
            this.isNewest = isNewest;
            Collections.reverse(topicList);
            bookDetailView.setTopicListRv(topicList);
            MyPreferences.getInstance().putBoolean(Constants.IS_NEWEST_ORDER, isNewest);
        }
    }

    @Override
    public boolean isNewest() {
        return isNewest;
    }

    @Override
    public void removeTopicList(List<Topic> topicList) {
        topicDao.removeTopicList(topicList);
    }

    @Override
    public void onToast(String log) {
        Toast.makeText(context, log, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteFinished() {
        onToast("删除完成");
        bookDetailView.removeTopicListFinish(topicList.isEmpty());
    }
}
