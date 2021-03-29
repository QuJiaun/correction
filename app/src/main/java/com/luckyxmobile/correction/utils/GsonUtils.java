package com.luckyxmobile.correction.utils;

import com.google.gson.Gson;

public class GsonUtils {

    private static Gson gson = null;

    static {
        gson = new Gson();
    }

    private GsonUtils() {
    }


    public static String obj2Json(Object object) {
        String result = null;
        if (gson != null) {
            result = gson.toJson(object);
        }
        return result;
    }

    public static <T> T json2Obj(String json, Class<T> cls) {
        T t = null;
        if (gson != null) {
            t = gson.fromJson(json, cls);
        }
        return t;
    }

}