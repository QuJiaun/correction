package com.luckyxmobile.correction.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Paper;

import java.util.List;

public class PaperAdapter extends RecyclerView.Adapter<PaperAdapter.ViewHolder> {

    private final List<Paper> paperList;
    private final OnItemListener listener;

    public PaperAdapter(OnItemListener listener, List<Paper> paperList) {
        this.listener = listener;
        this.paperList = paperList;
    }

    public boolean isEmpty() {
        return paperList.isEmpty();
    }

    public void remove(Paper paper) {
        int index = paperList.indexOf(paper);
        paperList.remove(index);
        notifyItemRemoved(index);
    }

    public void refresh(Paper paper) {
        int index = paperList.indexOf(paper);
        notifyItemChanged(index);
    }

    public void addPaper(Paper paper) {
        paperList.add(paper);
        notifyItemInserted(paperList.size()-1);
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item_paper, parent, false);
        return new ViewHolder(view);
    }

    int curFocused = -1;

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        Paper paper = paperList.get(position);
        viewHolder.paperNameTv.setText(paper.getPaperName());

        if (curFocused == position) {
            viewHolder.itemView.setBackgroundResource(R.drawable.shape_box_check_bg);
            curFocused = -1;
        } else {
            viewHolder.itemView.setBackgroundResource(R.drawable.shape_box_view);
        }

        viewHolder.menuBtn.setOnClickListener(view -> {
            listener.showPopupMenu(viewHolder.menuBtn, paper, position);
            curFocused = position;
            notifyItemChanged(position);
        });

        viewHolder.itemView.setOnLongClickListener(view -> {
            listener.showPopupMenu(viewHolder.menuBtn, paper, position);
            curFocused = position;
            notifyItemChanged(position);
            return true;
        });

        viewHolder.itemView.setOnClickListener(view -> listener.onItemClick(paper));
    }

    @Override
    public int getItemCount() {
        return paperList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageButton menuBtn;
        TextView paperNameTv;

         ViewHolder(View itemView) {
            super(itemView);
            menuBtn = itemView.findViewById(R.id.paper_menu_btn);
            paperNameTv = itemView.findViewById(R.id.paper_name_tv);
        }
    }

    public interface OnItemListener {
        void onItemClick(Paper paper);
        void showPopupMenu(View view, Paper paper, int position);
    }
}
