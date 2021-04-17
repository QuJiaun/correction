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
        MyPreferences.getInstance().init(this);

        int theme = MyPreferences.getInstance().getInt(Constants.TABLE_APP_THEME,
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setMyTheme(theme);
        initSQLFirst();
    }

    public static void setMyTheme(int them) {
        AppCompatDelegate.setDefaultNightMode(them);
    }

    /**
     * 在第一次安装时，初始化数据库
     */
    private void initSQLFirst(){

        MyPreferences table = MyPreferences.getInstance();

        if (table.getBoolean(Constants.IS_FIRST_START, true)) {
            table.clearAll();
            table.putBoolean(Constants.IS_FIRST_START, false);

            new Book(getString(R.string.favorites), "R.mipmap.favorite").save();

            new Tag("重要❤").save();
            new Tag("掌握👌").save();
            new Tag("马虎开🤦‍♂").save();

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            table.putInt(Constants.SCREEN_WIDTH, displayMetrics.widthPixels);
            table.putInt(Constants.SCREEN_HEIGHT, displayMetrics.heightPixels);
        }
    }
}
