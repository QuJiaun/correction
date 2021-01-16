package com.luckyxmobile.correction.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Book;


import androidx.annotation.NonNull;

public class BookInfoDialog extends AlertDialog.Builder {

    private final ImageButton alterBookCoverBtn;
    private ImageButton deleteBookCoverBtn;
    private ImageView bookCoverView;
    private EditText bookNameEt;
    private TextView bookNameNum;
    private String bookCoverPath = "default";

    public BookInfoDialog(Activity activity){
        super(activity);

        View view =  LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_book,null);
        setView(view);

        alterBookCoverBtn = view.findViewById(R.id.alter_cover_image);
        bookCoverView = view.findViewById(R.id.coverImg);
        bookNameEt = view.findViewById(R.id.bookNameEdt);
        bookNameNum = view.findViewById(R.id.bookNameEdtNum);
        deleteBookCoverBtn = view.findViewById(R.id.delete_book_cover);

        setNegativeButton(R.string.cancel, null);

        deleteBookCoverBtn.setOnClickListener(view1 -> {
            setBookCover(null);
        });

        //输入框字数提示和限制
        bookNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                bookNameNum.setText(s.length()+"/10");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                bookNameNum.setText(s.length()+"/10");
            }
        });

    }

    public BookInfoDialog build() {
        return build(null);
    }

    public BookInfoDialog build(Book book) {
        if (book != null) {
            setBookName(book.getName());
            setBookCover(book.getCover());
        } else {
            setBookName("");
            setBookCover("default");
        }

        return this;
    }

    public void setAlterCoverButton(View.OnClickListener listener) {
        alterBookCoverBtn.setOnClickListener(listener);
    }


    public void alterBookCover(String path) {
        setBookCover(path);
    }

    private void setBookCover(String path) {
        if (path == null || "".equals(path) || "default".equals(path)){
            bookCoverPath = "default";
            deleteBookCoverBtn.setVisibility(View.GONE);
        }else{
            bookCoverPath = path;
            deleteBookCoverBtn.setVisibility(View.VISIBLE);
        }

        Glide.with(getContext()).load(bookCoverPath)
                .placeholder(R.drawable.correction_book)
                .skipMemoryCache(true) // 不使用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                .into(bookCoverView);

    }

    private void setBookName(@NonNull String name) {
        bookNameEt.setText(name);
        bookNameNum.setText(name.length() + "/10");
    }

    public String getBookName() {
        return bookNameEt.getText().toString();
    }

    public String getBookCoverPath() {
        return bookCoverPath;
    }
}
