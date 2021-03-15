package com.luckyxmobile.correction.view;

import com.luckyxmobile.correction.model.bean.Topic;

import java.util.List;

public interface IBookDetailView {

    void setTagLayout();

    void setTopicListRv(List<Topic> topicList);

    void setToolBar(String barName);

    void removeTopicListFinish(boolean isEmpty);
}
