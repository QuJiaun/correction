package com.luckyxmobile.correction.presenter.impl;

import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.global.MySharedPreferences;
import com.luckyxmobile.correction.model.TopicDao;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.impl.TopicDaoImpl;
import com.luckyxmobile.correction.presenter.TopicViewPagePresenter;
import com.luckyxmobile.correction.utils.FilesUtils;
import com.luckyxmobile.correction.view.ITopicViewPage;

import org.litepal.LitePal;

import java.util.Collections;
import java.util.List;

public class TopicViewPagePresenterImpl implements TopicViewPagePresenter {

    private TopicDao topicDao;

    private ITopicViewPage topicViewPage;

    private MySharedPreferences sharedPreferences;
    private final boolean isShowTag;

    private final List<Topic> topicList;
    private Topic curTopic;
    private int curPosition;

    public TopicViewPagePresenterImpl(ITopicViewPage topicViewPage, int curTopicId) {
        this.topicViewPage = topicViewPage;
        topicDao = new TopicDaoImpl(null);

        curTopic = LitePal.find(Topic.class, curTopicId);
        topicList = topicDao.getTopicListByBookId(curTopic.getBook_id());

        sharedPreferences = MySharedPreferences.getInstance();
        isShowTag = sharedPreferences.getBoolean(Constants.SHOW_TAG_IN_TOPIC_VIEW_PAGE, true);

        if (!sharedPreferences.getBoolean(Constants.IS_NEWEST_ORDER, true)) {
            Collections.reverse(topicList);
        }

        curPosition = topicList.indexOf(curTopic);

        topicViewPage.setTopicViewPage(topicList, curPosition);
        topicViewPage.setTopicCollectBtn(curTopic.isCollection());
        topicViewPage.setTopicTagLayout(isShowTag, curTopic);
        topicViewPage.setTopicDate(FilesUtils.getTimeByDate(curTopic.getCreate_date()));
    }

    @Override
    public void curTopicChange(int position) {
        this.curPosition = position;
        this.curTopic = topicList.get(position);
        topicViewPage.setProgressBar(position+1);
        topicViewPage.setTopicCollectBtn(curTopic.isCollection());
        topicViewPage.setTopicDate(FilesUtils.getTimeByDate(curTopic.getCreate_date()));
        if (isShowTag) {
            topicViewPage.setTopicTagFlowLayout(getCurTopicId());
        }
    }

    @Override
    public void topicCollectChange() {
        boolean isCollect = topicDao.changeCurTopicCollection(getCurTopicId());
        topicViewPage.setTopicCollectBtn(isCollect);
    }

    @Override
    public int getCurTopicId() {
        return curTopic.getId();
    }
}
