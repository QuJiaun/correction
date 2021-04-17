package com.luckyxmobile.correction.model;

import com.luckyxmobile.correction.model.bean.Book;

/**
 * @author yanghao
 * @date 2019/8/3
 * @update 2021-01-16
 */
public interface BookDao {

    boolean saveBook(Book book);

    boolean updateBook(Book book);

    void removeBook(Book book);
}
