package com.luckyxmobile.correction.view;


import com.luckyxmobile.correction.ui.dialog.BookInfoDialog;

public interface BookInfoView {

    void setBookCoverView(String path);

    BookInfoView setBookName(String name);

    BookInfoDialog build();

    String getBookCoverPath();

    String getBookName();

    void onFailToast(String log);

}
