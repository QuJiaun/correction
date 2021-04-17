package com.luckyxmobile.correction.presenter.impl;

import android.content.Context;

import com.luckyxmobile.correction.model.BookDao;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.impl.BookDaoImpl;
import com.luckyxmobile.correction.model.DaoListener;
import com.luckyxmobile.correction.presenter.MainViewPresenter;
import com.luckyxmobile.correction.view.MainView;

import org.litepal.LitePal;

/**
 * MainView的presenter层，负责MainView与Model层（数据库）的联系
 * 提供功能：addBook(), alterBook(), deleteBook()，通知model层进行操作，并返回结果给MainView
 * @author qujiajun 2020/12/08
 */
public class MainViewPresenterImpl implements  MainViewPresenter, DaoListener {

    private final MainView mainView;
    private final BookDao bookDao;

    public MainViewPresenterImpl(Context context){
        this.mainView = (MainView) context;
        bookDao = new BookDaoImpl(this);
    }

    @Override
    public void init() {
        mainView.setHeadBookRv(LitePal.findAll(Book.class));
        mainView.setRecentTopicRv(LitePal.order("create_date desc").limit(10).find(Topic.class, true));
    }

    @Override
    public boolean saveBook(Book book) {
        if (bookDao.saveBook(book)) {
            mainView.saveBookFinished(book);
            return true;
        }
        return false;
    }

    @Override
    public boolean alterBookInfo(Book book) {
        if (bookDao.updateBook(book)) {
            mainView.updateBookFinished(book);
            return true;
        }
        return false;
    }

    @Override
    public void removeBook(Book book) {
        bookDao.removeBook(book);
    }

    @Override
    public void onToast(String log) {
        mainView.onToast(log);
    }

    @Override
    public void onDeleteFinished() {
        mainView.deleteBookFinished();
        mainView.onToast("删除完成");
    }
}
