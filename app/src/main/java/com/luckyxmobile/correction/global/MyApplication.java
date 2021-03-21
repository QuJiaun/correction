package com.luckyxmobile.correction.global;

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
    }

    /**
     * 在第一次安装时，初始化数据库
     */
    private void initSQLFirst(){

        MySharedPreferences table = MySharedPreferences.getInstance();

        if (table.getBoolean(Constants.TABLE_SHARED_IS_FIRST_START, true)) {
            table.clearAll();
            table.putBoolean(Constants.TABLE_SHARED_IS_FIRST_START, false);

            Book book = new Book(getString(R.string.favorites), "R.mipmap.favorite");
            book.save();

            Tag tag = new Tag("重要");
            tag.save();

        }
    }
}
