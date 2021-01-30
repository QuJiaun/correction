package com.luckyxmobile.camera;

import android.content.Context;
import android.os.Build;

import com.yanzhenjie.permission.AndPermission;


public class PermissionUtils {

    public interface PermissionListener {
        /**
         * 成功
         */
        void onSuccess(Context context);

        /**
         * 失败
         */
        void onFailed(Context context);
    }


    public static void applicationPermissions(Context context, PermissionListener listener, String[]... permissions) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (!AndPermission.hasPermissions(context, permissions)) {
                AndPermission.with(context)
                        .runtime()
                        .permission(permissions)
                        .rationale((mContext, data, executor) -> {
                            //选择显示提示弹窗
                            executor.execute();
                        })
                        .onGranted((permission) -> {
                            listener.onSuccess(context);
                        })
                        .onDenied((permission) -> {
                            listener.onFailed(context);
                        })
                        .start();
            } else {
                listener.onSuccess(context);
            }
        } else {
            listener.onSuccess(context);
        }
    }

    public static void applicationPermissions(Context context, PermissionListener listener, String... permissions) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!AndPermission.hasPermissions(context, permissions)) {
                AndPermission.with(context)
                        .runtime()
                        .permission(permissions)
                        .rationale((mContext, data, executor) -> {
                            //选择显示提示弹窗
                            executor.execute();
                        })
                        .onGranted((permission) -> {
                            listener.onSuccess(context);
                        })
                        .onDenied((permission) -> {
                            listener.onFailed(context);
                        })
                        .start();
            } else {
                listener.onSuccess(context);
            }
        } else {
            listener.onSuccess(context);
        }
    }

}
