package com.luckyxmobile.correction.model.impl;

import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.PaperTopicDao;
import com.luckyxmobile.correction.model.bean.PaperTopic;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class PaperTopicDaoImpl implements PaperTopicDao {
    @Override
    public Integer save(Integer paper_id, List<Integer> topic_ids) {
        PaperTopic paper_topic;
        if (topic_ids != null && topic_ids.size() != 0) {
            for (int i = 0; i < topic_ids.size(); i++) {
                paper_topic = new PaperTopic();
                paper_topic.setpaper_id(paper_id);
                paper_topic.setTopic_id(topic_ids.get(i));
                if(!paper_topic.save()){
                    return ERROR;
                }
            }
        } else {
            return ERROR;
        }
        return OK;
    }

    @Override
    public Integer deleteByPaperId(Integer paper_id) {
        return LitePal.deleteAll(PaperTopic.class, "paper_id = ?", String.valueOf(paper_id));
    }

    @Override
    public Integer deleteByTopicId(Integer paper_id, Integer topic_id) {
        return LitePal.deleteAll(PaperTopic.class, "paper_id = ? and topic_id = ?", String.valueOf(paper_id), String.valueOf(topic_id));
    }


    @Override
    public Integer update(Integer paper_id, List<Integer> topic_ids) {
        deleteByPaperId(paper_id);
        return save(paper_id, topic_ids);
    }


    @Override
    public List<Topic> selectPaper(Integer paper_id) {
        if (paper_id == null || paper_id <= 0) {
            return new ArrayList<>();
        }
        List<PaperTopic> paper_topics = LitePal.where("paper_id = ?", String.valueOf(paper_id)).find(PaperTopic.class);
        int size = paper_topics.size();
        long[] topics = new long[size];
        for (int i = 0; i < size; ++i) {
            topics[i] = paper_topics.get(i).getTopic_id();
        }
        if (size == 0) {
            return new ArrayList<>();
        } else
            return LitePal.findAll(Topic.class, topics);
    }

    @Override
    public List<String> selectByTopicId(Integer topic_id) {
        if(topic_id == null || topic_id == 0){
            return new ArrayList<>();
        }

        List<PaperTopic> paper_topics = LitePal.where("topic_id = ?", String.valueOf(topic_id)).find(PaperTopic.class);
        List<String> result = new ArrayList<>();
        if (paper_topics != null && paper_topics.size() != 0) {
            for (PaperTopic paper_topic : paper_topics) {
                result.add(String.valueOf(paper_topic.getpaper_id()));
            }
        }
        return result;
    }
}
