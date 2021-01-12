package com.luckyxmobile.correction.utils;

import android.graphics.Bitmap;
import android.net.Uri;

public interface IFiles {

    /**
     * 保存bitmap，到缓存文件夹中（/Cache/tmp.jpeg）
     * @param bitmap 图片
     * @return 缓存图片路径
     */
    String saveBitmap2TmpFile(Bitmap bitmap);

    /**
     * 修改文件路径
     * @param oldPath 原文件路径
     * @param newPath 新路径
     * @return 原文件存在时，返回新路径，否则返回null
     */
    String alterFilePath(String oldPath, String newPath);

    /**
     * 通过topicId，删除对应的文件（错题的图片）
     * @param topicId 错题id
     */
    void deleteTopicById(int topicId);

    /**
     * 通过bookId，删除对应的文件（错题本的文件夹，包括错题，封面图片）
     * @param bookId 错题本id
     * @return 异步删除错题本
     */
    Runnable deleteBookById(int bookId);

    /**
     * 删除文件（存在即删）
     * @param path 文件路径
     */
    void deleteFile(String path);

    /**
     * 通过bookId，新建对应文件夹（/Book/bookId）
     * @param bookId 错题本id
     * @return 错题本文件夹的路径
     */
    String saveBookFileInfo(int bookId);

    /**
     * 通过topicId，新建对应文件夹(/Book/bookId/topicId)
     * @param topicId 题目id
     * @return 错题文件夹的路径
     */
    String saveTopicFileInfo(int topicId);

    /**
     * 获取一个临时文件的uri
     * @return tmpFile的uri
     */
    Uri getTmpFileUri();

    void onFailToast(String log);

}
