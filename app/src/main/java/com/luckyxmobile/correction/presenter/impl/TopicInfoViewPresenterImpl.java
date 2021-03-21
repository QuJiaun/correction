package com.luckyxmobile.correction.presenter.impl;

import android.text.TextUtils;

import com.luckyxmobile.correction.model.TopicDao;
import com.luckyxmobile.correction.model.TopicImageDao;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.model.impl.TopicDaoImpl;
import com.luckyxmobile.correction.model.impl.TopicImageDaoImpl;
import com.luckyxmobile.correction.presenter.TopicInfoViewPresenter;
import com.luckyxmobile.correction.ui.activity.TopicInfoActivity;
import com.luckyxmobile.correction.utils.FilesUtils;
import com.luckyxmobile.correction.view.ITopicInfoView;

import org.litepal.LitePal;

public class TopicInfoViewPresenterImpl implements TopicInfoViewPresenter {

    private Topic curTopic;

    private final ITopicInfoView topicInfoView;
    private final TopicImageDao topicImageDao;

    public TopicInfoViewPresenterImpl(ITopicInfoView topicInfoView) {

        this.topicInfoView = topicInfoView;
        TopicDao topicDao = new TopicDaoImpl(null);
        topicImageDao = new TopicImageDaoImpl(null);
    }

    @Override
    public void initTopicInfo(int topicId) {

        curTopic = LitePal.find(Topic.class, topicId, true);
        if (curTopic == null) throw new RuntimeException(TopicInfoActivity.TAG + " : topic is null");

        Book curBook = LitePal.find(Book.class, curTopic.getBook_id());
        if (curBook == null) throw new RuntimeException(TopicInfoActivity.TAG + " : topic.getBook_Id is null");

        topicInfoView.setToolbar(curBook.getName());
        topicInfoView.setTopicCollection(curTopic.isCollection());
        topicInfoView.setTopicText(curTopic.getText());
        topicInfoView.setTopicImages(topicImageDao.getTopicImageByTopicId(curTopic.getId()));
        topicInfoView.setTopicTags(curTopic);
        topicInfoView.setTopicCreateDate(FilesUtils.getTimeByDate(curTopic.getCreate_date()));
    }

    @Override
    public Topic getCurTopic() {
        return curTopic;
    }

    @Override
    public void setTopicText(String text) {
        if (TextUtils.isEmpty(text)) {
            curTopic.setToDefault("text");
        } else {
            curTopic.setText(text);
        }
    }

    @Override
    public void setTopicCollection(boolean collection) {
        if (collection) {
            curTopic.setCollection(true);
        } else {
            curTopic.setToDefault("collection");
        }
    }

    @Override
    public void removeTopicImage(TopicImage topicImage) {
        topicImageDao.removeImageByTopic(topicImage);
    }

}
