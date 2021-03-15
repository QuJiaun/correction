package com.luckyxmobile.correction.view;

import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;

import java.util.List;

public interface ITopicInfoView {

    void setToolbar(String toolbarName);

    void setTopicCollection(boolean collection);

    void setTopicText(String text);

    void setTopicImages(List<TopicImage> topicImages);

    void setTopicTags(Topic topic);

    void setTopicCreateDate(String date);
}
