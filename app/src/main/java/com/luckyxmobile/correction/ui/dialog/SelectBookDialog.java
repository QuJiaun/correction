package com.luckyxmobile.correction.ui.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.adapter.TextTagAdapter;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.utils.ImageUtil;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectBookDialog {

    private final BottomSheetDialog dialog;
    private final OnClickListener listener;
    private final View view;

    private ImageView topicImageIv;

    private List<Book> bookList;
    private Book curBook;

    private final int[] imageTypeArr = {Constants.TOPIC_STEM, Constants.TOPIC_CORRECT,
            Constants.TOPIC_INCORRECT, Constants.TOPIC_KEY, Constants.TOPIC_CAUSE};
    private int curImageType = Constants.TOPIC_STEM;

    public SelectBookDialog(Context context) {
        dialog = new BottomSheetDialog(context);
        listener = (OnClickListener) context;

        view = LayoutInflater.from(context).inflate(R.layout.dialog_view_select_book,null);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(view.getWidth(), view.getHeight());

        topicImageIv = view.findViewById(R.id.select_book_dialog_image);

        Button ensureBtn = view.findViewById(R.id.ensure_btn);
        ensureBtn.setOnClickListener(v -> listener.onSelectBookFinished(curBook, curImageType));

        setTypeTagLayout();
    }

    /**
     * 初始化错题本标签
     * @param curBookId == -1：加载全部， > 1 : 加载具体某一个
     */
    public void initBookAll(int curBookId) {

        if (curBookId <= 1) {
            bookList = LitePal.findAll(Book.class);
            bookList.remove(0);

            if (bookList.isEmpty()) throw new RuntimeException("bookList is empty");

            curBook = bookList.get(0);
        } else {
            bookList = new ArrayList<>();
            curBook = LitePal.find(Book.class, curBookId);
            if (curBook == null) throw new RuntimeException("bookList is empty");

            bookList.add(curBook);
        }

        setBookNameTagLayout(bookList);
    }

    private void setBookNameTagLayout(List<Book> bookList) {

        List<String> bookNameList = new ArrayList<>();

        for (int i = 0; i < bookList.size(); i++) {
            Book book = bookList.get(i);
            bookNameList.add(book.getName());
        }

        TagFlowLayout bookLayout = view.findViewById(R.id.select_book_dialog_note_book_layout);
        bookLayout.setMaxSelectCount(1);

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
        List<String> topicImageTypeArr = Arrays.asList(view.getContext().getResources().getStringArray(R.array.topic_image_type));
        TagFlowLayout topicImageTypeLayout = view.findViewById(R.id.select_book_dialog_topic_image_type_layout);
        topicImageTypeLayout.setMaxSelectCount(1);
        TextTagAdapter adapter = new TextTagAdapter(topicImageTypeArr);
        topicImageTypeLayout.setAdapter(adapter);
        topicImageTypeLayout.setOnTagClickListener((view, position, parent) -> {
            adapter.setCurPosition(position);
            curImageType = imageTypeArr[position];
            topicImageTypeLayout.onChanged();
            return true;
        });

    }

    public void setTopicImageIv(Bitmap bitmap) {

        bitmap = ImageUtil.resizeBitmap(bitmap, bitmap.getWidth()/3, bitmap.getHeight()/3);

        Glide.with(dialog.getContext()).load(bitmap).into(topicImageIv);
    }

    public BottomSheetDialog getDialog() {
        return dialog;
    }

    public interface OnClickListener {
        void onSelectBookFinished(Book book, int imageType);
    }
}
