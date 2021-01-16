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
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Book;
import com.noober.menu.FloatMenu;

import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class HeadBookAdapter extends RecyclerView.Adapter<HeadBookAdapter.ViewHolder> {

    private static final String TAG = "HeadBookAdapter";

    public static int MENU_DELETE = 0;
    public static int MENU_BOOK_INFO = 1;

    private final List<Book> bookList;
    private final Context context;
    private final OnHeadBookAdapterListener listener;
    private final FloatMenu menu;
    private final Animation onLongClickAnim;

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Book book = bookList.get(position);

        //设置封面图片和删除效果
        if (position == 0){
            holder.bookImage.setImageResource(R.drawable.ic_favorite);
        }else{
            //Glide 加载封面图
            Glide.with(context)
                    .load(book.getCover())
                    .placeholder(R.drawable.correction_book)
                    .fitCenter()
                    .centerCrop()
                    .into(holder.bookImage);
        }

        holder.bookName.setText(book.getName());

        holder.bookLayout.setOnClickListener(v ->
                listener.onBookClickListener(book));

        holder.bookLayout.setOnLongClickListener(v -> {
            menu.showAsDropDown(holder.itemView);
            holder.bookLayout.startAnimation(onLongClickAnim);
            return false;
        });

        menu.setOnItemClickListener((v, positionMenu) -> {
            if (positionMenu == MENU_DELETE) {
                listener.onBookMenuRemove(book);
                bookList.remove(position);
                notifyItemRemoved(position);
            } else if(positionMenu == MENU_BOOK_INFO) {
                listener.onBookMenuAlter(book);
            }
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

    public void alterBook(Book book){
        int position = -1;
        for (Book b : bookList) {
            position++;
            if (b.equals(book)) {
                b.setName(book.getName());
                b.setCover(book.getCover());
                notifyItemChanged(position);
                return;
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
        void onBookClickListener(Book book);
        void onBookMenuRemove(Book book);
        void onBookMenuAlter(Book book);
    }


}
