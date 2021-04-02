package com.luckyxmobile.correction.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Book;

import java.util.List;

/**
 *
 */
public class HeadBookAdapter extends RecyclerView.Adapter<HeadBookAdapter.ViewHolder> {

    private static final String TAG = "HeadBookAdapter";

    private final List<Book> bookList;
    private final Context context;
    private final OnHeadBookAdapterListener listener;
    private final Animation onLongClickAnim;

    public HeadBookAdapter(Context context,List<Book> bookList){

        this.context = context;
        this.bookList = bookList;
        this.listener = (OnHeadBookAdapterListener) context;

        onLongClickAnim = AnimationUtils.loadAnimation(context,R.anim.layout_longpress);
    }

    public List<Book> getBookList() {
        return bookList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_item_book, parent, false);
        return new HeadBookAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Book book = bookList.get(position);

        //设置封面图片和删除效果
        if (book.getId() == 1 && position == 0){
            holder.bookImage.setImageResource(R.drawable.ic_favorite);
            holder.bookName.setTextColor(context.getColor(R.color.text_checked));
        }else if (!TextUtils.isEmpty(book.getCover())){
            //Glide 加载封面图
            Glide.with(context)
                    .load(book.getCover())
                    .error(R.drawable.ic_broken_image)
                    .skipMemoryCache(true) // 不使用内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                    .into(holder.bookImage);
        } else {
            Glide.with(context)
                    .load(R.drawable.correction_book)
                    .skipMemoryCache(true) // 不使用内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                    .into(holder.bookImage);
        }

        holder.bookName.setText(book.getName());

        holder.bookLayout.setOnClickListener(v ->
                listener.onBookClickListener(book));

        holder.bookLayout.setOnLongClickListener(v -> {
            if (book.getId() == 1) return true;
            listener.onBookLongClickListener(holder.bookLayout, book);
            holder.bookLayout.startAnimation(onLongClickAnim);
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void addBook(Book book){
        if (book != null){
            bookList.add(book);
            notifyItemInserted(bookList.size()-1);
        }
    }

    public void upBook(Book book) {
        int index = 0;
        for (Book b : bookList) {
            index++;
            if (b.getId() == book.getId()) {
                b.setName(book.getName());
                b.setCover(book.getCover());
                notifyItemChanged(index);
                return;
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView bookImage;
        TextView bookName;
        LinearLayout bookLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookLayout = itemView.findViewById(R.id.book_layout);
            bookImage = itemView.findViewById(R.id.book_image);
            bookName = itemView.findViewById(R.id.book_name);
        }
    }


    //声明点击、长按接口供外部调用
    public interface OnHeadBookAdapterListener {
        void onBookClickListener(Book book);
        void onBookLongClickListener(View view, Book book);
    }


}
