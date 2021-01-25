package com.luckyxmobile.correction.presenter;

import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;

public interface TopicInfoViewPresenter {

    void initTopicInfo(Topic currentTopic);

    void addTopicImage(TopicImage topicImage);

    void removeTopicImage(TopicImage topicImage);

    void showTopicTextDialog();
}
