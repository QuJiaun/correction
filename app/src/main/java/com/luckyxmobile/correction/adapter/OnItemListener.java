package com.luckyxmobile.correction.adapter;

public interface OnItemListener<T> {

    void onItemClick(T t, Object... param);

    void onItemLongClick(T t, Object... param);
}
