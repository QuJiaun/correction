package com.luckyxmobile.correction.model;

import com.luckyxmobile.correction.model.bean.Topic;

/**
 * @author yanghao
 * @date 2019/8/3
 * @time 9:14
 */
public interface BookDao {
    /**
     * 根据错题查询错题本名称
     *
     * @param topic
     * @return
     */
    String selectBookNameByTopic(Topic topic);
}
