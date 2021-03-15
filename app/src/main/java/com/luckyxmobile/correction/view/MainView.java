package com.luckyxmobile.correction.view;

import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Topic;

import java.util.List;

public interface MainView {


    void setHeadBookRv(List<Book> bookList);

    void setRecentTopicRv(List<Topic> topicList);

    /**
     * 完成错题本的添加后
     * 通知MainView，调用adapter 刷新页面
     * @param book 添加的错题本
     */
    void saveBookFinished(Book book);

    /**
     * 完成错题本的修改后
     * 通知MainView，调用adapter 刷新页面
     * @param book 修改的错题本
     */
    void updateBookFinished(Book book);

    /**
     * 完成删除错题本后
     */
    void deleteBookFinished();

    void onToast(String showLog);
}
