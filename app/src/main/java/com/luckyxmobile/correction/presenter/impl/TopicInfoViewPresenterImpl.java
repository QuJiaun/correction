package com.luckyxmobile.correction.presenter.impl;

import android.content.Context;

import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.presenter.TopicInfoViewPresenter;
import com.luckyxmobile.correction.view.ITopicInfoView;

import java.util.ArrayList;
import java.util.List;

public class TopicInfoViewPresenterImpl implements TopicInfoViewPresenter {


    private ITopicInfoView topicInfoView;

    private Topic currentTopic;
    private List<TopicImage> topicImageList;

    public TopicInfoViewPresenterImpl(Context context) {
        this.topicInfoView = (ITopicInfoView) context;

        topicImageList = new ArrayList<>();

    }

    @Override
    public void initTopicInfo(Topic currentTopic) {
        this.currentTopic = currentTopic;

        

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
