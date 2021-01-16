package com.luckyxmobile.correction.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.ui.activity.EditPhotoActivity;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.utils.OpenCvImageUtil;
import com.luckyxmobile.correction.utils.ImageUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import cn.forward.androids.ScaleGestureDetectorApi27;
import cn.forward.androids.TouchGestureDetector;
import es.dmoral.toasty.Toasty;

/**
 * 编辑图片页面
 * @author qjj、
 * @date 2019/08/03
 */
public class DrawingView extends View {

    public final static String TAG = "DoodleView";
    private Context context = null;
    /**触摸手势监听*/
    private TouchGestureDetector mTouchGestureDetector;
    /**记录所有的涂抹*/
    private List<TopicImagesHighlighter.ImageSmear> imageSmearList = new ArrayList<>();
    /**（用于记录撤销的涂抹）*/
    private List<TopicImagesHighlighter.ImageSmear> redoImageSmearList = new ArrayList<>();
    /**当前的涂抹（涂抹类型，宽度，涂抹点）*/
    private TopicImagesHighlighter.ImageSmear imageSmear = new TopicImagesHighlighter.ImageSmear();
    /**当前涂抹类型*/
    private String nowWhichSmear = Constants.PAINT_BLUE;
    /**当前画笔宽度*/
    private int nowBrushWidth = 40;
    /**当前涂抹点*/
    private List<Point> nowPoints = new ArrayList<>();
    /**当前画笔*/
    private Paint nowBrush = new Paint();
    /**画布*/
    private Canvas mCanvas;
    /**背景图, 前景图*/
    private Bitmap mBgBitmap, mFgBitmap;
    /**图片的宽,高，缩放*/
    private int imageWidth, imageHeight;
    private float mBitmapTransX, mBitmapTransY, mBitmapScale = 1;

