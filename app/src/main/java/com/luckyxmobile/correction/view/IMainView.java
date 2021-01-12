package com.luckyxmobile.correction.view;

import android.content.Context;
import android.content.Intent;

import com.luckyxmobile.correction.model.bean.Book;

import java.util.List;

public interface IMainView {


    void setHeadBookList(List<Book> bookList);

    /**
     * 完成错题本的添加后
     * 通知MainView，调用adapter 刷新页面
     * @param book 添加的错题本
     */
    void addBookFinished(Book book);

    /**
     * 完成错题本的修改后
     * 通知MainView，调用adapter 刷新页面
     * @param book 修改的错题本
     */
    void alterBookFinished(Book book);

    /**
     * 完成删除错题本后
     * 通知MainView，调用adapter 刷新页面（包括最近错题）
     * @param book 删除的错题本
     */
    void deleteBookFinished(Book book);

    void OnToastFinished(String showLog);

    /**
     * 获取MainView 的 context
     * @return context
     */
    Context getMainViewContext();


    void startActivityForResult(Intent intent, int requestCode);

}
