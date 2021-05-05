package com.luckyxmobile.correction.model.impl;

import android.text.TextUtils;

import com.luckyxmobile.correction.model.BeanUtils;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.BookDao;
import com.luckyxmobile.correction.model.DaoListener;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.utils.FilesUtils;

import org.litepal.LitePal;

import java.util.List;

/**
 * @author yanghao
 * @date 2019/8/3
 * @author qujiajun
 * @update 2021-01-16
 */
public class BookDaoImpl implements BookDao {

    private final FilesUtils filesUtils = FilesUtils.getInstance();
    private final DaoListener listener;

    public BookDaoImpl(DaoListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean saveBook(Book book) {

        if (book.isSaved()) return updateBook(book);

        //判断 命名是否重复
        if (existBookName(book.getName())) {
            listener.onToast(book.getName() + " 已存在！");
            return false;
        }

        book.save();

        FilesUtils.getInstance().saveBookInfoFile(book);

        //迁移错题本封面文件
        if (!TextUtils.isEmpty(book.getCover())) {
            String newPath = filesUtils.getBookCoverPath(book);
            filesUtils.copyFile(book.getCover(), newPath);
            book.setCover(newPath);
            book.save();
        }

        return true;
    }

    @Override
    public void removeBook(Book book) {
        //异步删除本地文件
        filesUtils.deleteBookDir(book, listener);
        //获取错题本下的错题集合
        List<Topic> topicList = LitePal.where("book_id=?",
                String.valueOf(book.getId())).find(Topic.class);
        //遍历错题集合，删除错题图片
        for (Topic topic : topicList) {
            List<TopicImage> topicImageList = BeanUtils.findTopicAll(topic);
            BeanUtils.removeTopicInTag(topic);
            BeanUtils.removeTopicImageList(topicImageList);
            topic.delete();
        }
        //删除错题本
        book.delete();
    }

    @Override
    public boolean updateBook(Book book) {

        if (!book.isSaved()) return saveBook(book);

        if (existBookName(book.getName())) {
            Book tmp = LitePal.find(Book.class, book.getId());
            if (!book.getName().equals(tmp.getName())) {
                listener.onToast(book.getName() + " 已存在！");
                return false;
            }
        }

        if (TextUtils.isEmpty(book.getCover())) {
            book.setToDefault("cover");
        } else {
            String newPath = filesUtils.getBookCoverPath(book);
            if (!book.getCover().equals(newPath)) {
                filesUtils.copyFile(book.getCover(), newPath);
                book.setCover(newPath);
            }
        }

        book.save();
        return true;
    }

    private boolean existBookName(String name) {
        List<Book> books = LitePal.where("name=?", name).find(Book.class);
        return books != null && !books.isEmpty();
    }
}
