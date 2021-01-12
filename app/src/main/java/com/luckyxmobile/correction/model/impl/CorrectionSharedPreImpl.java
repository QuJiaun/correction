package com.luckyxmobile.correction.model.impl;

import android.content.Context;
import android.content.SharedPreferences;

import com.luckyxmobile.correction.utils.ConstantsUtil;

public class CorrectionSharedPreImpl {

    private static volatile CorrectionSharedPreImpl preferencesImpl;
    private  SharedPreferences preferences;
    private Context context;


    private CorrectionSharedPreImpl(Context context){
        this.context = context;
    }

    public static CorrectionSharedPreImpl initPreferencesImpl(Context context) {
        if (preferencesImpl == null){
            synchronized (CorrectionSharedPreImpl.class){
                if (preferencesImpl == null){
                    preferencesImpl = new CorrectionSharedPreImpl(context);
                }
            }
        }
        return preferencesImpl;
    }

    public SharedPreferences.Editor getEditor(){
        return preferences.edit();
    }

    public SharedPreferences getPreferences() {
        if (preferences == null){
            preferences = context.getSharedPreferences(ConstantsUtil.TABLE_SHARED_CORRECTION, Context.MODE_PRIVATE);
        }
        return preferences;
    }
}
