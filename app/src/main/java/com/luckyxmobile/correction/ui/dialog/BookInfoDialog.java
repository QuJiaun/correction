package com.luckyxmobile.correction.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.ui.activity.CropImageActivity;
import com.luckyxmobile.correction.ui.activity.MainActivity;

public class BookInfoDialog extends AlertDialog.Builder {

    private ImageButton deleteBookCoverBtn;
    private ImageView bookCoverView;
    private EditText bookNameEt;
    private Book book;
    private final OnBtnClickListener listener;

    public BookInfoDialog(Activity activity) {
        super(activity);

        this.listener = (OnBtnClickListener) activity;

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_book, null);
        setView(view);

        ImageButton alterBookCoverBtn = view.findViewById(R.id.alter_cover_image);
        bookCoverView = view.findViewById(R.id.coverImg);
        bookNameEt = view.findViewById(R.id.bookNameEdt);
        deleteBookCoverBtn = view.findViewById(R.id.delete_book_cover);

        setNegativeButton(R.string.cancel, null);

        setPositiveButton(R.string.ensure, (dialog, which) ->{
            book.setName(bookNameEt.getText().toString());
            listener.onBookInfoDialogEnsure(book);
        });

        deleteBookCoverBtn.setOnClickListener(v1 -> alterBookCover(null));

        alterBookCoverBtn.setOnClickListener(v2 ->{
            Intent intent =  CropImageActivity.getIntent(activity, true, false);
            intent.putExtra(Constants.FROM_ACTIVITY, MainActivity.TAG);
            activity.startActivityForResult(intent, Constants.REQUEST_CODE_BOOK_COVER_IMAGE);
        });
    }

    public void setBook(Book book) {
        if (book == null) {
            book = new Book();
        }
        this.book = book;
        setBookNameEt();
        setBookCover();
    }

    public void alterBookCover(String path) {
        book.setCover(path);
        setBookCover();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setBookCover() {

        if (TextUtils.isEmpty(book.getCover())) {
            deleteBookCoverBtn.setVisibility(View.GONE);
            bookCoverView.setImageDrawable(getContext().getDrawable(R.drawable.correction_book));
        } else {
            deleteBookCoverBtn.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .load(book.getCover())
                    .skipMemoryCache(true) // 不使用内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                    .into(bookCoverView);
        }

    }

    private void setBookNameEt() {
        if (book.getName() != null && !book.getName().isEmpty()) {
            bookNameEt.setText(book.getName());
            bookNameEt.setSelection(book.getName().length());
        }
    }

    public interface OnBtnClickListener {
        void onBookInfoDialogEnsure(Book book);
    }

}


