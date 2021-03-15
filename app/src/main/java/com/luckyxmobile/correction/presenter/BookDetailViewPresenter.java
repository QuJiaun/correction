package com.luckyxmobile.correction.presenter;

import com.luckyxmobile.correction.model.bean.Topic;

import java.util.List;

public interface BookDetailViewPresenter {

    void init(int bookId);

    void setIsNewest(boolean isNewest);

    void removeTopicList(List<Topic> topicList);

    boolean isNewest();
}
