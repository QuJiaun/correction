package com.luckyxmobile.correction.model.impl;

import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.TopicDao;
import com.luckyxmobile.correction.model.DaoListener;
import com.luckyxmobile.correction.utils.FilesUtils;

import org.litepal.LitePal;

import java.util.List;

public class TopicDaoImpl implements TopicDao {

    private final FilesUtils filesUtils = FilesUtils.getInstance();
    private final DaoListener daoListener;

    public TopicDaoImpl(DaoListener daoListener) {
        this.daoListener = daoListener;
    }

    @Override
    public Topic newTopic(int bookId) {
        return saveTopic(bookId, new Topic(bookId));
    }

    @Override
    public Topic saveTopic(int bookId, Topic topic) {


        if (bookId <= 1) throw new RuntimeException("saveTopic : book is null");

        if (topic == null) throw new RuntimeException("saveTopic : topic is null");

        topic.setBook_id(bookId);
        topic.save();

        //创建对应文件夹
        filesUtils.saveTopicInfoFile(topic);

        return topic;
    }

    @Override
    public boolean changeCurTopicCollection(int topicId) {
        Topic topic = LitePal.find(Topic.class, topicId);
        boolean collection = topic.isCollection();

        if (collection) {
            topic.setCollection(false);
            topic.setToDefault("collection");
        } else {
            topic.setCollection(true);
        }

        topic.save();
        return !collection;
    }

    @Override
    public String getBookNameByTopic(Topic topic) {
        Book book = LitePal.find(Book.class, topic.getBook_id());
        if (book == null) throw new RuntimeException("this topic " + topic.getId() + " is error");
        return book.getName();
    }

    @Override
    public List<Topic> getTopicListByBookId(int bookId) {
        return LitePal.where("book_id=?",String.valueOf(bookId)).find(Topic.class);
    }

    @Override
    public List<Topic> getTopicListByCollection(boolean collection) {
        return LitePal.where("collection=?", String.valueOf(collection)).find(Topic.class);
    }

    @Override
    public void removeTopic(Topic topic) {
        filesUtils.deleteTopicDir(topic, daoListener);

        LitePal.deleteAll("TopicImage", "topic_id=?", String.valueOf(topic.getId()));
        LitePal.delete(Topic.class, topic.getId());
    }

    @Override
    public void removeTopicList(List<Topic> topicList) {

        filesUtils.deleteTopicDirList(topicList, daoListener);
        //删除级联表
        for (Topic topic : topicList) {
            LitePal.delete(Topic.class, topic.getId());
        }
    }
}
