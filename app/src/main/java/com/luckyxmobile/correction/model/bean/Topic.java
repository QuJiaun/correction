package com.luckyxmobile.correction.model.bean;


import androidx.annotation.NonNull;

import org.litepal.crud.LitePalSupport;

/**
 * 数据库表 Topic 题目信息
 * @date 2021-01-01
 */
public class Topic extends LitePalSupport {

    private int id;

    private int book_id;
    /**
     * 是否收藏
     */
    private boolean collection = false;

    /**
     * 重要程度 1~5
     */
    private int importance = 0;

    /**
     * 文字备注
     */
    private String text;

    private long create_date;

    public Topic(int book_id) {
        setBook_id(book_id);
        setCreate_date(System.currentTimeMillis());
    }


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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public long getCreate_date() {
        return create_date;
    }

    public void setCreate_date(long create_date) {
        this.create_date = create_date;
    }

    @Override
    public String toString() {
        return "Topic{" +
            "id=" + id +
            ", book_id=" + book_id +
            ", collection=" + collection +
            ", importance=" + importance +
            ", text=" + text +
            ", create_date=" + create_date +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Topic topic = (Topic) o;
        return id == topic.id && create_date == topic.getCreate_date();
    }

}
