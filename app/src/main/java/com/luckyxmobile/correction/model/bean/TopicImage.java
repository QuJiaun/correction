package com.luckyxmobile.correction.model.bean;

import android.graphics.Point;

import com.luckyxmobile.correction.global.Constants;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库表 TopicImage 题目图片信息
 * @date 2021-01-01
 */
public class TopicImage extends LitePalSupport {

    private int id;

    private int topic_id;

    /**
     * 图片类型
     */
    private int type;

    /**
     * 图片路径
     */
    private String path;

    /**
     * 文字大小
     */
    private int word_size;

    /**
     * 图片对比度设置
     */
    private int contrast_radio;

    /**
     * 荧光笔涂抹信息
     */
    private List<Highlighter> highlighterList = new ArrayList<>();

    private long create_time;

    public TopicImage() {
        setCreate_time(System.currentTimeMillis());
    }

    public TopicImage(@Constants.TopicImageType int type) {
        this.type = type;
        setCreate_time(System.currentTimeMillis());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(@Constants.TopicImageType int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getWord_size() {
        return word_size;
    }

    public void setWord_size(int word_size) {
        this.word_size = word_size;
    }

    public int getContrast_radio() {
        return contrast_radio;
    }

    public void setContrast_radio(@Constants.ContrastRadio int contrast_radio) {
        this.contrast_radio = contrast_radio;
    }

    public List<Highlighter> getHighlighterList() {
        return highlighterList;
    }

    public void setHighlighterList(List<Highlighter> highlighterList) {
        this.highlighterList = highlighterList;
    }

    public int getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(int topic_id) {
        this.topic_id = topic_id;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    @Override
    public String toString() {
        return "TopicImage{" +
                "id=" + id +
                ", topic_id=" + topic_id +
                ", type=" + type +
                ", path=" + path +
                ", word_size=" + word_size +
                ", contrast_radio=" + contrast_radio +
                ", highlighterList=" + highlighterList +
                ", create_time=" + create_time +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicImage that = (TopicImage) o;
        return id == that.id && path.equals(that.getPath());
    }

    public static class Highlighter {

        /**
         * 荧光笔类型
         */
        private int type;

        /**
         * 荧光笔宽度
         */
        private int width;

        /**
         * 涂抹位置信息
         */
        private List<Point> pointList = new ArrayList<>();

        public Highlighter(@Constants.HighlighterType int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public void setType(@Constants.HighlighterType int type) {
            this.type = type;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public List<Point> getPointList() {
            return pointList;
        }

        public void setPointList(List<Point> pointList) {
            this.pointList = pointList;
        }

        @Override
        public String toString() {
            return "Highlighter{" +
                "type=" + type +
                ", width=" + width +
                ", pointList=" + pointList +
                '}';
        }
    }
}
