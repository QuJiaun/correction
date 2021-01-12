package com.luckyxmobile.correction.model.impl;

import com.luckyxmobile.correction.model.BookModel;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.presenter.OnBookFinishedListener;
import com.luckyxmobile.correction.utils.IFiles;
import com.luckyxmobile.correction.utils.impl.FilesUtils;

import org.litepal.LitePal;

public class BookModelImpl implements BookModel {

    private OnBookFinishedListener listener;
    private IFiles filesUtil;

    @Override
    public void setOnBookFinishedListener(OnBookFinishedListener listener) {
        this.listener = listener;
        filesUtil = new FilesUtils(listener.getContext());
    }

    @Override
    public void newBook(String bookName, String bookCoverPath) {

        Book book = new Book(bookName, bookCoverPath);
        book.save();

        //初始化错题本文件夹,并返回文件路径
        String bookDir = filesUtil.saveBookFileInfo(book.getId());

        //把图片从缓存中移到对应目录下
        if (!"default".equals(bookCoverPath)){
            bookCoverPath = filesUtil.alterFilePath(bookCoverPath,
                    bookDir +"/"+book.getId()+".jpeg");

            //重新保存图片文件路径
            if (bookCoverPath != null){
                book.setBook_cover(bookCoverPath);
                book.save();
            }
        }

        if (listener != null) listener.addSuccess(book);
    }

    @Override
    public void alterBook(int id, String bookName, String bookCoverPath) {
        Book book = LitePal.find(Book.class, id);

        if (bookName != null && !bookName.equals(book.getBook_name())){
            book.setBook_name(bookName);
        }

        if (bookCoverPath != null && !bookCoverPath.equals(book.getBook_cover())){
            filesUtil.deleteFile(book.getBook_cover());//删除原图

            if (!"default".equals(bookCoverPath)){

                String bookDir = filesUtil.saveBookFileInfo(book.getId());

                bookCoverPath = filesUtil.alterFilePath(bookCoverPath,
                        bookDir +"/"+book.getId()+".jpeg");

                if (bookCoverPath == null) bookCoverPath = "default";

            }

            book.setBook_cover(bookCoverPath);
        }

        book.save();
        if (listener != null) listener.alterSuccess(book);
    }

    @Override
    public void deleteBook(int bookId) {

        CorrectionLab.deleteBook(bookId);

        Runnable runnable = filesUtil.deleteBookById(bookId);

        if (runnable != null){
            Thread thread = new Thread(runnable,"deleteBook"+bookId);
            thread.start();
        }

        if (listener != null) listener.deleteSuccess();
    }
}
