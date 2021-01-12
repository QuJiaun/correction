package com.luckyxmobile.correction.presenter;

import android.content.Context;

import com.luckyxmobile.correction.model.bean.Book;

public interface OnBookFinishedListener {

    Context getContext();

    void addSuccess(Book book);

    void alterSuccess(Book book);

    void deleteSuccess();

}
