package com.luckyxmobile.correction.presenter;

public interface TopicViewPagePresenter {

    void curTopicChange(int curPosition);

    void topicCollectChange();

    int getCurTopicPosition();

    int getCurTopicId();

    String getCurTopicBookName();
}
