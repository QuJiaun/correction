package com.luckyxmobile.correction.model.impl;

import com.luckyxmobile.correction.model.TopicImageDao;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.model.DaoListener;
import com.luckyxmobile.correction.utils.impl.FilesUtils;

import org.litepal.LitePal;

import java.util.List;

public class TopicImageDaoImpl implements TopicImageDao {

    private final String TAG = TopicImageDaoImpl.class.getSimpleName();

    private final FilesUtils filesUtils = FilesUtils.getInstance();
    private final DaoListener listener;

    public TopicImageDaoImpl(DaoListener listener) {
        this.listener = listener;
    }

    @Override
    public void saveTopicImage(int topicId, TopicImage topicImage) {

        if (topicImage.getId() > 0) throw new RuntimeException("saveTopicImage : topicImage is saved!");

        Topic topic = LitePal.find(Topic.class, topicId, true);

        if (topic == null || !topic.isSaved()) throw new RuntimeException("saveTopicImage : topic is null");

        if (topic.getBook_id() <= 0) throw new RuntimeException("saveTopicImage : topic.getBookId is null");

        topicImage.setTopic_id(topicId);
        topicImage.save();

        //迁移图片位置
        String newPath = filesUtils.getTopicImagePath(topicImage);
        filesUtils.copyFile(topicImage.getPath(), newPath);
        topicImage.setPath(newPath);

        topicImage.save();
    }

    @Override
    public List<TopicImage> getTopicImageByTopicId(int topicId) {
        return LitePal.where("topic_id=?", String.valueOf(topicId)).find(TopicImage.class);
    }

    @Override
    public void removeImageByTopic(TopicImage topicImage) {

        filesUtils.deleteTopicImage(topicImage, listener);

        topicImage.delete();
    }
}
