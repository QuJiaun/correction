package com.luckyxmobile.correction.utils.impl;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import com.luckyxmobile.correction.global.Constants;

import java.util.ArrayList;
import java.util.List;

public class PermissionsUtil {

    private static final String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};


    /**
     * 初始申请权限
     * @param activity activity
     */
    public static void initRequestPermission(Activity activity){
        checkPermission(activity, permissions);
    }

    /**
     * 检查相应的权限，没有就申请
     * @param activity activity
     * @param permissions 权限组
     */
    public static void checkPermission(Activity activity, String... permissions){

        List<String> deniedPer = new ArrayList<>();

        for (String per : permissions){
            if (activity.checkSelfPermission(per) == PackageManager.PERMISSION_DENIED){
                deniedPer.add(per);
            }
        }

        if (!deniedPer.isEmpty()){
            activity.requestPermissions( deniedPer.toArray(new String[]{}), Constants.REQUEST_PERMISSION);
        }
    }
}
