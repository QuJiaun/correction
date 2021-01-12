package com.luckyxmobile.correction.model;

import com.luckyxmobile.correction.presenter.OnTopicImageFinishedListener;

public interface TopicImageModel {

    /**
     * 添加图片到错题的数据库中
     * @param topicId 题目id
     * @param whichType 图片类型
     * @param path 图片路径
     */
    void addImage2Topic(int topicId, String whichType, String path);

    void setOnTopicImageFinishedListener(OnTopicImageFinishedListener listener);
}
