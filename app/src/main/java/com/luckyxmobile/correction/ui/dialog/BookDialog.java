package com.luckyxmobile.correction.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.textfield.TextInputLayout;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Book;

import org.litepal.LitePal;

public class BookDialog extends CommonDialog{

    public BookDialog(@NonNull Context context) {
        super(context);
    }

    private ImageButton deleteImageBtn;
    private ImageView bookCoverView;
    private TextInputLayout textInputLayout;
    private EditText bookNameEt;

    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookCoverView = findViewById(R.id.coverImg);
        bookNameEt = findViewById(R.id.edit_text);
        textInputLayout = findViewById(R.id.text_input_layout);
        deleteImageBtn = findViewById(R.id.delete_book_cover);

        textInputLayout.setCounterMaxLength(15);
        textInputLayout.setHint(getContext().getString(R.string.notebook_name_hint));
        setNegativeButton(R.string.cancel, this::dismiss);
    }

    public void setBook(Book book) {
        if (book == null) {
            this.book = new Book();
        } else {
            this.book = LitePal.find(Book.class, book.getId());
        }

        String bookName = this.book.getName();
        if (!TextUtils.isEmpty(bookName)) {
            bookNameEt.setText(bookName);
            bookNameEt.setSelection(bookName.length());
        } else {
            bookNameEt.setText(null);
            bookNameEt.setSelection(0);
        }

        setBookCoverView(this.book.getCover());
    }

    private void setBookCoverView(String path) {
        if (TextUtils.isEmpty(path)) {
            deleteImageBtn.setVisibility(View.GONE);
            bookCoverView.setImageDrawable(getContext().getDrawable(R.drawable.correction_book));
        } else {
            deleteImageBtn.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .load(book.getCover())
                    .skipMemoryCache(true) // 不使用内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                    .into(bookCoverView);
        }
    }

    public void alterBookCover(String path) {
        book.setCover(path);
        setBookCoverView(path);
    }

    public Book getBook() {
        book.setName(bookNameEt.getText().toString());
        return book;
    }

    @Override
    public int getDialogContentLayout() {
        return R.layout.dialog_add_book;
    }
}
