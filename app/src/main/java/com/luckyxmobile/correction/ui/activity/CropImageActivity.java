package com.luckyxmobile.correction.ui.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.presenter.CropImageViewPresenter;
import com.luckyxmobile.correction.presenter.impl.CropImageViewPresenterImpl;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.utils.DestroyActivityUtil;
import com.luckyxmobile.correction.utils.ImageUtil;
import com.luckyxmobile.correction.utils.impl.FilesUtils;

import java.io.FileNotFoundException;

import me.pqpo.smartcropperlib.view.CropImageView;

import com.luckyxmobile.correction.view.ICropImageView;

/**
 * @author zc
 */
public class CropImageActivity extends AppCompatActivity implements View.OnClickListener, ICropImageView {

    public static final String TAG = "CropActivity";
    private CropImageView cropImageView;
    private String whichActivity;
    /**用于判断是否来自相册*/
    private boolean isFromAlbum;
    /**判断哪种图片类型*/
    private String imageType;
    /**判断是否需要编辑图片*/
    private boolean isEditPhoto = true;
    private int topicID = -1;
    /**临时文件*/
//    private File tempFile;
    /**照片bitmap*/
    private Bitmap imageBitmap = null;
    private boolean isTopic = true;
    /**从外部App进入该Activity的图片资源*/
    private Uri exterUri;

    private CropImageViewPresenter cropImageViewPresenter;

    /**
     *
     * @param fromAlbum 是否来自相册
     * @param whichImage 图片类型
     * @param isEditPhoto 是否进行编辑图片
     * @param id -1表示是一个新题
     * @param isTopic 可能是题 可能是错题本封面
     * @return intent
     */
    public static Intent getJumpIntent(Context context,String whichActivity,boolean fromAlbum,String whichImage,boolean isEditPhoto,boolean isTopic,int id){
        Intent intent = new Intent(context, CropImageActivity.class);
        intent.putExtra(Constants.WHICH_ACTIVITY,whichActivity);
        intent.putExtra(Constants.WHETHER_FROM_ALBUM,fromAlbum);
        intent.putExtra(Constants.WHICH_IMAGE,whichImage);
        intent.putExtra(Constants.WHETHER_EDIT_PHOTO,isEditPhoto);
        intent.putExtra(Constants.TOPIC_ID,id);
        intent.putExtra(Constants.IS_TOPIC,isTopic);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 将activity设置为全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_crop_actovoty);

