package com.luckyxmobile.correction.presenter;

import com.luckyxmobile.correction.model.bean.Book;

public interface MainViewPresenter {

    void init();

    boolean saveBook(Book book);

    boolean alterBookInfo(Book book);

    void removeBook(Book book);

}
