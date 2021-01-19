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
    private Book book;

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
            alterBookCover(null);
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
                book.setName(bookNameEt.getText().toString());
                bookNameNum.setText(s.length()+"/10");
            }
        });

    }

    public BookInfoDialog build() {
        this.book = new Book();
        return this;
    }

    public BookInfoDialog build(Book book) {
        this.book = book;
        setBookCover();
        setBookNameEt();
        return this;
    }

    public void setAlterCoverButton(View.OnClickListener listener) {
        alterBookCoverBtn.setOnClickListener(listener);
    }

    public void alterBookCover(String path) {
        if (path == null || path.isEmpty()) {
           path = "default";
        }
        book.setCover(path);
        setBookCover();
    }

    public Book getBookInfo() {
        return book;
    }

    private void setBookCover() {

        if ("default".equals(book.getCover())){
            deleteBookCoverBtn.setVisibility(View.GONE);
        }else{
            deleteBookCoverBtn.setVisibility(View.VISIBLE);
        }

        Glide.with(getContext()).load(book.getCover())
                .placeholder(R.drawable.correction_book)
                .skipMemoryCache(true) // 不使用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                .into(bookCoverView);

    }

    private void setBookNameEt() {
        bookNameEt.setText(book.getName());
        bookNameNum.setText(book.getName().length() + "/10");
    }
}
