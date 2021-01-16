package com.luckyxmobile.correction.model;

import com.luckyxmobile.correction.model.bean.Book;

/**
 * @author yanghao
 * @date 2019/8/3
 * @update 2021-01-16
 */
public interface BookDao {

    void saveBook(Book book);

    void removeBook(Book book);

    void alterBookCover(Book book, String coverImagePath);
}
