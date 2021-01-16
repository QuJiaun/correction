package com.luckyxmobile.correction.model.bean;

import org.litepal.crud.LitePalSupport;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


import androidx.annotation.NonNull;


/**
 * 标签表Tag
 * @author qjj
 * @date 2019/7/24
 */
public class Tag extends LitePalSupport {

    /**
     * 标签id
     */
    private int id;

    /**
     * 标签名称
     * tag_name
     */
    private String tag_name;

    /**
     * 标签与错题的关联
     */
    private Set<Topic> topicSet = new HashSet<>();

    /**
     * 标签的创建时间
     * tag_creation_time
     */
    private Date tag_create_time;

    public Tag(@NonNull String tag_name) {
        this.tag_name = tag_name;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public Date getTag_create_time() {
        return tag_create_time;
    }

    public void setTag_create_time(Date tag_create_time) {
        this.tag_create_time = tag_create_time;
    }

    public Set<Topic> getTopicSet() {
        return topicSet;
    }

    public void setTopicSet(Set<Topic> topicSet) {
        this.topicSet = topicSet;
    }

    @Override
    public String toString() {
        return "Tag{" +
            "id=" + id +
            ", tag_name='" + tag_name + '\'' +
            ", topicSet=" + topicSet +
            ", tag_create_time=" + tag_create_time +
            '}';
    }
}
