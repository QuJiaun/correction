package com.luckyxmobile.correction.model;

import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;

import org.litepal.LitePal;

public class BeanUtils {

    public static TopicImage findFirst(Topic topic) {
        return LitePal.where("topic_id=?", String.valueOf(topic.getId()))
                .findFirst(TopicImage.class);
    }
}
