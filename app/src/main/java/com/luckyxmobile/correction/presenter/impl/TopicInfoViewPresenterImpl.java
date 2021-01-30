package com.luckyxmobile.correction.presenter.impl;

import android.content.Context;

import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.presenter.TopicInfoViewPresenter;
import com.luckyxmobile.correction.view.ITopicInfoView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class TopicInfoViewPresenterImpl implements TopicInfoViewPresenter {


    private ITopicInfoView topicInfoView;

    private Topic curTopic;
    private List<TopicImage> topicImageList;

    public TopicInfoViewPresenterImpl(Context context) {
        this.topicInfoView = (ITopicInfoView) context;

    }

    @Override
    public void initTopicInfo(Topic currentTopic) {
        this.curTopic = currentTopic;
        topicImageList = LitePal.where("topic_id = ?", curTopic.getId()+"").find(TopicImage.class);

        topicInfoView.setTopicImages(topicImageList);
        topicInfoView.setTopicTextFinished(currentTopic.getText());
    }

    @Override
    public void addTopicImage(TopicImage topicImage) {

    }

    @Override
    public void removeTopicImage(TopicImage topicImage) {

    }

    @Override
    public void showTopicTextDialog() {

    }
}
