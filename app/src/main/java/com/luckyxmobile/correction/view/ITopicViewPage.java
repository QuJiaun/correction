package com.luckyxmobile.correction.view;

import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;

import java.util.List;

public interface ITopicViewPage {

    void setTopicTagLayout(boolean isShow, Topic topic);

    void setProgressBar(int progress);

    void setTopicViewPage(List<Topic> topicList, int curPosition);

    void setTopicCollectBtn(boolean isCollect);

    void setTopicTagFlowLayout(int curTopicId);
}
