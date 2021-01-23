package com.luckyxmobile.correction.ui.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.adapter.SelectBookAdapter;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.ui.views.DrawingView;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.utils.DestroyActivityUtil;
import com.luckyxmobile.correction.utils.FastJsonUtil;
import com.luckyxmobile.correction.utils.OpenCVUtils;
import com.luckyxmobile.correction.utils.ImageUtil;
import com.luckyxmobile.correction.utils.impl.FilesUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import es.dmoral.toasty.Toasty;

/**
 * 编辑图片页面
 * @author qjj、
 * @date 2019/08/03
 */
public class EditPhotoActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "EditPhotoActivity";
    /**错题对象*/
    private Topic topic;
    /**临时变量,用于旋转屏幕恢复数据*/
    private static String whichContrastRadioS;
    private static int INITIAL_PAINT_WIDTH_S;
    private static List<TopicImagesHighlighter.ImageSmear> imageSmearLists = new ArrayList<>();
    private static List<TopicImagesHighlighter.ImageSmear> redoImageSmearLists = new ArrayList<>();
    /**用于判断是否旋转*/
    public static boolean isSensor = false;
    /**错题图片（用于记录操作）*/
    private TopicImagesHighlighter topicImagesHighlighter;
    /**判断是否进行触摸涂抹*/
    public static boolean ISTOUCH = false;
    /**自定义涂抹布局*/
    private DrawingView drawingView;
    /**返回按钮*/
    private TextView returnBtn;
    /**undo/redo按钮*/
    @SuppressLint("StaticFieldLeak")
    public static TextView undoBtn, redoBtn;
    /**画板功能按钮>涂改液、橡皮擦、对比度*/
    private TextView whiteOutBtn, eraseBtn, contrastRadioBtn;
    /**选择画笔按钮>正解、错解、考点、错误原因笔刷*/
    private Button brushRight, brushError, brushPoint, brushErrorReason;
    private int positionImage;
    /**图片路径地址*/
    private String imagePath;
    private TextView whichToolLayoutText;
    /**画笔宽度布局*/
    private HorizontalScrollView brushWidthLayout;
    private final String PAINT_WIDTH_LAYOUT = "PAINT_WIDTH_LAYOUT";
    private Button widthThinBtn, widthMediumBtn, widthThickBtn, initialWidthBtn;
    private int nowPaintWidth = Constants.PAINT_THIN;
    private int INITIAL_PAINT_WIDTH = Constants.PAINT_THIN;
    /**橡皮擦布局*/
    private HorizontalScrollView eraseLayout;
    private final String ERASE_WIDTH_LAYOUT = "ERASE_WIDTH_LAYOUT";
    private Button eraseThinBtn, eraseMediumBtn, eraseThickBtn;
    private int nowEraseWidth = Constants.ERASE_MEDIUM;
    /**对比度布局*/
    private HorizontalScrollView contrastRatioLayout;
    private final String CONTRAST_RATION_LAYOUT = "CONTRAST_RADIO_LAYOUT";
    private String whichContrastRadio = Constants.CONTRAST_RADIO_COMMON;
    private Button contrastWeakBtn, contrastCommonBtn, contrastStrongBtn;
    /**选择错题本*/
    private List<Book> bookList;
    private SelectBookAdapter adapter;
    private BottomSheetDialog selectBookDialog = null;
    private String whichActivity;
    /**判断是否是新创建的涂抹*/
    private boolean isNewTopicPaints = false;
    private CheckBox srceenRotationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 将activity设置为全屏显示（必须放在setContentView()前）
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_photo);

        //初始化布局
        initView();

        //初始化页面数据
        initViewDate();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!isSensor){
            isSensor = true;
            whichContrastRadioS = whichContrastRadio;
            INITIAL_PAINT_WIDTH_S = INITIAL_PAINT_WIDTH;
            imageSmearLists = drawingView.getImageSmearList();
            redoImageSmearLists = drawingView.getRedoImageSmearList();

            Log.i(TAG, "临时数据"+whichContrastRadioS+";"+INITIAL_PAINT_WIDTH_S+";"+ imageSmearLists.size()+";"+ redoImageSmearLists.size());
        }

    }

    /**
     * 初始化布局，并设置点击事件
     * 初始化自定义view，并传入参数
     */
    private void initView() {
        drawingView = findViewById(R.id.doodle_view_photo);
        returnBtn = findViewById(R.id.edit_photo_return_btn);
        ImageButton finishBtn = findViewById(R.id.doodle_btn_next);
        undoBtn = findViewById(R.id.edit_photo_undo_btn);
        redoBtn = findViewById(R.id.edit_photo_redo_btn);
        eraseBtn = findViewById(R.id.edit_photo_erase);
        contrastRadioBtn = findViewById(R.id.edit_photo_contrast_ratio_btn);
        whiteOutBtn = findViewById(R.id.edit_photo_white_out);
        brushRight = findViewById(R.id.doodle_paint_right);
        brushError = findViewById(R.id.doodle_paint_error);
        brushPoint = findViewById(R.id.doodle_paint_point);
        brushErrorReason = findViewById(R.id.doodle_paint_error_reason);
        brushWidthLayout = findViewById(R.id.paint_width_layout);
        initialWidthBtn = findViewById(R.id.doodle_paint_initial_width);
        widthThinBtn = findViewById(R.id.doodle_paint_width_thin);
        widthMediumBtn = findViewById(R.id.doodle_paint_width_medium);
        widthThickBtn = findViewById(R.id.doodle_paint_width_thick);
        contrastRatioLayout = findViewById(R.id.contrast_ratio_layout);
        contrastWeakBtn = findViewById(R.id.contrast_weak_ratio_btn);
        contrastCommonBtn = findViewById(R.id.contrast_common_ratio_btn);
        contrastStrongBtn = findViewById(R.id.contrast_strong_ratio_btn);
        eraseLayout = findViewById(R.id.erase_layout);
        eraseThinBtn = findViewById(R.id.doodle_erase_width_thin);
        eraseMediumBtn = findViewById(R.id.doodle_erase_width_medium);
        eraseThickBtn = findViewById(R.id.doodle_erase_width_thick);
        whichToolLayoutText = findViewById(R.id.which_tools_text);
        srceenRotationBtn = findViewById(R.id.screen_rotation);

        undoBtn.setOnClickListener(this);
        redoBtn.setOnClickListener(this);
        eraseBtn.setOnClickListener(this);
        contrastRadioBtn.setOnClickListener(this);
        whiteOutBtn.setOnClickListener(this);
        brushRight.setOnClickListener(this);
        brushError.setOnClickListener(this);
        brushPoint.setOnClickListener(this);
        brushErrorReason.setOnClickListener(this);
        returnBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        widthThinBtn.setOnClickListener(this);
        widthMediumBtn.setOnClickListener(this);
        widthThickBtn.setOnClickListener(this);
        initialWidthBtn.setOnClickListener(this);
        contrastWeakBtn.setOnClickListener(this);
        contrastCommonBtn.setOnClickListener(this);
        contrastStrongBtn.setOnClickListener(this);
        eraseThinBtn.setOnClickListener(this);
        eraseMediumBtn.setOnClickListener(this);
        eraseThickBtn.setOnClickListener(this);
        srceenRotationBtn.setOnClickListener(this);
    }

    /**
     * 获取错题topic对象
     * 获取题干图片(待编辑图片)路径
     * 根据路径转换成bitmap格式
     */
    private void initViewDate() {

        DestroyActivityUtil.addDestroyActivityToMap(EditPhotoActivity.this,TAG);
        positionImage = getIntent().getIntExtra(Constants.IMAGE_POSITION,0);
        whichActivity = getIntent().getStringExtra(Constants.WHICH_ACTIVITY);
        topic = LitePal.find(Topic.class,getIntent().getIntExtra(Constants.TOPIC_ID,0));
        topicImagesHighlighter = FastJsonUtil.jsonToObject(topic.getTopic_original_picture(), TopicImagesHighlighter.class);
        assert topicImagesHighlighter != null;
        //获取指定图片路径
        imagePath = topicImagesHighlighter.getPrimitiveImagePathList().get(positionImage);
        Log.d(TAG, "primitive--imagePath: "+imagePath);

        if (!whichActivity.equals(MainActivity.TAG)){
            returnBtn.setText(getString(R.string.exit));
        }

        //判断是否是新的涂抹
        if (topic.getBook_id() == 0 || topicImagesHighlighter.getPrimitiveImagesPathSize() > topicImagesHighlighter.getImageSmearsList().size()){
            isNewTopicPaints = true;
        }else{
            isNewTopicPaints = false;
        }

        //判断是否旋转
        if (isSensor){
            Log.i(TAG, "旋转:true");
            isSensor = false;
            INITIAL_PAINT_WIDTH = INITIAL_PAINT_WIDTH_S;
            whichContrastRadio = whichContrastRadioS;
            drawingView.setImageSmearList(imageSmearLists);
            drawingView.setRedoImageSmearList(redoImageSmearLists);
        }else{
            Log.i(TAG, "旋转:false");
            if (isNewTopicPaints){
                drawingView.setImageBitmap(whichContrastRadio,imagePath);
                INITIAL_PAINT_WIDTH = OpenCVUtils.calculateImageWordSize(drawingView.getImageBitmap());
                drawingView.setImageSmearList(new ArrayList<>());
            }else {
                INITIAL_PAINT_WIDTH = topicImagesHighlighter.getImageWordSizeList().get(positionImage);
                whichContrastRadio = topicImagesHighlighter.getImageContrastRadioList().get(positionImage);
                drawingView.setImageSmearList(topicImagesHighlighter.getImageSmearsList().get(positionImage));
            }
        }

        setWhichBrush(Constants.PAINT_BLUE,true);
        setContrastRadio(whichContrastRadio,true);
        setBrushWidth(INITIAL_PAINT_WIDTH,true);

        nowPaintWidth = INITIAL_PAINT_WIDTH;
        initialWidthBtn.setText(String.valueOf(INITIAL_PAINT_WIDTH));

        int widthInitialBtn = (INITIAL_PAINT_WIDTH + 80)/4;
        LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) initialWidthBtn.getLayoutParams();
        linearParams.width = ImageUtil.dip2px(this,(float)widthInitialBtn);
        linearParams.height = ImageUtil.dip2px(this,(float)widthInitialBtn);
        initialWidthBtn.setLayoutParams(linearParams); //使设置好的布局参数应用到控件

    }

    @Override
    protected void onResume() {
        super.onResume();

        //加载所有错题本（删去收藏）
        bookList = LitePal.findAll(Book.class);
        bookList.remove(0);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ISTOUCH = false;
        isSensor = false;
        whichContrastRadioS = "";
        INITIAL_PAINT_WIDTH_S = 0;
        imageSmearLists = null;
        redoImageSmearLists = null;

        //此活动返回时 将错题是否是从收藏夹添加的 标记为false
        SharedPreferences sp = getSharedPreferences(Constants.TABLE_SHARED_CORRECTION, MODE_PRIVATE);
        Editor editor =sp.edit();
        editor.putBoolean(Constants.TABLE_FROM_FAVORITE, false);
        editor.apply();

        switch (whichActivity){
            case MainActivity.TAG:
                FilesUtils.cascadeDeleteTopic(topic.getId(),this);
                CorrectionLab.deleteTopic(topic.getId());
                DestroyActivityUtil.destroyActivity(TAG);
                break;

            case TopicInfoActivity.TAG:
                FilesUtils.deleteFile(imagePath,this);
                topicImagesHighlighter.getPrimitiveImagePathList().remove(positionImage);
                topic.setTopic_original_picture(FastJsonUtil.objectToJson(topicImagesHighlighter));
                topic.save();
                DestroyActivityUtil.destroyActivityALL();
                break;
                default:
                    break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //返回操作
            case R.id.edit_photo_return_btn:
                onBackPressed();
                break;
            //撤销操作
            case R.id.edit_photo_undo_btn:
                drawingView.undo();
                break;
            //恢复操作
            case R.id.edit_photo_redo_btn:
                drawingView.redo();
                break;
            //对比度
            case R.id.edit_photo_contrast_ratio_btn:
                changeLayout(CONTRAST_RATION_LAYOUT,true);
                break;
            //切换 橡皮擦
            case R.id.edit_photo_erase:
                changeLayout(ERASE_WIDTH_LAYOUT,true);
                break;
            //切换 涂改液
            case R.id.edit_photo_white_out:
                setWhichBrush(Constants.PAINT_WHITE_OUT,true);
                break;
            //切换 正解 笔刷
            case R.id.doodle_paint_right:
                setWhichBrush(Constants.PAINT_BLUE,true);
                break;
            //切换 错解 笔刷
            case R.id.doodle_paint_error:
                setWhichBrush(Constants.PAINT_RED,true);
                break;
            //切换 考点 笔刷
            case R.id.doodle_paint_point:
                setWhichBrush(Constants.PAINT_GREEN,true);
                break;
            //切换 错误原因 笔刷
            case R.id.doodle_paint_error_reason:
                setWhichBrush(Constants.PAINT_YELLOW,true);
                break;
            //涂抹完成-下一步
            case R.id.doodle_btn_next:
                if (whichActivity.equals(MainActivity.TAG)){
                    //如果是新题，选择错题本
                    selectBook();
                }else{
                    finishNext();
                    //销毁活动
                    DestroyActivityUtil.destroyActivityALL();
                }

                break;
            //画笔宽度 细
            case R.id.doodle_paint_width_thin:
                setBrushWidth(Constants.PAINT_THIN,true);
                break;
            //画笔宽度 中
            case R.id.doodle_paint_width_medium:
                setBrushWidth(Constants.PAINT_MEDIUM,true);
                break;
            //画笔宽度 粗
            case R.id.doodle_paint_width_thick:
                setBrushWidth(Constants.PAINT_THICK,true);
                break;
            //画笔宽度 初始设置
            case R.id.doodle_paint_initial_width:
                setBrushWidth(INITIAL_PAINT_WIDTH,true);
                break;
            //橡皮擦宽度 细
            case R.id.doodle_erase_width_thin:
                setEraseWidth(Constants.ERASE_THIN,true);
                break;
            //橡皮擦宽度 中
            case R.id.doodle_erase_width_medium:
                setEraseWidth(Constants.ERASE_MEDIUM,true);
                break;
            //橡皮擦宽度 粗
            case R.id.doodle_erase_width_thick:
                setEraseWidth(Constants.ERASE_THICK,true);
                break;
            //对比度 弱
            case R.id.contrast_weak_ratio_btn:
                setContrastRadio(Constants.CONTRAST_RADIO_WEAK,true);
                break;
            //对比度 一般
            case R.id.contrast_common_ratio_btn:
                setContrastRadio(Constants.CONTRAST_RADIO_COMMON,true);
                break;
            //对比度 强
            case R.id.contrast_strong_ratio_btn:
                setContrastRadio(Constants.CONTRAST_RADIO_STRONG,true);
                break;
            case R.id.screen_rotation:
                if (srceenRotationBtn.isChecked()){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                    srceenRotationBtn.setBackground(getResources().getDrawable(R.drawable.ic_screen_rotation_24dp,null));
                }else{
                    srceenRotationBtn.setBackground(getResources().getDrawable(R.drawable.ic_screen_lock_rotation_24dp,null));
                    switch (this.getResources().getConfiguration().orientation){
                        case Configuration.ORIENTATION_PORTRAIT:
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            break;

                        case Configuration.ORIENTATION_LANDSCAPE:
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            break;
                            default:
                                break;
                    }
                }
                break;

                default:
                    break;
        }
    }

    /**
     * 处理按钮对应的布局
     * @param whichLayout 布局参数
     * @param isVisible 是否显示可见
     */
    private void changeLayout(String whichLayout,boolean isVisible){

        switch (whichLayout){
            case ERASE_WIDTH_LAYOUT:
                if (isVisible){
                    whichToolLayoutText.setText(getString(R.string.width));
                    Log.v(TAG,"setWhichPaint-->"+ Constants.PAINT_ERASE);
                    drawingView.setNowBrushWidth(nowEraseWidth);
                    drawingView.setNowWhichSmear(Constants.PAINT_ERASE);
                    eraseLayout.setVisibility(View.VISIBLE);
                    eraseBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paint_erase_checked,0,0);
                    eraseBtn.setTextColor(getColor(R.color.orange_f7));

                    changeLayout(PAINT_WIDTH_LAYOUT,false);
                    changeLayout(CONTRAST_RATION_LAYOUT,false);

                    setWhichBrush(Constants.PAINT_BLUE,false);
                    setWhichBrush(Constants.PAINT_RED,false);
                    setWhichBrush(Constants.PAINT_GREEN,false);
                    setWhichBrush(Constants.PAINT_YELLOW,false);
                    setWhichBrush(Constants.PAINT_WHITE_OUT,false);

                }else {
                    eraseLayout.setVisibility(View.GONE);
                    eraseBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paint_erase_unchecked,0,0);
                    eraseBtn.setTextColor(getColor(R.color.gray_9c));
                }
                break;

            case PAINT_WIDTH_LAYOUT:
                if (isVisible){
                    whichToolLayoutText.setText(getString(R.string.width));
                    brushWidthLayout.setVisibility(View.VISIBLE);
                    changeLayout(ERASE_WIDTH_LAYOUT,false);
                    changeLayout(CONTRAST_RATION_LAYOUT,false);
                }else{
                    brushWidthLayout.setVisibility(View.GONE);
                }
                break;

            case CONTRAST_RATION_LAYOUT:
                if (isVisible){
                    whichToolLayoutText.setText(getString(R.string.contrast_ratio));
                    contrastRatioLayout.setVisibility(View.VISIBLE);
                    contrastRadioBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paint_contrast_ratio_checked,0,0);
                    contrastRadioBtn.setTextColor(getColor(R.color.orange_f7));

                    changeLayout(ERASE_WIDTH_LAYOUT,false);
                    changeLayout(PAINT_WIDTH_LAYOUT,false);

                    setWhichBrush(Constants.PAINT_BLUE,false);
                    setWhichBrush(Constants.PAINT_RED,false);
                    setWhichBrush(Constants.PAINT_GREEN,false);
                    setWhichBrush(Constants.PAINT_YELLOW,false);
                    setWhichBrush(Constants.PAINT_WHITE_OUT,false);
                }else{
                    contrastRatioLayout.setVisibility(View.GONE);
                    contrastRadioBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paint_contrast_ratio_unchecked,0,0);
                    contrastRadioBtn.setTextColor(getColor(R.color.gray_9c));
                }
                break;

            default:
                break;
        }
    }

    /**
     * 处理画笔按钮
     * @param whichPaint 画笔参数
     * @param isSelect 是否选中
     */
    private void setWhichBrush(String whichPaint, boolean isSelect){

        if (isSelect){
            drawingView.setNowBrushWidth(nowPaintWidth);
            drawingView.setNowWhichSmear(whichPaint);
            changeLayout(PAINT_WIDTH_LAYOUT,true);
            Log.v(TAG,"setWhichPaint-->"+whichPaint);
        }

        switch (whichPaint) {
            case Constants.PAINT_BLUE:
                if (isSelect){
                    setWhichBrush(Constants.PAINT_RED,false);
                    setWhichBrush(Constants.PAINT_GREEN,false);
                    setWhichBrush(Constants.PAINT_YELLOW,false);
                    setWhichBrush(Constants.PAINT_WHITE_OUT,false);
                    brushRight.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paint_right,0,0);
                    brushRight.setTextColor(getColor(R.color.blue_right));
                }else{
                    brushRight.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paint_unchecked,0,0);
                    brushRight.setTextColor(getColor(R.color.gray_9c));
                }
                break;
            case Constants.PAINT_RED:
                if (isSelect){
                    setWhichBrush(Constants.PAINT_BLUE,false);
                    setWhichBrush(Constants.PAINT_GREEN,false);
                    setWhichBrush(Constants.PAINT_YELLOW,false);
                    setWhichBrush(Constants.PAINT_WHITE_OUT,false);
                    brushError.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paint_error,0,0);
                    brushError.setTextColor(getColor(R.color.red_error));
                }else{
                    brushError.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paint_unchecked,0,0);
                    brushError.setTextColor(getColor(R.color.gray_9c));
                }
                break;
            case Constants.PAINT_GREEN:
                if (isSelect){
                    setWhichBrush(Constants.PAINT_BLUE,false);
                    setWhichBrush(Constants.PAINT_RED,false);
                    setWhichBrush(Constants.PAINT_YELLOW,false);
                    setWhichBrush(Constants.PAINT_WHITE_OUT,false);
                    brushPoint.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paint_point,0,0);
                    brushPoint.setTextColor(getColor(R.color.green_point));
                }else{
                    brushPoint.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paint_unchecked,0,0);
                    brushPoint.setTextColor(getColor(R.color.gray_9c));
                }
                break;
            case Constants.PAINT_YELLOW:
                if (isSelect){
                    setWhichBrush(Constants.PAINT_BLUE,false);
                    setWhichBrush(Constants.PAINT_RED,false);
                    setWhichBrush(Constants.PAINT_GREEN,false);
                    setWhichBrush(Constants.PAINT_WHITE_OUT,false);
                    brushErrorReason.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paint_error_reason,0,0);
                    brushErrorReason.setTextColor(getColor(R.color.yellow_reason));
                }else{
                    brushErrorReason.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paint_unchecked,0,0);
                    brushErrorReason.setTextColor(getColor(R.color.gray_9c));
                }
                break;
            case Constants.PAINT_WHITE_OUT:
                if (isSelect){
                    setWhichBrush(Constants.PAINT_BLUE,false);
                    setWhichBrush(Constants.PAINT_RED,false);
                    setWhichBrush(Constants.PAINT_GREEN,false);
                    setWhichBrush(Constants.PAINT_YELLOW,false);
                    whiteOutBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paint_white_out_check,0,0);
                    whiteOutBtn.setTextColor(getColor(R.color.orange_f7));
                }else{
                    whiteOutBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paint_white_out_uncheck,0,0);
                    whiteOutBtn.setTextColor(getColor(R.color.gray_9c));
                }
                break;

        }
    }

    /**
     * 处理画笔宽度
     * @param whichPrintWidth 画笔宽度参数
     * @param isSelect 是否选中
     */
    private void setBrushWidth(int whichPrintWidth, boolean isSelect){

        if (isSelect){
            nowPaintWidth = whichPrintWidth;
            drawingView.setNowBrushWidth(nowPaintWidth);
            Log.v(TAG,"setPaintWidth-->"+whichPrintWidth);
        }

        switch (whichPrintWidth){
            case Constants.PAINT_THIN:
                if (isSelect){
                    widthThinBtn.setBackground(getDrawable(R.drawable.ic_paint_width_check_24dp));
                    widthThinBtn.setTextColor(getColor(R.color.orange_f7));
                    setBrushWidth(Constants.PAINT_MEDIUM,false);
                    setBrushWidth(Constants.PAINT_THICK,false);
                    setBrushWidth(INITIAL_PAINT_WIDTH,false);
                }else{
                    widthThinBtn.setBackground(getDrawable(R.drawable.ic_paint_width_uncheck_24dp));
                    widthThinBtn.setTextColor(getColor(R.color.gray_9c));
                }

                break;
            case Constants.PAINT_MEDIUM:
                if (isSelect){
                    widthMediumBtn.setBackground(getDrawable(R.drawable.ic_paint_width_check_24dp));
                    widthMediumBtn.setTextColor(getColor(R.color.orange_f7));
                    setBrushWidth(Constants.PAINT_THIN,false);
                    setBrushWidth(Constants.PAINT_THICK,false);
                    setBrushWidth(INITIAL_PAINT_WIDTH,false);
                }else{
                    widthMediumBtn.setBackground(getDrawable(R.drawable.ic_paint_width_uncheck_24dp));
                    widthMediumBtn.setTextColor(getColor(R.color.gray_9c));
                }
                break;
            case Constants.PAINT_THICK:
                if (isSelect){
                    widthThickBtn.setBackground(getDrawable(R.drawable.ic_paint_width_check_24dp));
                    widthThickBtn.setTextColor(getColor(R.color.orange_f7));
                    setBrushWidth(Constants.PAINT_THIN,false);
                    setBrushWidth(Constants.PAINT_MEDIUM,false);
                    setBrushWidth(INITIAL_PAINT_WIDTH,false);
                }else{
                    widthThickBtn.setBackground(getDrawable(R.drawable.ic_paint_width_uncheck_24dp));
                    widthThickBtn.setTextColor(getColor(R.color.gray_9c));
                }
                break;

                default:
                    if (isSelect){
                        initialWidthBtn.setBackground(getDrawable(R.drawable.ic_paint_width_check_24dp));
                        initialWidthBtn.setTextColor(getColor(R.color.red_ff));
                        setBrushWidth(Constants.PAINT_THIN,false);
                        setBrushWidth(Constants.PAINT_MEDIUM,false);
                        setBrushWidth(Constants.PAINT_THICK,false);
                    }else{
                        initialWidthBtn.setBackground(getDrawable(R.drawable.ic_paint_width_uncheck_24dp));
                        initialWidthBtn.setTextColor(getColor(R.color.gray_9c));
                    }
                    break;
        }
    }

    /**
     * 处理橡皮擦宽度
     * @param whichEraseWidth 橡皮擦宽度参数
     * @param isSelect 是否选中
     */
    private void setEraseWidth(int whichEraseWidth, boolean isSelect){

        if (isSelect){
            nowEraseWidth =  whichEraseWidth;
            drawingView.setNowBrushWidth(nowEraseWidth);
            Log.v(TAG,"setEraseWidth-->"+whichEraseWidth);
        }

        switch (whichEraseWidth){
            case Constants.ERASE_THIN:
                if (isSelect){
                    eraseThinBtn.setBackground(getDrawable(R.drawable.ic_paint_width_check_24dp));
                    setEraseWidth(Constants.ERASE_MEDIUM,false);
                    setEraseWidth(Constants.ERASE_THICK,false);
                }else{
                    eraseThinBtn.setBackground(getDrawable(R.drawable.ic_paint_width_uncheck_24dp));
                }

                break;
            case Constants.ERASE_MEDIUM:
                if (isSelect){
                    eraseMediumBtn.setBackground(getDrawable(R.drawable.ic_paint_width_check_24dp));
                    setEraseWidth(Constants.ERASE_THIN,false);
                    setEraseWidth(Constants.ERASE_THICK,false);
                }else{
                    eraseMediumBtn.setBackground(getDrawable(R.drawable.ic_paint_width_uncheck_24dp));
                }
                break;
            case Constants.ERASE_THICK:
                if (isSelect){
                    eraseThickBtn.setBackground(getDrawable(R.drawable.ic_paint_width_check_24dp));
                    setEraseWidth(Constants.ERASE_THIN,false);
                    setEraseWidth(Constants.ERASE_MEDIUM,false);
                }else{
                    eraseThickBtn.setBackground(getDrawable(R.drawable.ic_paint_width_uncheck_24dp));
                }
                break;
            default:
                break;
        }
    }

    /**
     * 处理对比度
     * @param whichContrastRadio 对比度参数
     * @param isSelect 是否选中
     */
    private void setContrastRadio(String whichContrastRadio, boolean isSelect){

        if (isSelect){
            this.whichContrastRadio = whichContrastRadio;
            drawingView.setImageBitmap(whichContrastRadio, imagePath);
            Log.v(TAG,"setContrastRadio-->"+whichContrastRadio);
        }

        switch (whichContrastRadio){
            case Constants.CONTRAST_RADIO_WEAK:
                if (isSelect){
                    contrastWeakBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paint_contrast_weak_ratio_checked,0,0);
                    contrastWeakBtn.setTextColor(getColor(R.color.orange_f7));
                    setContrastRadio(Constants.CONTRAST_RADIO_COMMON,false);
                    setContrastRadio(Constants.CONTRAST_RADIO_STRONG,false);
                }else{
                    contrastWeakBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paint_contrast_weak_ratio_unchecked,0,0);
                    contrastWeakBtn.setTextColor(getColor(R.color.gray_9c));
                }
                break;

            case Constants.CONTRAST_RADIO_COMMON:
                if (isSelect){
                    contrastCommonBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paint_contrast_common_ratio_checked,0,0);
                    contrastCommonBtn.setTextColor(getColor(R.color.orange_f7));
                    setContrastRadio(Constants.CONTRAST_RADIO_WEAK,false);
                    setContrastRadio(Constants.CONTRAST_RADIO_STRONG,false);
                }else{
                    contrastCommonBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paint_contrast_common_ratio_unchecked,0,0);
                    contrastCommonBtn.setTextColor(getColor(R.color.gray_9c));
                }
                break;

            case Constants.CONTRAST_RADIO_STRONG:
                if (isSelect){
                    contrastStrongBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paint_contrast_strong_ratio_checked,0,0);
                    contrastStrongBtn.setTextColor(getColor(R.color.orange_f7));
                    setContrastRadio(Constants.CONTRAST_RADIO_WEAK,false);
                    setContrastRadio(Constants.CONTRAST_RADIO_COMMON,false);
                }else{
                    contrastStrongBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_paint_contrast_strong_ratio_unchecked,0,0);
                    contrastStrongBtn.setTextColor(getColor(R.color.gray_9c));
                }
                break;

            default:
                break;
        }
    }


    /**
     * 选择错题本
     */
    private void selectBook() {
        selectBookDialog = new BottomSheetDialog(EditPhotoActivity.this);
        View view = LayoutInflater.from(EditPhotoActivity.this).inflate(R.layout.select_book_dialog,null);
        TextView createBook = view.findViewById(R.id.create_book);
        TextView finish = view.findViewById(R.id.select_book_finish);

        int mRows = 2;
        int mClomns = 4;

        RecyclerView recyclerView;
//        PagerGridLayoutManager mLayoutManager;

//        mLayoutManager = new PagerGridLayoutManager(mRows,mClomns,PagerGridLayoutManager.HORIZONTAL);

        recyclerView = view.findViewById(R.id.recycler_view);

        //设置页面变化监听器
//        mLayoutManager.setPageListener(this);
//        recyclerView.setLayoutManager(mLayoutManager);

        //设置滚动辅助工具
//        PagerGridSnapHelper pagerGridSnapHelper = new PagerGridSnapHelper();
//        pagerGridSnapHelper.attachToRecyclerView(recyclerView);

        adapter = new SelectBookAdapter(bookList);

        recyclerView.setAdapter(adapter);
        initAdapterSelectItem(adapter);

        //recyclerView
        adapter.setOnItemClickListener(new SelectBookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                adapter.selectItem = position;
                adapter.notifyDataSetChanged();
            }
        });
        //新建错题本
        createBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookDialog();
            }
        });

        //完成后 数据库添加  页面跳转
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookList.size()>0) {

                    topic.setBook_id(bookList.get(adapter.selectItem).getId());

                    //判断是否是从收藏夹中添加的错题 若是则标记为收藏
                    SharedPreferences sp = getSharedPreferences(Constants.TABLE_SHARED_CORRECTION, MODE_PRIVATE);
                    Boolean fromFavorite = sp.getBoolean(Constants.TABLE_FROM_FAVORITE, false);
                    if(fromFavorite){
                        topic.setTopic_collection(1);
                        Editor editor = sp.edit();
                        editor.putBoolean(Constants.TABLE_FROM_FAVORITE, false);
                        editor.apply();
                        //Toast.makeText(EditPhotoActivity.this, String.valueOf(topic.getTopic_collection()), Toast.LENGTH_LONG).show();
                    }

                    finishNext();

                    Intent intent = new Intent(EditPhotoActivity.this, BookDetailActivity.class);
                    intent.putExtra(Constants.WHICH_ACTIVITY,whichActivity);
                    intent.putExtra(Constants.BOOK_ID,topic.getBook_id());
                    startActivity(intent);

                    //销毁活动
                    DestroyActivityUtil.destroyActivityALL();
                    Toasty.success(EditPhotoActivity.this, R.string.add_successful, Toast.LENGTH_SHORT, true).show();
                }
            }
        });

        selectBookDialog.setContentView(view);
        setLp(view);
        selectBookDialog.show();

    }

    private void finishNext(){
        ISTOUCH = false;
        //如果不是新创建的涂抹，需要更新最后一个（即先删除最后一个，在添加）
        if (!isNewTopicPaints){
            topicImagesHighlighter.getImageSmearsList().remove(positionImage);
            topicImagesHighlighter.getImageWordSizeList().remove(positionImage);
            topicImagesHighlighter.getImageContrastRadioList().remove(positionImage);
        }

        topicImagesHighlighter.getImageWordSizeList().add(positionImage,INITIAL_PAINT_WIDTH);
        topicImagesHighlighter.getImageContrastRadioList().add(positionImage,whichContrastRadio);
        topicImagesHighlighter.getImageSmearsList().add(positionImage,drawingView.getImageSmearList());

        String json = FastJsonUtil.objectToJson(topicImagesHighlighter);
        topic.setTopic_original_picture(json);
        topic.save();

        isSensor = false;
        whichContrastRadioS = "";
        INITIAL_PAINT_WIDTH_S = 0;
        imageSmearLists = null;
        redoImageSmearLists = null;
    }

    //bottomsheet 横屏下正常显示
    public void setLp(View view) {
        View parent = (View)view.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        view.measure(0,0);
        Log.d(TAG, "setLp: "+behavior.getPeekHeight()+"    "+view.getMeasuredHeight());
        behavior.setPeekHeight(view.getMeasuredHeight());
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        parent.setLayoutParams(lp);
    }

    /**
     * 初始化错题本的选中状态
     * @param adapter
     * @author zc
     */
    private void initAdapterSelectItem(SelectBookAdapter adapter) {
        SharedPreferences sp = getSharedPreferences(Constants.TABLE_SHARED_CORRECTION, MODE_PRIVATE);
        int book_id = sp.getInt(Constants.TABLE_FROM_BOOK_ID,0);
        //读完之后重置bookid的数据，防止造成干扰
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.BOOK_ID, 0);
        editor.apply();

        adapter.selectItem = adapter.transferBookIdToIndex(book_id);
        adapter.notifyDataSetChanged();
    }

    /**
     * 添加错题本
     */

    @SuppressLint("SetTextI18n")
    private void addBookDialog() {
        @SuppressLint("InflateParams")
         View view =  LayoutInflater.from(EditPhotoActivity.this).inflate(R.layout.dialog_add_book,null);
        final EditText bookNameEdt = view.findViewById(R.id.bookNameEdt);
        final TextView bookNameNum = view.findViewById(R.id.bookNameEdtNum);

        //输入框字数提示和限制
        bookNameEdt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                bookNameNum.setText(s.length()+"/10");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                bookNameNum.setText(s.length()+"/10");
            }
        });

        AlertDialog.Builder mChangeBookDialog = new AlertDialog.Builder(EditPhotoActivity.this);

        mChangeBookDialog.setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String bookName = bookNameEdt.getText().toString();
                //保存创建
                if (bookName.length() <= 0) {

                    Toasty.warning(EditPhotoActivity.this,R.string.empty_input, Toast.LENGTH_SHORT, true).show();
                } else {
                    Book newBook = new Book();
                    newBook.setBook_name(bookNameEdt.getText().toString());
                    newBook.setBook_cover(ImageUtil.getResultPath());
                    //插入到litepal数据库
                    newBook.save();
                    bookList.add(newBook);
                    adapter.notifyDataSetChanged();
                    Toasty.success(EditPhotoActivity.this, R.string.add_successful, Toast.LENGTH_SHORT, true).show();

                }
            }
        }).setNegativeButton(R.string.cancel,null);

        mChangeBookDialog.setView(view);
        mChangeBookDialog.create();
        mChangeBookDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (selectBookDialog !=null) {
            selectBookDialog.dismiss();
        }
    }
}
