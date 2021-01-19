package com.luckyxmobile.correction.presenter;

import com.luckyxmobile.correction.model.bean.Book;

public interface MainViewPresenter {

    /**
     * 添加错题本：弹出添加dialog进行操作
     */
    void addBook(Book book);

    void alterBookInfo(Book book);

    void deleteBook(Book book);

    void openCamera();

    void openAlbum();
}
