package com.luckyxmobile.correction.model.bean;

import org.litepal.crud.LitePalSupport;

import java.util.Date;
import java.util.Objects;

/**
 * 数据库表 Topic 题目信息
 * @date 2021-01-01
 */
public class Topic extends LitePalSupport {

    private int id;

    /**
     * 绑定的book，唯一
     */
    private Book book;

    /**
     * 是否收藏
     */
    private boolean collection;

    /**
     * 重要程度 1~5
     */
    private int importance;

    /**
     * 文字备注
     */
    private String text;

    private Date create_date;


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

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    @Override
    public String toString() {
        return "Topic{" +
            "id=" + id +
            ", book=" + book +
            ", collection=" + collection +
            ", importance=" + importance +
            ", text='" + text + '\'' +
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
        return id == topic.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
