package com.luckyxmobile.correction.view;

import com.luckyxmobile.correction.model.bean.Tag;
import com.luckyxmobile.correction.model.bean.TopicImage;

import java.util.List;

public interface ITopicInfoView {

    void setTopicImages(List<TopicImage> topicImages);

    void setTopicTags(List<Tag> tags);

    void addTopicTextFinished(String text);

    void addTopicImageFinished(TopicImage topicImage);
}
