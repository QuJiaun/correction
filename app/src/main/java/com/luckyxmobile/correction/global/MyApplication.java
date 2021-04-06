package com.luckyxmobile.correction.global;

import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatDelegate;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Paper;
import com.luckyxmobile.correction.model.bean.Tag;
import com.luckyxmobile.correction.ui.dialog.ProgressDialog;
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

        setThem(MySharedPreferences.getInstance().getString(Constants.TABLE_APP_THEME,"0"));
        initSQLFirst();
    }

    public static void setThem(String them) {
        switch (them) {
            case "0":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;

            case "1":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;

            case "2":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }
    }

    /**
     * 在第一次安装时，初始化数据库
     */
    private void initSQLFirst(){

        MySharedPreferences table = MySharedPreferences.getInstance();

        if (table.getBoolean(Constants.IS_FIRST_START, true)) {
            table.clearAll();
            table.putBoolean(Constants.IS_FIRST_START, false);

            new Book(getString(R.string.favorites), "R.mipmap.favorite").save();
            new Book("数学", "default").save();
            new Book("语文", "default").save();
            new Book("英语", "default").save();

            new Tag("重要❤").save();
            new Tag("掌握👌").save();
            new Tag("马虎开🤦‍♂").save();

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            table.putInt(Constants.SCREEN_WIDTH, displayMetrics.widthPixels);
            table.putInt(Constants.SCREEN_HEIGHT, displayMetrics.heightPixels);
        }
    }
}
