package com.luckyxmobile.correction.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Book;
import org.litepal.LitePal;

public class LanguageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving

        Book book = LitePal.find(Book.class, 1);
        book.setBook_name(context.getString(R.string.favorites));
        book.save();
    }
}
