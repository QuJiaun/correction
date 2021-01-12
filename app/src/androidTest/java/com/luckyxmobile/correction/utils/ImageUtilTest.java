package com.luckyxmobile.correction.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * author:zc
 * created on:2019/7/25 11:26
 * description:
 */
public class ImageUtilTest {

    /**
     * 测试重置存储路径的方法
     * 可能出现两种情况，1.重置成功，将resultPath置为default，2.重置失败，resultPath保持原样
     */
    @Test
    public void resetResultPath(){
        ImageUtil.resetResultPath();
        Assert.assertEquals("default", ImageUtil.getResultPath());
    }


    /**
     * 测试获取存储路径的方法
     */
    @Test
    public void getResultPath(){
        ImageUtil.resultPath = "d:test";
        Assert.assertEquals("d:test", ImageUtil.getResultPath());

        ImageUtil.resetResultPath();
        Assert.assertEquals("default", ImageUtil.getResultPath());
    }
}