        // 得到传入的action和type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        //判断跳转来源与类型
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                //得到Uri
                exterUri = handleSendImageUri(intent); // Handle single image being sent
            }
        }
        //初始化操作
        initViewData();
        //判断如果来源于外部程序跳转进行的操作
        if(exterUri!=null){
            ContentResolver contentResolver = getContentResolver();
            Uri bmpUri = exterUri;
            try {
                imageType = Constants.IMAGE_ORIGINAL;
                whichActivity = MainActivity.TAG;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(contentResolver.openInputStream(bmpUri), new Rect(), options);
                options.inJustDecodeBounds = false;
                options.inSampleSize = calculateSampleSize(options);
                imageBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(bmpUri), new Rect(), options);
                if (imageBitmap != null) {

                    cropImageView.setImageToCrop(imageBitmap);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }//来自MainActivity跳转
        else {
            selectPhoto();
        }

    }

    public Uri handleSendImageUri(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if(imageUri != null) {
            return imageUri;
        }else{
            return null;
        }

    }


    private void initViewData() {
        cropImageView = findViewById(R.id.crop_image_view);
        Button exitBtn = findViewById(R.id.exit_btn);
        Button nextBtn = findViewById(R.id.next_btn);
        Button resetBtn = findViewById(R.id.reset_btn);
        Button rotateBtn = findViewById(R.id.rotate_btn);

        cropImageViewPresenter = new CropImageViewPresenterImpl(this);

        cropImageView.setDragLimit(true);
        exitBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        rotateBtn.setOnClickListener(this);

//        tempFile = new File(getExternalFilesDir("img"), "temp.jpg");
        whichActivity = getIntent().getStringExtra(Constants.WHICH_ACTIVITY);
        isFromAlbum = getIntent().getBooleanExtra(Constants.WHETHER_FROM_ALBUM,true);
        imageType = getIntent().getStringExtra(Constants.WHICH_IMAGE);
        isEditPhoto = getIntent().getBooleanExtra(Constants.WHETHER_EDIT_PHOTO,true);
        topicID = getIntent().getIntExtra(Constants.TOPIC_ID,-1);
        isTopic = getIntent().getBooleanExtra(Constants.IS_TOPIC,true);


        if (!isEditPhoto){
            nextBtn.setText(getString(R.string.finish));
        }
    }

    /**
     * 选择图片来源：相册/相机
     */
    private void selectPhoto() {
        if (isFromAlbum) {
            Intent selectIntent = new Intent(Intent.ACTION_PICK);
            selectIntent.setType("image/*");
            if (selectIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(selectIntent, Constants.REQUEST_CODE_SELECT_ALBUM);
            }
        } else {
            Intent startCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri uri = FilesUtils.getUri(CropImageActivity.this,tempFile);
            startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            if (startCameraIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(startCameraIntent, Constants.REQUEST_CODE_TAKE_PHOTO);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.exit_btn://退出操作
                onBackPressed();
                break;

            case R.id.next_btn://下一步，保存裁剪后的图片
                Bitmap bitmap = cropImageView.crop();
                if(cropImageView.canRightCrop()) {//判断选区是否为凸四边形,bitmap是否为空
                    cropImageViewPresenter.saveCropImage(bitmap);
                }else{
                    OnToastFinished(getString(R.string.crop_failed));
                }
                break;

            case R.id.reset_btn://重置裁剪框
                cropImageView.setFullImgCrop();
                break;

            case R.id.rotate_btn://旋转图片
                cropImageView.setImageToCrop(ImageUtil.rotateBitmap(cropImageView.getBitmap(),-90));
                break;

                default:
                    break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            cropImageViewPresenter.getImageByIntentData(data);
        }

        if (resultCode != RESULT_OK) {
            onBackPressed();
        }

        //获取图片（来自拍照或图），传给imageBitmap
        if (requestCode == Constants.REQUEST_CODE_TAKE_PHOTO && tempFile.exists()) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(tempFile.getPath(), options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateSampleSize(options);
            imageBitmap = BitmapFactory.decodeFile(tempFile.getPath(), options);
            int degree = ImageUtil.getBitmapDegree(tempFile.getAbsolutePath());
            Log.d(TAG,"图片旋转方向："+degree);
            imageBitmap = ImageUtil.rotateBitmap(imageBitmap,degree);

        } else if (requestCode == Constants.REQUEST_CODE_SELECT_ALBUM && data != null && data.getData() != null) {
            ContentResolver contentResolver = getContentResolver();
            Uri bmpUri = data.getData();
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(contentResolver.openInputStream(bmpUri), new Rect(), options);
                options.inJustDecodeBounds = false;
                options.inSampleSize = calculateSampleSize(options);
                imageBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(bmpUri), new Rect(), options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        //将读取后的图片，传给裁剪框架
        if (imageBitmap != null) {
            cropImageView.setImageToCrop(imageBitmap);
        }
    }

    private int calculateSampleSize(BitmapFactory.Options options){
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        int sampleSize = 1;
        int destHeight = 1000;
        int desWidth = 1000;
        if(outHeight > destHeight || outWidth > desWidth){
            if(outHeight > outWidth){
                sampleSize = outHeight / destHeight;
            }else{
                sampleSize = outWidth / desWidth;
            }
        }
        return sampleSize;
    }

    @Override
    public void onCropImageFinished(String cropImagePath) {

        if (!isEditPhoto){
            Intent intent = new Intent();
            intent.putExtra(Constants.IMAGE_PATH, cropImagePath);
            intent.putExtra(Constants.WHICH_IMAGE, imageType);
            setResult(RESULT_OK, intent);
        }else{
            DestroyActivityUtil.addDestroyActivityToMap(CropImageActivity.this,TAG);

            //跳转到EditPhotoActivity页面
            Intent intent = new Intent(CropImageActivity.this, EditPhotoActivity.class);
            intent.putExtra(Constants.WHICH_ACTIVITY, whichActivity);
            intent.putExtra(Constants.IMAGE_PATH,  cropImagePath);
            startActivity(intent);
        }

        finish();
    }

    @Override
    public void OnToastFinished(String showLog) {
        Toast.makeText(this, showLog, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getCropImageViewContext() {
        return this;
    }
}
