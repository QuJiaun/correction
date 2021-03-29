package com.luckyxmobile.correction.model;

import android.util.Log;

import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Highlighter;
import com.luckyxmobile.correction.model.bean.Paper;
import com.luckyxmobile.correction.model.bean.Tag;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.utils.GsonUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BeanUtils {

    public static TopicImage findTopicImageFirst(Topic topic) {
        return LitePal.where("topic_id=?", String.valueOf(topic.getId()))
                .findFirst(TopicImage.class);
    }

    public static List<Topic> findTopicAll(Paper paper) {
        Set<Integer> topicIds = paper.getTopicSet();

        List<Topic> result = new ArrayList<>();
        for (int id : topicIds) {
            result.add(LitePal.find(Topic.class, id));
        }
        return result;
    }

    public static List<TopicImage> findTopicAll(Topic topic) {
        return LitePal.where("topic_id=?", String.valueOf(topic.getId()))
                .find(TopicImage.class);
    }

    public static boolean existsTag(String tagName) {
        Tag tag = LitePal.where("tag_name=?",tagName).findFirst(Tag.class);
        return tag != null;
    }

    public static List<Tag> findTagAll(int topicId) {
        List<Tag> tagList = LitePal.findAll(Tag.class);
        Iterator<Tag> iterator = tagList.iterator();
        while (iterator.hasNext()) {
            Tag tag = iterator.next();
            if (!tag.getTopicSet().contains(topicId)) {
                iterator.remove();
            }
        }
        return tagList;
    }

    public static String tagsToString(int topicId) {
        List<Tag> tagList = findTagAll(topicId);
        if (tagList.isEmpty()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tagList.size(); i++) {
            if (i != tagList.size()-1) {
                builder.append(tagList.get(i).getTag_name()).append("、");
            } else {
                builder.append(tagList.get(i).getTag_name());
            }
        }
        return builder.toString();
    }

    public static List<TopicImage> findTopicImageByType(Topic topic, @Constants.TopicImageType int type) {
        Iterator<TopicImage> iterator = findTopicAll(topic).iterator();
        List<TopicImage> tmp = new ArrayList<>();
        while (iterator.hasNext()) {
            TopicImage topicImage = iterator.next();
            if (topicImage.getType() == type) {
                tmp.add(topicImage);
            }
        }
        return tmp;
    }

    public static String getBookName(Topic topic) {
        return LitePal.find(Book.class, topic.getBook_id()).getName();
    }

    public static List<Highlighter> findAll(TopicImage topicImage) {
        List<Highlighter> result = new ArrayList<>();
        List<String> tmp = topicImage.getHighlighterList();

        for (String s: tmp) {
            Log.d("123456", "findAll: " + s);
            Highlighter highlighter = GsonUtils.json2Obj(s, Highlighter.class);
            if (highlighter != null) {
                Log.d("123456", "highlighter: " + highlighter);
                result.add(highlighter);
            }
        }

        return result;
    }

    public static List<String> obj2Strings(List<Highlighter> highlighterList) {
        List<String> result = new ArrayList<>();
        for (Highlighter h : highlighterList) {
            result.add(GsonUtils.obj2Json(h));
        }
        return result;
    }
}
