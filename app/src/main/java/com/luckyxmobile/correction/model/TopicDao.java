package com.luckyxmobile.correction.model;

import com.luckyxmobile.correction.model.bean.Topic;

import java.util.List;

public interface TopicDao {

    Topic newTopic(int bookId);

    Topic saveTopic(int bookId, Topic topic);

    List<Topic> getTopicListByBookId(int bookId);

    List<Topic> getTopicListByCollection(boolean collection);

    boolean changeCurTopicCollection(int topicId);

    String getBookNameByTopic(Topic topic);

    void removeTopic(Topic topic);

    void removeTopicList(List<Topic> topicList);

}
