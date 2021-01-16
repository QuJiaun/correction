package com.luckyxmobile.correction.model;

import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;

public interface TopicImageDao {

    void addImage2Topic(TopicImage topicImage, Topic topic);

    void removeImageByTopic(TopicImage topicImage, Topic topic);
}
