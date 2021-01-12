package com.luckyxmobile.correction.model;

import com.luckyxmobile.correction.presenter.OnBookFinishedListener;

public interface BookModel {

    void setOnBookFinishedListener(OnBookFinishedListener listener);

    void newBook(String bookName, String bookCoverPath);

    void alterBook(int bookId, String bookName, String bookCoverPath);

    void deleteBook(int bookId);
}
