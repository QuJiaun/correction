package com.luckyxmobile.correction.model.impl;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Paper;
import com.luckyxmobile.correction.model.bean.PaperTopic;
import com.luckyxmobile.correction.model.bean.Tag;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.utils.FastJsonUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CorrectionLab {

    public static boolean addBook(Book book){
        return book.save();
    }

    public static Paper addPaper(String paper_name){

        Paper paper = new Paper();
        paper.setPaper_name(paper_name);
        paper.save();
        return paper;

    }

    public static boolean addTopic(Topic topic){
        return topic.save();
    }

    public static Tag addTag(String tag_name){

        Tag tag = new Tag();
        tag.setTag_name(tag_name);
        tag.save();
        return tag;

    }

    public static void add_Topic_To_Book(int topic_id,int book_id){

        Topic topic = new Topic();
        topic.setBook_id(book_id);
        topic.update(topic_id);
        topic.save();

    }

    public static void deleteBook(int book_id){

        //删除错题本表对应的内容
        LitePal.delete(Book.class,book_id);
        //删除对应的错题
        LitePal.deleteAll(Topic.class,"book_id=?",String.valueOf(book_id));

    }

    public static void deletePaper(int paper_id){

        LitePal.delete(Paper.class,paper_id);

        LitePal.deleteAll(PaperTopic.class,"paper_id=?",String.valueOf(paper_id));
    }

    public static List<String> getTopicImagePaths(int topic_id){
        List<String> result = new ArrayList<>();

        Topic topic = LitePal.find(Topic.class, topic_id);

        

        return result;
    }

    public static void deleteTopic(int topic_id){

        //删除错题表对应内容
        LitePal.delete(Topic.class,topic_id);
        //删除复习卷-错题关联表对应内容
        LitePal.deleteAll(PaperTopic.class,"topic_id=?",String.valueOf(topic_id));

    }


    /**
     * 通过名字查询所有Paper和Book
     * searchType = 0 时查询所有Paper和Book
     * searchType = 1 时查询Book
     * searchType = 2 时查询Paper
     */
    public static List<Search> selectName(String name, int searchType){
        List<Search> searchList = new ArrayList<>();
        List<Paper> papers = LitePal.where("paper_name like ?","%" + name + "%").find(Paper.class);
        List<Book> books = LitePal.where("book_name like ?", "%" + name + "%").find(Book.class);
        if (searchType == 0){
            for(Paper paper : papers){
                Search search = new Search(paper.getPaper_name(),R.drawable.ic_page);
                search.setId(paper.getId());
                searchList.add(search);
            }
            for (Book book : books){
                Search search = new Search(book.getBook_name(),R.drawable.correction_book);
                search.setId(book.getId());
                searchList.add(search);
            }
        }
        if (searchType == 1){
            for (Book book : books){
                Search search = new Search(book.getBook_name(),R.drawable.correction_book);
                search.setId(book.getId());
                searchList.add(search);
            }
        }
        if (searchType == 2){
            for(Paper paper : papers){
                Search search = new Search(paper.getPaper_name(),R.drawable.ic_page);
                search.setId(paper.getId());
                searchList.add(search);
            }
        }

        return searchList;
    }

    /**
     * 获取现在时间
     * @author qjj
     */
    public static Date getNowDate() {
        return new Date(System.currentTimeMillis());
    }

    public static void updateBook(Book book) {
        Book book1 = LitePal.find(Book.class,book.getId());
        book1.setBook_name(book.getBook_name());
        book1.setBook_cover(book.getBook_cover());
        book1.save();
    }

    public static int getBookIdByTopic(int topicId){
        Topic topic = LitePal.find(Topic.class, topicId);
        return topic.getBook_id();
    }

    public static TopicImagesInfo getTopicImagesInfoById(int topicId, String whichType){
        Topic topic = LitePal.find(Topic.class, topicId);

        String json = topic.getTopic_images_info_map().get(whichType);

        return FastJsonUtil.jsonToObject(json, TopicImagesInfo.class);
    }

    public static void alterTopicImages(int topicId, String whichType, TopicImagesInfo topicImagesInfo){

        Topic topic = LitePal.find(Topic.class, topicId);

        String json = FastJsonUtil.objectToJson(topicImagesInfo);

        topic.getTopic_images_info_map().put(whichType, json);

        topic.save();

    }

}
