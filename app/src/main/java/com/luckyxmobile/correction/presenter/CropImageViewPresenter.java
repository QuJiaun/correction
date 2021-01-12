package com.luckyxmobile.correction.presenter;

import android.content.Intent;
import android.graphics.Bitmap;

public interface CropImageViewPresenter {

    void saveCropImage(Bitmap cropImage);

    void getImageByIntentData(Intent data);

    void addImage2Topic(int topicId, String whichType, String path);
}
