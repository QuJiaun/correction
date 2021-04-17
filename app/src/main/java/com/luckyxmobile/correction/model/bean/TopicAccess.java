package com.luckyxmobile.correction.model.bean;

import java.util.LinkedHashMap;

public class TopicAccess {

    long crate_date = 0;

    long last_access_date = 0;

    int totalAccess = 0;

    LinkedHashMap<Long, Integer> dateAccess;
}
