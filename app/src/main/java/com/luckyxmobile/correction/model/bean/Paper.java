package com.luckyxmobile.correction.model.bean;

import org.litepal.crud.LitePalSupport;

import java.util.HashSet;
import java.util.Set;

/**
 * 复习卷的数据库表Paper
 * @author qjj
 * @date 2019/7/24(up)
 */
public class Paper extends LitePalSupport {

    /**
     * 复习卷id
     */
    private int id;

    /**
     * 复习卷名称
     * paper_name
     */
    private String paper_name;

    private Set<Integer> topicSet = new HashSet<>();

    /**
     * 复习卷的创建时间
     * paper_creation_time
     */
    private long paper_create_time;

    public Paper (){
        setPaper_create_time(System.currentTimeMillis());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPaperName() {
        return paper_name;
    }

    public void setPaperName(String paper_name) {
        this.paper_name = paper_name;
    }

    public long getPaper_create_time() {
        return paper_create_time;
    }

    public void setPaper_create_time(long paper_create_time) {
        this.paper_create_time = paper_create_time;
    }

    public Set<Integer> getTopicSet() {
        return topicSet;
    }

    public void setTopicSet(Set<Integer> topicSet) {
        this.topicSet = topicSet;
    }

    @Override
    public String toString() {
        return "Paper{" +
            "id=" + id +
            ", paper_name='" + paper_name + '\'' +
            ", paper_create_time=" + paper_create_time +
            '}';
    }
}
