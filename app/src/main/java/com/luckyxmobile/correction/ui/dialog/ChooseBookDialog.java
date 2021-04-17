package com.luckyxmobile.correction.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.adapter.TextTagAdapter;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.model.bean.Book;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChooseBookDialog extends CommonBottomDialog{

    public ChooseBookDialog(@NonNull Context context) {
        super(context);
    }

    private TagFlowLayout bookLayout;
    private TagFlowLayout imageTypeLayout;
    private Button ensureBtn;

    private List<Book> bookList;
    private Book curBook;

    private int[] imageTypeValueArr;
    private String[] imageTypeArr;
    private int curImageType = Constants.TOPIC_STEM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookLayout = findViewById(R.id.select_book_dialog_note_book_layout);
        bookLayout.setMaxSelectCount(1);
        imageTypeLayout = findViewById(R.id.select_book_dialog_topic_image_type_layout);
        imageTypeLayout.setMaxSelectCount(1);
        setTitle(R.string.finish);
        ensureBtn = findViewById(R.id.ensure_btn);

        imageTypeValueArr = getContext().getResources().getIntArray(R.array.topic_image_type_value);
        imageTypeArr = getContext().getResources().getStringArray(R.array.topic_image_type);
        setTypeTagLayout();
    }

    public Book getCurBook() {
        return curBook;
    }

    public int getCurImageType() {
        return curImageType;
    }

    public void setEnsureBtn(Runnable runnable) {
        ensureBtn.setOnClickListener(view -> runnable.run());
        dismiss();
    }

    public void setCurBook(int bookId) {
        if (bookId <= 0) {
            bookList = LitePal.findAll(Book.class);
            bookList.remove(0);
            if (bookList.isEmpty()) throw new RuntimeException("bookList is empty");
            curBook = bookList.get(0);
        } else {
            bookList = new ArrayList<>();
            curBook = LitePal.find(Book.class, bookId);
            if (curBook == null) throw new RuntimeException("bookList is empty");
            bookList.add(curBook);
        }
        setBookNameTagLayout(bookList);
    }

    private void setBookNameTagLayout(List<Book> bookList) {

        List<String> bookNameList = new ArrayList<>();

        for (Book book : bookList) {
            bookNameList.add(book.getName());
        }

        TextTagAdapter adapter = new TextTagAdapter(bookNameList);
        adapter.setCurPosition(0);
        bookLayout.setAdapter(adapter);

        bookLayout.setOnTagClickListener((view1, position, parent) -> {
            adapter.setCurPosition(position);
            curBook = bookList.get(position);
            bookLayout.onChanged();
            return true;
        });
    }

    private void setTypeTagLayout() {
        TextTagAdapter adapter = new TextTagAdapter(Arrays.asList(imageTypeArr));
        imageTypeLayout.setAdapter(adapter);
        imageTypeLayout.setOnTagClickListener((view, position, parent) -> {
            adapter.setCurPosition(position);
            curImageType = imageTypeValueArr[position];
            imageTypeLayout.onChanged();
            return true;
        });
    }

    @Override
    public int getDialogContentLayout() {
        return R.layout.dialog_select_book;
    }
}
