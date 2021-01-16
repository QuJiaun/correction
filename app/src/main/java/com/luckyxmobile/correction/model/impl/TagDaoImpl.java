package com.luckyxmobile.correction.model.impl;


import com.luckyxmobile.correction.model.TagDao;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Tag;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.utils.impl.FilesUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签表封装方法
 * @author qjj
 * @date 2019/7/24
 */
public class TagDaoImpl implements TagDao {

    @Override
    public List<Topic> findTopicByTagInBook(Book book, Tag tag) {

        return null;
    }

    @Override
    public List<Tag> findTagByTopic(Topic topic) {

        return null;
    }
}
