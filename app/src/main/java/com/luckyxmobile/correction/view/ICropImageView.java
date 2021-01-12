package com.luckyxmobile.correction.view;

import android.content.Context;

public interface ICropImageView {

    void onCropImageFinished(String cropImagePath);

    Context getCropImageViewContext();

    void OnToastFinished(String showLog);
}
