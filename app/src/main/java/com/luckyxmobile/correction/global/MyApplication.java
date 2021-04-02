package com.luckyxmobile.correction.global;

import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatDelegate;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Tag;
import com.luckyxmobile.correction.utils.FilesUtils;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

public class MyApplication extends LitePalApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        LitePal.initialize(this);
        FilesUtils.getInstance().init(this);
        MySharedPreferences.getInstance().init(this);

        initSQLFirst();

        //日间 切换 夜间
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    /**
     * 在第一次安装时，初始化数据库
     */
    private void initSQLFirst(){

        MySharedPreferences table = MySharedPreferences.getInstance();

        if (table.getBoolean(Constants.IS_FIRST_START, true)) {
            table.clearAll();
            table.putBoolean(Constants.IS_FIRST_START, false);

            Book book = new Book(getString(R.string.favorites), "R.mipmap.favorite");
            book.save();

            Tag tag = new Tag("重要");
            tag.save();

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            table.putInt(Constants.SCREEN_WIDTH, displayMetrics.widthPixels);
            table.putInt(Constants.SCREEN_HEIGHT, displayMetrics.heightPixels);
        }
    }
}
