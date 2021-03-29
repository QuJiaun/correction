package com.luckyxmobile.correction.model.bean;

import android.graphics.Point;

import com.luckyxmobile.correction.global.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Highlighter implements Serializable {

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

}