package com.luckyxmobile.correction.presenter.impl;


import com.luckyxmobile.correction.model.TopicDao;
import com.luckyxmobile.correction.model.TopicImageDao;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.model.impl.TopicDaoImpl;
import com.luckyxmobile.correction.model.impl.TopicImageDaoImpl;
import com.luckyxmobile.correction.presenter.EditTopicImagePresenter;

public class EditTopicImagePresenterImpl implements EditTopicImagePresenter {

    private final TopicDao topicDao;
    private final TopicImageDao topicImageDao;

    public EditTopicImagePresenterImpl() {

        topicDao = new TopicDaoImpl(null);
        topicImageDao = new TopicImageDaoImpl(null);
    }

    @Override
    public void saveTopicImage(int bookId, TopicImage topicImage) {

        Topic topic = topicDao.newTopic(bookId);
        topicImageDao.saveTopicImage(topic.getId(), topicImage);
    }

    @Override
    public void addTopicImage2Topic(int topicId, TopicImage topicImage) {

        topicImageDao.saveTopicImage(topicId, topicImage);
    }
}
