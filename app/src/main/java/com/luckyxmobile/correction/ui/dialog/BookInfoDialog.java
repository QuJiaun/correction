package com.luckyxmobile.correction.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.view.BookInfoView;

public class BookInfoDialog extends AlertDialog.Builder implements BookInfoView {

    private final ImageButton alterBookCoverBtn;
    private final ImageButton deleteBookCoverBtn;
    private final ImageView bookCoverView;
    private final EditText bookNameEt;
    private final TextView bookNameNum;
    private String bookName = null, bookCoverPath = "default";

    public BookInfoDialog(Context context){
        super(context);

        View view =  LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_book,null);
        setView(view);

        alterBookCoverBtn = view.findViewById(R.id.alter_cover_image);
        bookCoverView = view.findViewById(R.id.coverImg);
        bookNameEt = view.findViewById(R.id.bookNameEdt);
        bookNameNum = view.findViewById(R.id.bookNameEdtNum);
        deleteBookCoverBtn = view.findViewById(R.id.delete_book_cover);

        deleteBookCoverBtn.setOnClickListener(view1 -> {
            setBookCoverView(null);
        });

        //输入框字数提示和限制
        bookNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                bookName = bookNameEt.getText().toString();
                bookNameNum.setText(s.length()+"/10");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                bookName = bookNameEt.getText().toString();
                bookNameNum.setText(s.length()+"/10");
            }
        });

    }

    public ImageButton getAlterBookCoverBtn() {
        return alterBookCoverBtn;
    }

    @Override
    public void setBookCoverView(String path) {
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

    @Override
    public BookInfoView setBookName(String name) {
        bookName = name == null ? "":name;
        bookNameEt.setText(bookName);
        bookNameNum.setText(String.format("%d/10", bookName.length()));
        return this;
    }

    @Override
    public BookInfoDialog build() {
        if(bookName == null) setBookName("");
        if(bookCoverPath == null) setBookCoverView("default");
        return this;
    }

    @Override
    public String getBookName() {
        bookName = bookNameEt.getText().toString().trim();
        if ("".equals(bookName)) bookName = null;
        return bookName;
    }

    @Override
    public String getBookCoverPath() {
        return bookCoverPath;
    }

    @Override
    public void onFailToast(String log) {
        Toast.makeText(getContext(), log, Toast.LENGTH_SHORT).show();
    }
}
