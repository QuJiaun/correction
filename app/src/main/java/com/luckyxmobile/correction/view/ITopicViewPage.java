package com.luckyxmobile.correction.view;

import com.luckyxmobile.correction.model.bean.Topic;

import java.util.List;

public interface ITopicViewPage {

    void setTopicViewPage(List<Topic> topicList, int curPosition);

    void setTopicCollectBtn(boolean isCollect);

    void setTopicTagFlowLayout(Topic curTopic);
}
