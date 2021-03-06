package com.luckyxmobile.correction.model.bean;

import com.luckyxmobile.correction.global.Constants;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
     * 图片调节参数
     */
    private String imageParam;

    /**
     * 该图调用ocr
     * 调用ocr后，不能调编辑图片
     */
    private boolean ocr;

    /**
     * 荧光笔涂抹信息
     */
    private List<String> highlighterList = new ArrayList<>();

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

    public String getImageParam() {
        return imageParam;
    }

    public void setImageParam(String imageParam) {
        this.imageParam = imageParam;
    }

    public List<String> getHighlighterList() {
        return highlighterList;
    }

    public void setHighlighterList(List<String> highlighterList) {
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

    public void setOcr(boolean ocr) {
        this.ocr = ocr;
    }

    public boolean isOcr() {
        return ocr;
    }

    @Override
    public String toString() {
        return "TopicImage{" +
                "id=" + id +
                ", topic_id=" + topic_id +
                ", type=" + type +
                ", path=" + path +
                ", word_size=" + word_size +
                ", imageParam=" + imageParam +
                ", ocr=" + ocr +
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

    @Override
    public int hashCode() {
        return Objects.hash(id, path);
    }
}
