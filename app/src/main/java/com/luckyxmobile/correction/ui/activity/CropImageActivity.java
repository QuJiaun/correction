package com.luckyxmobile.correction.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.utils.DestroyActivityUtil;
import com.luckyxmobile.correction.utils.ImageUtil;
import com.luckyxmobile.correction.utils.FilesUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.BindView;
import me.pqpo.smartcropperlib.view.CropImageView;

/**
 * @author zc
 */
public class CropImageActivity extends AppCompatActivity{

    public static final String TAG = "CropActivity";

    @BindView(R.id.crop_image_view)
    CropImageView cropImageView;

    @BindView(R.id.next_btn)
    Button nextBtn;


    private boolean isEdit = true;
    private Bitmap imageBitmap = null;

    public static Intent getCropImageActivityIntent(Activity activity, boolean fromAlbum, boolean isEdit){
        Intent intent = new Intent(activity, CropImageActivity.class);
        intent.putExtra(Constants.IS_FROM_ALBUM,fromAlbum);
        intent.putExtra(Constants.IS_EDIT_PHOTO, isEdit);

        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 将activity设置为全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_crop_actovoty);
        ButterKnife.bind(this);

        // 得到传入的action和type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        isEdit = intent.getBooleanExtra(Constants.IS_EDIT_PHOTO, true);
        boolean isFromAlbum = intent.getBooleanExtra(Constants.IS_FROM_ALBUM, true);

        Uri exterUri = null;

        //判断跳转来源与类型
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                //得到Uri
                exterUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            }
        }

        if(exterUri != null){
            //外部分享
            imageBitmap = ImageUtil.getImage(exterUri, getContentResolver());

            if (imageBitmap != null) {
                cropImageView.setImageBitmap(imageBitmap);
            }else {
                onToast("分享失败");
                onBackPressed();
                startActivity(new Intent(this, MainActivity.class));
            }

        } else {
            //内部选择
            if (isFromAlbum) {
                Intent selectIntent = new Intent(Intent.ACTION_GET_CONTENT);
                selectIntent.setType("image/*");
                startActivityForResult(selectIntent, Constants.REQUEST_CODE_SELECT_ALBUM);
            } else {
                Intent startCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FilesUtils.getInstance().getCacheFileUri());
                startActivityForResult(startCameraIntent, Constants.REQUEST_CODE_TAKE_PHOTO);
            }
        }

        cropImageView.setDragLimit(true);

        if (!isEdit){
            nextBtn.setText(getString(R.string.finish));
        }
    }

    @OnClick(R.id.exit_btn)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    @OnClick(R.id.next_btn)
    public void onClickNext(){

        if(cropImageView.canRightCrop()) {//判断选区是否为凸四边形,bitmap是否为空
            Bitmap bitmap = cropImageView.crop();
            FilesUtils.getInstance().saveBitmap2TmpFile(bitmap);
            onCropImageFinished(FilesUtils.getInstance().getCacheFilePath());
        }else{
            onToast(getString(R.string.crop_failed));
        }
    }

    @OnClick(R.id.reset_btn)
    public void onClickReset(){
        cropImageView.setFullImgCrop();
    }

    @OnClick(R.id.rotate_btn)
    public void onClickRotate(){
        cropImageView.setImageToCrop(ImageUtil.rotateBitmap(cropImageView.getBitmap(),-90));
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        imageBitmap = null;

        if (resultCode == RESULT_OK) {
            //获取图片（来自拍照或图），传给imageBitmap
            if (requestCode == Constants.REQUEST_CODE_TAKE_PHOTO) {
                imageBitmap = ImageUtil.getBitmapByImagePath(FilesUtils.getInstance().getCacheFilePath());
            } else if (requestCode == Constants.REQUEST_CODE_SELECT_ALBUM) {
                if (data.getData() != null) {
                    imageBitmap = ImageUtil.getImage(data.getData(), getContentResolver());
                }
            }
        } else if (resultCode == RESULT_CANCELED){
            onBackPressed();
            return;
        }

        //将读取后的图片，传给裁剪框架
        if (imageBitmap != null) {
            cropImageView.setImageToCrop(imageBitmap);
        } else {
            onBackPressed();
            onToast("图片获取失败");
        }
    }


    public void onCropImageFinished(String cropImagePath) {

        if (!isEdit){
            Intent intent = new Intent();
            intent.putExtra(Constants.IMAGE_PATH, cropImagePath);
            setResult(RESULT_OK, intent);
        }else{
            DestroyActivityUtil.addDestroyActivityToMap(CropImageActivity.this,TAG);
//            跳转到EditPhotoActivity页面
            Intent intent = new Intent(this, EditTopicImageActivity.class);
            intent.putExtra(Constants.IMAGE_PATH,  cropImagePath);
            startActivity(intent);
        }

        finish();
    }

    public void onToast(String showLog) {
        Toast.makeText(this, showLog, Toast.LENGTH_SHORT).show();
    }
}
