package com.luckyxmobile.correction.global;

import com.luckyxmobile.correction.R;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.annotation.IntDef;

public class Constants {

    //SharedPreferences
    public static final String TABLE_SHARED_CORRECTION = "correction_sharePer_table";
    public static final String TABLE_SHARED_IS_NEWEST_ORDER = "is_newest_order";
    public static final String TABLE_SHARED_IS_FIRST_START = "is_first_start";
    public static final String TABLE_FROM_BOOK_ID = "from_book_id";
    public static final String TABLE_PRINT_PAGE = "print_page";
    public static final String TABLE_SHOW_SMEAR = "show_smear";
    public static final String TABLE_FULL_SCREEN = "is_full_screen";
    public static final String TABLE_SHOW_TAG = "is_show_tag";
    public static final String TABLE_VIEW_SMEAR_BY = "show_smear_by";
    public static final String TABLE_SHOW_SMEAR_MARK = "print_smear_content";
    public static final String TABLE_FROM_FAVORITE = "from_favorite";

    //多少秒点击一次 默认1秒
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    public static final int MIN_CLICK_TOW_TIME = 500;

    public static final int REQUEST_PERMISSION = 100;
    public static final int REQUEST_CODE_TAKE_PHOTO = 200;
    public static final int REQUEST_CODE_SELECT_ALBUM = 300;
    public static final int REQUEST_CODE = 400;

    //intent参数
    public static final String WHETHER_FROM_ALBUM = "whether_from_album";
    public static final String WHICH_IMAGE = "which_image";
    public static final String WHICH_ACTIVITY = "which_activity";
    public static final String TOPIC_ID = "topic_id";
    public static final String TOPIC_POSITION = "topic_position";
    public static final String IMAGE_POSITION = "image_position";
    public static final String BOOK_ID = "book_id";
    public static final String WHETHER_EDIT_PHOTO = "whether_edit_photo";
    public static final String TOOLBAR_NAME = "toolbar_name";
    public static final String IMAGE_PATH = "image_path";
    public static final String IS_TOPIC = "is_topic";

    //book
    public static final  int IMAGE_BOOK_COVER = 0x101;

    //topic_image
    public static final int TOPIC_STEM = R.string.stem;
    public static final int TOPIC_CORRECT = R.string.correct;
    public static final int TOPIC_INCORRECT = R.string.incorrect;
    public static final int TOPIC_KEY = R.string.key;
    public static final int TOPIC_CAUSE = R.string.cause;

    @IntDef({TOPIC_STEM, TOPIC_CORRECT, TOPIC_INCORRECT, TOPIC_KEY, TOPIC_CAUSE})
    @Target({ElementType.PARAMETER, ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TopicImageType {

    }

    //对比度
    public static final int CONTRAST_RADIO_WEAK = 0x210;
    public static final int CONTRAST_RADIO_COMMON = 0x211;
    public static final int CONTRAST_RADIO_STRONG = 0x212;

    @IntDef({CONTRAST_RADIO_COMMON, CONTRAST_RADIO_WEAK, CONTRAST_RADIO_STRONG})
    @Target({ElementType.PARAMETER, ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ContrastRadio{

    }

    //涂抹工具类型
    public static final int PAINT_BLUE = 0x220;
    public static final int PAINT_RED = 0x222;
    public static final int PAINT_GREEN = 0x223;
    public static final int PAINT_YELLOW = 0x224;
    public static final int PAINT_ERASE = 0x225;
    public static final int PAINT_WHITE_OUT = 0x226;

    @IntDef({PAINT_BLUE, PAINT_RED, PAINT_GREEN, PAINT_YELLOW, PAINT_ERASE, PAINT_WHITE_OUT})
    @Target({ElementType.PARAMETER, ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HighlighterType{

    }

    //橡皮擦宽度
    public static final int ERASE_THIN = 60, ERASE_MEDIUM = 80, ERASE_THICK = 100;

    //画笔宽度
    public static final int PAINT_THIN = 40, PAINT_MEDIUM = 60, PAINT_THICK = 80;

    public static final String FILE_PROVIDER  = "com.luckyxmobile.correction.fileprovider";

}
