package com.luckyxmobile.correction.presenter.impl;

import android.content.Intent;
import android.graphics.Bitmap;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.TopicImageModel;
import com.luckyxmobile.correction.model.impl.TopicImageModelImpl;
import com.luckyxmobile.correction.presenter.CropImageViewPresenter;
import com.luckyxmobile.correction.utils.IFiles;
import com.luckyxmobile.correction.utils.impl.FilesUtils;
import com.luckyxmobile.correction.view.ICropImageView;

public class CropImageViewPresenterImpl implements CropImageViewPresenter {


    private IFiles filesUtil;
    private TopicImageModel topicImageModel;
    private ICropImageView cropImageView;

    public CropImageViewPresenterImpl(ICropImageView cropImageView){
        this.cropImageView = cropImageView;
        topicImageModel = new TopicImageModelImpl();
        filesUtil = new FilesUtils(cropImageView.getCropImageViewContext());
    }

    @Override
    public void saveCropImage(Bitmap cropImage) {

        if (cropImage == null){
            cropImageView.OnToastFinished(cropImageView.getCropImageViewContext().getString(R.string.crop_failed));
        }else{
            //调用util层，保存bitmap到临时文件，获取路径
            String path = filesUtil.saveBitmap2TmpFile(cropImage);
            //返回该文件的路径
            cropImageView.onCropImageFinished(path);
        }

    }

    @Override
    public void getImageByIntentData(Intent data) {

    }

    @Override
    public void addImage2Topic(int topicId, String whichType, String path) {

        topicImageModel.addImage2Topic(topicId, whichType, path);
    }
}
