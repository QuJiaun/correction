package com.luckyxmobile.correction.model;

import com.luckyxmobile.correction.model.bean.TopicImage;

import java.util.List;

public interface TopicImageDao {

    void saveTopicImage(int topicId, TopicImage topicImage);

    List<TopicImage> getTopicImageByTopicId(int topicId);

    void removeImageByTopic(TopicImage topicImage);
}
