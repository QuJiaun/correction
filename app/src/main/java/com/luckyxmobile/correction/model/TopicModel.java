package com.luckyxmobile.correction.model;

import com.luckyxmobile.correction.presenter.OnTopicFinishedListener;

public interface TopicModel {

    void setOnTopicFinishedListener(OnTopicFinishedListener listener);

    void deleteTopic(int topicId);

}