    public DrawingView(Context context){
        super(context);
        this.setInit(context);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context,attrs);
        this.setInit(context);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setInit(context);
    }

    private void setInit(Context context) {
        this.context = context;
        //初始化触摸事件
        onTouchEvent();
        //初始化笔刷
        setNowBrushWidth(nowBrushWidth);
        setNowWhichSmear(nowWhichSmear);
    }

    /**
     * 设置画笔
     * @param whichSmear 画笔类型
     * @param brushWidth 画笔宽度
     * @return 返回设置号的画笔
     */
    public Paint createBrush(String whichSmear, int brushWidth){

        int color = R.color.red_error;
        int alpha = 150;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStrokeJoin(Paint.Join.BEVEL);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));

        switch (whichSmear) {
            case Constants.PAINT_BLUE:
                color = R.color.blue_right;
                break;
            case Constants.PAINT_RED:
                color = R.color.red_error;
                break;
            case Constants.PAINT_GREEN:
                color = R.color.green_point;
                break;
            case Constants.PAINT_YELLOW:
                color = R.color.yellow_reason;
                break;
            case Constants.PAINT_WHITE_OUT:
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
                color = R.color.colorWhite;
                alpha = 255;
                break;
            case Constants.PAINT_ERASE:
                paint.setXfermode(new  PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
                color = R.color.black;
                alpha = 255;
                break;
            default:
                break;
        }
        paint.setStrokeWidth(brushWidth);
        paint.setColor(getResources().getColor(color,null));
        paint.setAlpha(alpha);

        return paint;
    }

    public void setImageSmearList(List<TopicImagesHighlighter.ImageSmear> imageSmearList){
        this.imageSmearList = imageSmearList;
    }

    public List<TopicImagesHighlighter.ImageSmear> getImageSmearList(){
        return imageSmearList;
    }

    public void setNowBrushWidth(int paintWidth){
        this.nowBrushWidth = paintWidth;
        nowBrush = createBrush(nowWhichSmear, nowBrushWidth);
    }

    public void setNowWhichSmear(String whichPaint){
        this.nowWhichSmear = whichPaint;
        nowBrush = createBrush(nowWhichSmear, nowBrushWidth);
    }

    public Bitmap getImageBitmap(){
        return Bitmap.createBitmap(mBgBitmap,0,0,mBgBitmap.getWidth(),mBgBitmap.getHeight());
    }

    public void setImageBitmap(String contrastRadio, String imagePath){

        this.mBgBitmap = OpenCvImageUtil.setImageContrastRadioByPath(contrastRadio,imagePath);
        this.mFgBitmap = Bitmap.createBitmap(mBgBitmap.getWidth(),mBgBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        imageWidth = mBgBitmap.getWidth();
        imageHeight = mBgBitmap.getHeight();

        mCanvas = new Canvas(mFgBitmap);
        invalidate();
    }

    public List<TopicImagesHighlighter.ImageSmear> getRedoImageSmearList() {
        return redoImageSmearList;
    }

    public void setRedoImageSmearList(List<TopicImagesHighlighter.ImageSmear> redoImageSmearList) {
        this.redoImageSmearList = redoImageSmearList;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        // 画布和图片共用一个坐标系，只需要处理屏幕坐标系到图片（画布）坐标系的映射关系(toX toY)
        canvas.translate(mBitmapTransX, mBitmapTransY);
        canvas.scale(mBitmapScale, mBitmapScale);

        canvas.drawBitmap(mBgBitmap, 0, 0, null);
        canvas.drawBitmap(mFgBitmap, 0, 0, null);

        if (nowWhichSmear.equals(Constants.PAINT_ERASE)){
            Paint paint = nowBrush;
            paint.setAlpha(150);
           canvas.drawPath(ImageUtil.pointsToPath(nowPoints),paint);
        }

        //每次清屏
        mCanvas.save();

        Paint p = new Paint();
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas.drawPaint(p);
        for (int i = 0; i < imageSmearList.size(); i++) {
            TopicImagesHighlighter.ImageSmear imageSmear = imageSmearList.get(i);
            Paint paint = createBrush(imageSmear.getWhichSmear(), imageSmear.getBrushWidth());
            Path path = ImageUtil.pointsToPath(imageSmear.getSmearPoints());
            mCanvas.drawPath(path,paint);
        }

        mCanvas.restore();
        setUndoOrRedo();
    }

    /**
     * 将屏幕触摸坐标x转换成在图片中的坐标
     */
    public final float toX(float touchX) {
        return (touchX - mBitmapTransX) / mBitmapScale;
    }

    /**
     * 将屏幕触摸坐标y转换成在图片中的坐标
     */
    public final float toY(float touchY) {
        return (touchY - mBitmapTransY) / mBitmapScale;
    }

    /**
     * 处理涂抹，缩放
     */
    private void onTouchEvent() {
        // 由手势识别器处理手势
        mTouchGestureDetector = new TouchGestureDetector(getContext(), new TouchGestureDetector.OnTouchGestureListener() {

            // 缩放手势操作相关
            Float mLastFocusX;
            Float mLastFocusY;
            float mTouchCentreX, mTouchCentreY;

            @Override
            public boolean onScaleBegin(ScaleGestureDetectorApi27 detector) {
                mLastFocusX = null;
                mLastFocusY = null;
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetectorApi27 detector) {
            }

            @Override
            public boolean onScale(ScaleGestureDetectorApi27 detector) { // 双指缩放中

                // 屏幕上的焦点
                mTouchCentreX = detector.getFocusX();
                mTouchCentreY = detector.getFocusY();

                if (mLastFocusX != null && mLastFocusY != null) { // 焦点改变
                    float dx = mTouchCentreX - mLastFocusX;
                    float dy = mTouchCentreY - mLastFocusY;
                    // 移动图片
                    mBitmapTransX = mBitmapTransX + dx;
                    mBitmapTransY = mBitmapTransY + dy;
                }

                // 缩放图片
                mBitmapScale = mBitmapScale * detector.getScaleFactor();
                if (mBitmapScale < 0.1f) {
                    mBitmapScale = 0.1f;
                }
                invalidate();

                mLastFocusX = mTouchCentreX;
                mLastFocusY = mTouchCentreY;

                return true;
            }

            @Override
            public void onScrollBegin(MotionEvent e) { // 滑动开始
                float x = toX(e.getX()), y = toY(e.getY());
                Log.d(TAG, "滑动开始-->("+x+":"+y+")");
                if (x > 0 && x < mCanvas.getWidth() && y > 0 && y < mCanvas.getHeight()){

                    nowPoints = new ArrayList<>();
                    nowPoints.add(new Point((int)x,(int)y));
                    imageSmear = new TopicImagesHighlighter.ImageSmear();
                    imageSmear.setWhichSmear(nowWhichSmear);
                    imageSmear.setBrushWidth(nowBrushWidth);
                    imageSmear.setSmearPoints(nowPoints);
                    imageSmearList.add(imageSmear);
                    invalidate(); // 刷新
                }

            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { // 滑动中
                float x = toX(e2.getX()), y = toY(e2.getY());
                if (x < 0 || x > mCanvas.getWidth() || y < 0 || y > mCanvas.getHeight()){
                    return false;
                }else{
                    Log.d(TAG, "滑动中-->("+x+":"+y+")");
                    nowPoints.add(new Point((int)x,(int)y));
                    invalidate(); // 刷新
                    return true;
                }

            }

            @Override
            public void onScrollEnd(MotionEvent e) { // 滑动结束
                float x = toX(e.getX()), y = toY(e.getY());
                if (x > 0 && x < mCanvas.getWidth() && y > 0 && y < mCanvas.getHeight()){
                    Log.d(TAG, "滑动结束-->("+x+":"+y+")");
                    nowPoints.add(new Point((int)x,(int)y));
                    changePoints();//计算涂抹点是否合理
                    if (nowWhichSmear.equals(Constants.PAINT_ERASE)){
                        nowPoints = new ArrayList<>();
                    }
                    invalidate(); // 刷新
                }


            }
        });

        // 下面两行绘画场景下应该设置间距为大于等于1，否则设为0双指缩放后抬起其中一个手指仍然可以移动
        mTouchGestureDetector.setScaleSpanSlop(1); // 手势前识别为缩放手势的双指滑动最小距离值
        mTouchGestureDetector.setScaleMinSpan(1); // 缩放过程中识别为缩放手势的双指最小距离值
        mTouchGestureDetector.setIsLongpressEnabled(false);
        mTouchGestureDetector.setIsScrollAfterScaled(false);

    }

    /**
     * 计算手指滑动的区域，删去过小涂抹
     */
    private void changePoints() {

        List<Float> pointX = new ArrayList<>();
        List<Float> pointY = new ArrayList<>();

        for (Point point: nowPoints){
            pointX.add((float)point.x);
            pointY.add((float)point.y);
        }

        float minX = Collections.min(pointX)- nowBrushWidth /2;
        float minY = Collections.min(pointY)- nowBrushWidth /2;
        float maxX = Collections.max(pointX)+ nowBrushWidth /2;
        float maxY = Collections.max(pointY)+ nowBrushWidth /2;

        minX = (minX < 0)?0:minX;
        minY = (minY < 0)?0:minY;
        maxX = (maxX < 0)?0:maxX;
        maxY = (maxY < 0)?0:maxY;

        minX = (minX > imageWidth)? imageWidth :minX;
        minY = (minY > imageHeight)? imageHeight :minY;
        maxX = (maxX > imageWidth)? imageWidth :maxX;
        maxY = (maxY > imageHeight)? imageHeight :maxY;

        RectF rect = new RectF(minX, minY, maxX, maxY);

        float rectWidth = Math.abs(rect.left - rect.right);
        float rectHeight = Math.abs(rect.top - rect.bottom);

        if (rectWidth * rectHeight < 2000) {
            Log.d(TAG,"涂抹区域太小，删除以上涂抹点");
            Toasty.warning(context, R.string.smear_waring, Toasty.LENGTH_SHORT, true).show();
            imageSmearList.remove(imageSmearList.size()-1);
        }else{
           redoImageSmearList.clear();
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) { //view绘制完成时 大小确定
        super.onSizeChanged(width, height, oldw, oldh);
        int w = mBgBitmap.getWidth();
        int h = mBgBitmap.getHeight();
        float nw = w * 1f / getWidth();
        float nh = h * 1f / getHeight();
        float centerWidth, centerHeight;
        // 1.计算使图片居中的缩放值
        if (nw > nh) {
            mBitmapScale = 1 / nw;
            centerWidth = getWidth();
            centerHeight = (int) (h * mBitmapScale);
        } else {
            mBitmapScale = 1 / nh;
            centerWidth = (int) (w * mBitmapScale);
            centerHeight = getHeight();
        }
        // 2.计算使图片居中的偏移值
        mBitmapTransX = (getWidth() - centerWidth) / 2f;
        mBitmapTransY = (getHeight() - centerHeight) / 2f;
        invalidate();
    }

    @Override
    public boolean dispatchTouchEvent (MotionEvent event){
        // 由手势识别器处理手势
        boolean consumed = mTouchGestureDetector.onTouchEvent(event);
        if (!consumed) {
            return super.dispatchTouchEvent(event);
        }
        return true;
    }

    /**
     * 返回上一步
     */
    public void undo(){

        if (!imageSmearList.isEmpty()){
            int lastPosition = imageSmearList.size()-1;
            if (imageSmearList.get(lastPosition).getWhichSmear().equals(Constants.PAINT_ERASE)){
                Toasty.normal(context,context.getString(R.string.undo)+":"+context.getString(R.string.erase)+"×1").show();
            }
            redoImageSmearList.add(imageSmearList.get(lastPosition));
            imageSmearList.remove(lastPosition);
            invalidate();


        }
    }
    /**
     * 下一步
     */
    public void redo(){

        if (!redoImageSmearList.isEmpty()){
            int lastPosition = redoImageSmearList.size()-1;
            if (redoImageSmearList.get(lastPosition).getWhichSmear().equals(Constants.PAINT_ERASE)){
                Toasty.normal(context,context.getString(R.string.redo)+":"+context.getString(R.string.erase)+"×1").show();
            }
            imageSmearList.add(redoImageSmearList.get(lastPosition));
            redoImageSmearList.remove(lastPosition);
            invalidate();
        }
    }


    private void setUndoOrRedo(){
        if (imageSmearList.isEmpty()){
            EditPhotoActivity.undoBtn.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_paint_undo_no,0,0);
            EditPhotoActivity.undoBtn.setTextColor(context.getColor(R.color.gray_9c));
        }else{
            EditPhotoActivity.undoBtn.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_paint_undo_yes,0,0);
            EditPhotoActivity.undoBtn.setTextColor(context.getColor(R.color.orange_f7));
        }

        if (redoImageSmearList.isEmpty()){
            EditPhotoActivity.redoBtn.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_paint_redo_no,0,0);
            EditPhotoActivity.redoBtn.setTextColor(context.getColor(R.color.gray_9c));
        }else{
            EditPhotoActivity.redoBtn.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_paint_redo_yes,0,0);
            EditPhotoActivity.redoBtn.setTextColor(context.getColor(R.color.orange_f7));
        }
    }

}
