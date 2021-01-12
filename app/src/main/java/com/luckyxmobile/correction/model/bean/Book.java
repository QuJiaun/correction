package com.luckyxmobile.correction.model.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据库表 NoteBook 笔记本信息
 * @date 2021-01-01
 */
public class Book extends LitePalSupport {

    private int id;

    private String name;

    private String cover_image_path;

    private Date create_date;

    private List<Topic> topicList = new ArrayList<>();

    public Book(@NonNull String name) {
        setName(name);
        setCreate_date(new Date(System.currentTimeMillis()));
    }

    public Book(@NonNull String name, @Nullable String cover_image_path) {
        setName(name);
        setCover_image_path(cover_image_path);
        setCreate_date(new Date(System.currentTimeMillis()));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover_image_path() {
        return cover_image_path;
    }

    public void setCover_image_path(String cover_image_path) {
        this.cover_image_path = cover_image_path;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public List<Topic> getTopicList() {
        return topicList;
    }

    public void setTopicList(List<Topic> topicList) {
        this.topicList = topicList;
    }

    private void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    @Override
    public String toString() {
        return "NoteBook{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cover_image_path='" + cover_image_path + '\'' +
                ", create_date=" + create_date +
                ", topicList=" + topicList +
                '}';
    }
}
