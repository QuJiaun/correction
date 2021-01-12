package com.luckyxmobile.correction.model.impl;

import com.luckyxmobile.correction.model.TopicModel;
import com.luckyxmobile.correction.presenter.OnTopicFinishedListener;
import com.luckyxmobile.correction.utils.IFiles;
import com.luckyxmobile.correction.utils.impl.FilesUtils;

public class TopicModelImpl implements TopicModel {

    private IFiles filesUtil;
    private OnTopicFinishedListener listener;

    @Override
    public void setOnTopicFinishedListener(OnTopicFinishedListener listener) {
        this.listener = listener;
        filesUtil = new FilesUtils(listener.getContext());
    }

    @Override
    public void deleteTopic(int topicId) {
        CorrectionLab.deleteTopic(topicId);

        filesUtil.deleteTopicById(topicId);

        listener.deleteTopicSuccess();
    }
}
