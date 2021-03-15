package com.luckyxmobile.correction.model.bean;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.litepal.crud.LitePalSupport;

/**
 * 数据库表 NoteBook 笔记本信息
 * @date 2021-01-01
 * @update 2021-01-16
 */
public class Book extends LitePalSupport {

    private int id;

    private String name;

    private String cover;

    private long create_date;

    public Book() {
        setCreate_date(System.currentTimeMillis());
    }

    public Book(@NonNull String name) {
        setName(name);
        setCreate_date(System.currentTimeMillis());
    }

    public Book(@NonNull String name, @Nullable String cover) {
        setName(name);
        setCover(cover);
        setCreate_date(System.currentTimeMillis());
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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public long getCreate_date() {
        return create_date;
    }

    public void setCreate_date(long create_date) {
        this.create_date = create_date;
    }

    @Override
    public String toString() {
        return "NoteBook{" +
                "id=" + id +
                ", name=" + name +
                ", cover=" + cover +
                ", create_date=" + create_date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.getId() && create_date == book.create_date;
    }
}
