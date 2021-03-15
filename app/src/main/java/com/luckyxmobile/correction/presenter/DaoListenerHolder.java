package com.luckyxmobile.correction.presenter;

import com.luckyxmobile.correction.model.DaoListener;

import java.util.HashSet;
import java.util.Set;

public class DaoListenerHolder {

    private Set<DaoListener> listenerSet = new HashSet<>();

    private DaoListenerHolder() {}

    private static DaoListenerHolder holder = new DaoListenerHolder();

    public static DaoListenerHolder getInstance() {
        return holder;
    }

    public void registerDaoListener(DaoListener listener){
        listenerSet.add(listener);
    }

    public void removeDaoListener(DaoListener listener) {
        listenerSet.remove(listener);
    }

    public void clearAllListener() {
        listenerSet.clear();
    }

    public void onDeleteFinished(DaoListener listener) {
        for (DaoListener d : listenerSet) {
            if (d.equals(listener)) {
                d.onDeleteFinished();
            }
        }
    }

}
