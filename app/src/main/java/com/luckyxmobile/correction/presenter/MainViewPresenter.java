package com.luckyxmobile.correction.presenter;

import com.luckyxmobile.correction.model.bean.Book;

public interface MainViewPresenter {

    void init();

    void saveBook(Book book);

    void alterBookInfo(Book book);

    void removeBook(Book book);

}
