package com.luckyxmobile.correction.model.bean;

import android.graphics.Point;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库表 TopicImage 题目图片信息
 * @date 2021-01-01
 */
public class TopicImage extends LitePalSupport {

    private int id;

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
    private List<Highlighter> highlighterList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
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

    public void setContrast_radio(int contrast_radio) {
        this.contrast_radio = contrast_radio;
    }

    public List<Highlighter> getHighlighterList() {
        return highlighterList;
    }

    public void setHighlighterList(List<Highlighter> highlighterList) {
        this.highlighterList = highlighterList;
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

        public int getType() {
            return type;
        }

        public void setType(int type) {
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
    }
}
