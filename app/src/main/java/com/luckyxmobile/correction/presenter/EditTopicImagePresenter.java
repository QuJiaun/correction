package com.luckyxmobile.correction.presenter;

import com.luckyxmobile.correction.model.bean.TopicImage;

public interface EditTopicImagePresenter {

    void saveTopicImage(int bookId, TopicImage topicImage);

    void addTopicImage2Topic(int topicId, TopicImage topicImage);
}
