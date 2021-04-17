package com.luckyxmobile.correction.model.bean;

import org.litepal.crud.LitePalSupport;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
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
    private Set<Integer> topicSet = new HashSet<>();

    /**
     * 标签的创建时间
     * tag_creation_time
     */
    private long tag_create_time;

    public Tag(String tag_name) {
        this.tag_name = tag_name;
        setTag_create_time(System.currentTimeMillis());
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

    public long getTag_create_time() {
        return tag_create_time;
    }

    public void setTag_create_time(long tag_create_time) {
        this.tag_create_time = tag_create_time;
    }

    public Set<Integer> getTopicSet() {
        return topicSet;
    }

    public void setTopicSet(Set<Integer> topicSet) {
        this.topicSet = topicSet;
    }

    @Override
    public String toString() {
        return "Tag{" +
            "id=" + id +
            ", tag_name='" + tag_name + '\'' +
            ", tag_create_time=" + tag_create_time +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return id == tag.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
