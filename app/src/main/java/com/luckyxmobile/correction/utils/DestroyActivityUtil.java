package com.luckyxmobile.correction.utils;

import android.app.Activity;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qjj
 * @date 2019/08/26
 * 用于销毁指定活动
 */
public class DestroyActivityUtil {
    private static Map<String, WeakReference<Activity>> destroyMap = new HashMap<>();

    public static void add(@NonNull Activity activity) {
        destroyMap.put(getName(activity), new WeakReference<>(activity));
    }

    public static void destroy(@NonNull Activity activity) {
        String TAG = getName(activity);
        WeakReference<Activity> weakReference = destroyMap.get(TAG);
        if (weakReference != null) {
            weakReference.get().finish();
            weakReference.clear();
            destroyMap.remove(TAG);
        } else {
            activity.finish();
        }
    }

    public static void clear() {
        for (WeakReference<Activity> weakReference : destroyMap.values()) {
            Activity activity = weakReference.get();
            if (activity != null) {
                activity.finish();
            }
            weakReference.clear();
        }
        destroyMap.clear();
    }

    private static String getName(Activity activity) {
        return activity.getClass().getSimpleName();
    }
}
