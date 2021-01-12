package com.luckyxmobile.correction.model.impl;

import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.BookDao;

import org.litepal.LitePal;

import java.util.List;

/**
 * @author yanghao
 * @date 2019/8/3
 * @time 9:15
 */
public class BookDaoImpl implements BookDao {
    @Override
    public String selectBookNameByTopic(Topic topic) {
        List<Book> bookList = LitePal.where("id = ?", String.valueOf(topic.getBook_id())).find(Book.class);
        String book_name = null;
        for (Book book : bookList) {
            book_name = book.getName();
        }
        return book_name
    }
}
