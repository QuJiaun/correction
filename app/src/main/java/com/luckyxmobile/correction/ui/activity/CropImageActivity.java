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
import com.luckyxmobile.correction.ui.dialog.ProgressDialog;
import com.luckyxmobile.correction.ui.views.CropImageView;
import com.luckyxmobile.correction.utils.DestroyActivityUtil;
import com.luckyxmobile.correction.utils.BitmapUtils;
import com.luckyxmobile.correction.utils.FilesUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.BindView;


public class CropImageActivity extends AppCompatActivity{

    @BindView(R.id.cropImageView)
    CropImageView cropImageView;

    @BindView(R.id.next_btn)
    Button nextBtn;

    private Intent curIntent;
    private boolean isEdit = true;
    private FilesUtils filesUtils = FilesUtils.getInstance();
    private Bitmap imageBitmap = null;
    private ProgressDialog progressDialog;

    public static Intent getIntent(Activity activity, boolean fromAlbum, boolean isEdit){
        Intent intent = new Intent(activity, CropImageActivity.class);
        intent.putExtra(Constants.FROM_ALBUM,fromAlbum);
        intent.putExtra(Constants.IS_EDIT_PHOTO, isEdit);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 将activity设置为全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_crop_activity);
        ButterKnife.bind(this);

        cropImageView.setDragLimit(true);

        // 得到传入的action和type
        curIntent = getIntent();
        String action = curIntent.getAction();
        String type = curIntent.getType();

        isEdit = curIntent.getBooleanExtra(Constants.IS_EDIT_PHOTO, true);
        boolean isFromAlbum = curIntent.getBooleanExtra(Constants.FROM_ALBUM, true);

        Uri exterUri = null;

        //判断跳转来源与类型
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                //得到Uri
                exterUri = curIntent.getParcelableExtra(Intent.EXTRA_STREAM);
            }
        }

        if(exterUri != null){
            //外部分享
            imageBitmap = BitmapUtils.getImage(exterUri, getContentResolver());

            if (imageBitmap != null) {
                cropImageView.setImageToCrop(imageBitmap);
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
                startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FilesUtils.getInstance().getTmpFileUri());
                startActivityForResult(startCameraIntent, Constants.REQUEST_CODE_TAKE_PHOTO);
            }
        }

        if (!isEdit){
            nextBtn.setText(getString(R.string.finish));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cropImageView.destroy();
    }

    @OnClick(R.id.exit_btn)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (imageBitmap != null) {
            imageBitmap.recycle();
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    @OnClick(R.id.next_btn)
    public void onClickNext(){
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this) {
                @Override
                public boolean onPreExecute() {
                    if(!cropImageView.canRightCrop()) {//判断选区是否为凸四边形,bitmap是否为空
                        onToast(getString(R.string.crop_failed));
                        return false;
                    }
                    return true;
                }
                @Override
                public boolean doInBackground() throws Exception {
                    Bitmap bitmap = cropImageView.crop();
                    return filesUtils.saveBitmap2TmpFile(bitmap);
                }
                @Override
                public void onPostExecute(boolean result) {
                    super.onPostExecute(result);
                    if (result) {
                        onCropImageFinished(filesUtils.getTmpFilePath());
                    }
                }
            };
        }
        progressDialog.start();
    }

    @OnClick(R.id.reset_btn)
    public void onClickReset(){
        cropImageView.setFullImgCrop();
    }

    @OnClick(R.id.rotate_btn)
    public void onClickRotate(){
        cropImageView.setImageRotate(-90);
    }

    @OnClick(R.id.autoScan)
    public void onClickScan() {
        cropImageView.autoScan();
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
                imageBitmap = BitmapUtils.getBitmapToSize(FilesUtils.getInstance().getTmpFilePath());
            } else if (requestCode == Constants.REQUEST_CODE_SELECT_ALBUM) {
                if (data.getData() != null) {
                    imageBitmap = BitmapUtils.getImage(data.getData(), getContentResolver());
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
            finish();
        }else{
            DestroyActivityUtil.add(this);
//            跳转到EditPhotoActivity页面
            Intent intent = new Intent(this, EditTopicImageActivity.class);
            intent.putExtra(Constants.FROM_ACTIVITY, curIntent.getStringExtra(Constants.FROM_ACTIVITY));
            setExtraInt(intent, Constants.BOOK_ID);
            setExtraInt(intent, Constants.TOPIC_ID);
            setExtraInt(intent, Constants.TOPIC_IMAGE_ID);
            intent.putExtra(Constants.IMAGE_PATH,  cropImagePath);
            startActivity(intent);
        }
    }

    public void setExtraInt(Intent intent, String name) {
        if (curIntent.hasExtra(name)) {
            intent.putExtra(name, curIntent.getIntExtra(name, -1));
        }
    }

    public void onToast(String showLog) {
        Toast.makeText(this, showLog, Toast.LENGTH_SHORT).show();
    }
}
