package com.luckyxmobile.correction.model.impl;

import com.luckyxmobile.correction.model.TopicImageModel;
import com.luckyxmobile.correction.presenter.OnTopicImageFinishedListener;
import com.luckyxmobile.correction.utils.IFiles;
import com.luckyxmobile.correction.utils.impl.FilesUtils;

public class TopicImageModelImpl implements TopicImageModel {


    private IFiles filesUtil;
    private OnTopicImageFinishedListener listener;

    @Override
    public void setOnTopicImageFinishedListener(OnTopicImageFinishedListener listener) {
        this.listener = listener;
        filesUtil = new FilesUtils(listener.getContext());
    }

    @Override
    public void addImage2Topic(int topicId, String whichType, String path) {

        TopicImagesInfo topicImagesInfo = CorrectionLab.getTopicImagesInfoById(topicId, whichType);

        if (topicImagesInfo == null){

            topicImagesInfo = new TopicImagesInfo();
            topicImagesInfo.setImageMaxId();
        }

        //对应题目路径
        String topicDir = filesUtil.saveTopicFileInfo(topicId);
        //缓存文件移到/Book/bookId/topicId/whichType-topicId-imagesMaxId.jpeg
        path = filesUtil.alterFilePath(path, topicDir + "/" + whichType + "-" +
                topicId + "-" + topicImagesInfo.getImageMaxId() + ".jpeg");


        TopicImagesInfo.TopicImage topicImage = new TopicImagesInfo.TopicImage();
        topicImage.setImagePath(path);

        topicImagesInfo.getTopicImageList().add(topicImage);

        CorrectionLab.alterTopicImages(topicId, whichType, topicImagesInfo);

        listener.alterSuccess();

    }

}
