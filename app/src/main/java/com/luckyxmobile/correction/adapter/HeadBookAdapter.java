package com.luckyxmobile.correction.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.ui.activity.MainActivity;
import com.noober.menu.FloatMenu;

import java.util.List;

/**
 *
 */
public class HeadBookAdapter extends RecyclerView.Adapter<HeadBookAdapter.ViewHolder> {

    public static int MENU_DELETE = 0;
    public static int MENU_BOOK_INFO = 1;

    private final List<Book> bookList;
    private final Context context;
    private final OnHeadBookAdapterListener listener;
    private final FloatMenu menu;
    private Animation onLongClickAnim;

    private static final String TAG = "HeadBookAdapter";

    public HeadBookAdapter(Context context,List<Book> bookList){
        this.context = context;
        this.bookList = bookList;
        this.listener = (OnHeadBookAdapterListener) context;

        menu = new FloatMenu((Activity) context);
        menu.items(350,
                context.getString(R.string.delete),
                context.getString(R.string.change_notebook_info));

        onLongClickAnim = AnimationUtils.loadAnimation(context,R.anim.layout_longpress);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_item_book, parent, false);
        return new HeadBookAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        //设置封面图片和删除效果
        if (position == 0){
            holder.bookImage.setImageResource(R.drawable.ic_favorite);
        }else{
            //Glide 加载封面图
            Glide.with(context)
                    .load(bookList.get(position).getBook_cover())
                    .placeholder(R.drawable.correction_book)
                    .fitCenter()
                    .centerCrop()
                    .skipMemoryCache(true) // 不使用内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                    .into(holder.bookImage);
        }


        holder.bookName.setText(bookList.get(position).getBook_name());

        holder.bookLayout.setOnClickListener(v ->
                listener.onBookClickListener(bookList.get(position), position));

        holder.bookLayout.setOnLongClickListener(v -> {
            menu.showAsDropDown(holder.itemView);
            holder.bookLayout.startAnimation(onLongClickAnim);
            return false;
        });

        menu.setOnItemClickListener((v, positionMenu) ->
                listener.onBookMenuClickListener(positionMenu, bookList.get(position)));

    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void addBook(Book book){
        if (book != null){
            bookList.add(book);
            notifyDataSetChanged();
        }
    }

    public void deleteBook(Book book){
        int i=0;
        for (Book b: bookList){
            i++;
            if (b.getId() == book.getId()){
                bookList.remove(b);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    public void alterBook(Book book){
        if (book != null){
            for (Book bk : bookList){
                if (bk.getId() == book.getId()){
                    bk.setBook_name(book.getBook_name());
                    bk.setBook_cover(book.getBook_cover());
                    notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView bookImage;
        TextView bookName;
        RelativeLayout bookLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookLayout = itemView.findViewById(R.id.book_layout);
            bookImage = itemView.findViewById(R.id.book_image);
            bookName = itemView.findViewById(R.id.book_name);

        }
    }


    //声明点击、长按接口供外部调用
    public interface OnHeadBookAdapterListener {
        void onBookClickListener(Book book, int position);
        void onBookMenuClickListener(int menuPosition, Book book);
    }


}
