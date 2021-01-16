package com.luckyxmobile.correction.model;

import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Tag;
import com.luckyxmobile.correction.model.bean.Topic;

import java.util.List;

public interface TagDao {

    List<Topic> findTopicByTagInBook(Book book, Tag tag);

    List<Tag> findTagByTopic(Topic topic);
}
