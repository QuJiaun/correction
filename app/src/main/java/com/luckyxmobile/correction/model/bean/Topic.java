package com.luckyxmobile.correction.model.bean;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库表 Topic 题目信息
 * @date 2021-01-01
 */
public class Topic extends LitePalSupport {

    private int id;

    /**
     * 是否收藏
     */
    private boolean collection;

    /**
     * 重要程度 1~5
     */
    private int importance;

    /**
     * 存入的图片
     */
    private List<TopicImage> topicImageList = new ArrayList<>();

    /**
     * 题目标签
     */
    private List<Tag> tagList = new ArrayList<>();

    /**
     * 文字备注
     */
    private String text;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCollection() {
        return collection;
    }

    public void setCollection(boolean collection) {
        this.collection = collection;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public List<TopicImage> getTopicImageList() {
        return topicImageList;
    }

    public void setTopicImageList(List<TopicImage> topicImageList) {
        this.topicImageList = topicImageList;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
