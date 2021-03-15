package com.luckyxmobile.correction.presenter;

import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;

public interface TopicInfoViewPresenter {

    void initTopicInfo(int topicId);

    Topic getCurTopic();

    void setTopicText(String text);

    void setTopicCollection(boolean collection);

    void removeTopicImage(TopicImage topicImage);
}
