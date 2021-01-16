package com.luckyxmobile.correction;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.litepal.LitePal;
import org.litepal.LitePalDB;

public class LitePalTestCase {
    protected PaperTopicDao PaperTopic = new PaperTopicDaoImpl();


    @BeforeClass
    public static void initDB(){
        LitePalDB litePalDB = LitePalDB.fromDefault("test_correction");
        LitePal.use(litePalDB);
    }


    @AfterClass
    public static void destroyDB(){
        LitePal.deleteDatabase("test_correction");
    }

}
