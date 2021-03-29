package com.luckyxmobile.correction.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.model.BeanUtils;
import com.luckyxmobile.correction.model.bean.Highlighter;
import com.luckyxmobile.correction.model.bean.ImageParam;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.utils.BitmapUtils;
import com.luckyxmobile.correction.utils.GsonUtils;
import com.luckyxmobile.correction.utils.HighlighterUtil;
import com.luckyxmobile.correction.utils.ImageTask;
import com.luckyxmobile.correction.utils.OpenCVUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.forward.androids.ScaleGestureDetectorApi27;

/**
 * 编辑图片页面
 * @author qjj、
 * @date 2019/08/03
 */
public class DrawingView extends View implements
        TouchGesture.OnTouchGestureScale, TouchGesture.OnTouchGestureScroll{

    public final static String TAG = "DrawingView";
    private Context context = null;
    private TouchGesture touchGesture;

    private TopicImage topicImage;
    private List<Highlighter> highlighterList;
    private List<Highlighter> redoHighlighterList = new ArrayList<>();

    private final Paint curPaint = new Paint();
    private int curType;
    private int curWidth;
    private List<Point> curPoints;

    private Canvas mCanvas;
    private Bitmap mBgBitmap, mFgBitmap;
    private int imageWidth, imageHeight;
    private float mBitmapTransX, mBitmapTransY, mBitmapScale = 1;

    public DrawingView(Context context){
        super(context);
        onTouchEvent();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context,attrs);
        onTouchEvent();
    }

    public void init(@NonNull TopicImage topicImage) {
        this.topicImage = topicImage;
        String imagePath = topicImage.getPath();

        highlighterList = BeanUtils.findAll(topicImage);

        setCurPaint(Constants.PAINT_BLUE);
        setImageParam(GsonUtils.json2Obj(topicImage.getImageParam(), ImageParam.class));
        setCurPaintWidth(topicImage.getWord_size());
    }

    public List<Highlighter> getHighlighterList() {
        return highlighterList;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 画布和图片共用一个坐标系，只需要处理屏幕坐标系到图片（画布）坐标系的映射关系(toX toY)
        canvas.translate(mBitmapTransX, mBitmapTransY);
        canvas.scale(mBitmapScale, mBitmapScale);

        canvas.drawBitmap(mBgBitmap, 0, 0, null);
        canvas.drawBitmap(mFgBitmap, 0, 0, null);

        //每次清屏
        mCanvas.save();

        mCanvas.drawPaint(HighlighterUtil.clearPaint());

        for (Highlighter highlighter : highlighterList) {
            Paint paint = HighlighterUtil.getHighlighter(getContext(), highlighter, true);
            Path path = HighlighterUtil.pointsToPath(highlighter.getPointList());
            mCanvas.drawPath(path, paint);
        }

        mCanvas.restore();
    }

    public final float toX(float touchX) {
        return (touchX - mBitmapTransX) / mBitmapScale;
    }

    public final float toY(float touchY) {
        return (touchY - mBitmapTransY) / mBitmapScale;
    }

    private void onTouchEvent() {
        touchGesture = new TouchGesture(getContext());
        touchGesture.setScaleListener(this);
        touchGesture.setScrollListener(this);
    }
    /**
     * 计算手指滑动的区域，删去过小涂抹
     */
    private void changePoints() {

        List<Float> pointX = new ArrayList<>();
        List<Float> pointY = new ArrayList<>();

        for (Point point: curPoints){
            pointX.add((float)point.x);
            pointY.add((float)point.y);
        }

        float minX = Collections.min(pointX)- (curWidth >> 1);
        float minY = Collections.min(pointY)- (curWidth >> 1);
        float maxX = Collections.max(pointX)+ (curWidth >> 1);
        float maxY = Collections.max(pointY)+ (curWidth >> 1);

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
            highlighterList.remove(highlighterList.size()-1);
        }else{
           redoHighlighterList.clear();
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
        boolean consumed = touchGesture.onTouchEvent(event);
        if (!consumed) {
            return super.dispatchTouchEvent(event);
        }
        return true;
    }

    public boolean redoAble() {
        return !redoHighlighterList.isEmpty();
    }

    public boolean redo(){

        if (redoAble()){

            Highlighter tmp = redoHighlighterList.get(redoHighlighterList.size()-1);
            highlighterList.add(tmp);
            redoHighlighterList.remove(tmp);
            invalidate();

            return !redoHighlighterList.isEmpty();
        }

        return false;
    }

    public boolean undoAble() {
        return !highlighterList.isEmpty();
    }

    public boolean undo(){

        if (undoAble()){

            Highlighter tmp = highlighterList.get(highlighterList.size()-1);
            redoHighlighterList.add(tmp);
            highlighterList.remove(tmp);
            invalidate();

            return !highlighterList.isEmpty();
        }

        return false;
    }

    public void setCurPaint(int type) {
        curType = type;
        HighlighterUtil.setHighlighter(getContext(), curPaint, type);
    }

    public int getCurType() {
        return curType;
    }

    public void setCurPaintWidth(int width) {
        if (width == -1) {
            width = OpenCVUtil.calculateImageWordSize(mBgBitmap);
            Log.d(TAG, "setCurPaintWidth: " + width);
            topicImage.setWord_size(width);
        }
        curWidth = width;
        curPaint.setStrokeWidth(width);
    }

    public int getCurWidth() {
        return curWidth;
    }

    public void setImageParam(ImageParam param) {
        topicImage.setImageParam(GsonUtils.obj2Json(param));

        ImageTask.getInstance().clearTopicImage(topicImage);
        this.mBgBitmap = BitmapUtils.getBitmap(topicImage);
        this.mFgBitmap = Bitmap.createBitmap(mBgBitmap.getWidth(),mBgBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        imageWidth = mBgBitmap.getWidth();
        imageHeight = mBgBitmap.getHeight();

        mCanvas = new Canvas(mFgBitmap);
        invalidate();
    }

    @Override
    public void onScrollBegin(float x, float y) {
        x = toX(x);
        y = toY(y);

        Log.d(TAG, "滑动开始-->("+x+":"+y+")");
        if (x > 0 && x < mCanvas.getWidth() && y > 0 && y < mCanvas.getHeight()){

            curPoints = new ArrayList<>();
            curPoints.add(new Point((int)x,(int)y));

            Highlighter curHighlighter = new Highlighter(curType);
            curHighlighter.setWidth(curWidth);
            curHighlighter.setPointList(curPoints);

            highlighterList.add(curHighlighter);
            invalidate(); // 刷新
        }
    }

    @Override
    public boolean onScroll(float x, float y) {
        x = toX(x);
        y = toY(y);

        if (x < 0 || x > mCanvas.getWidth() || y < 0 || y > mCanvas.getHeight()){
            return false;
        }else{
            Log.d(TAG, "滑动中-->("+x+":"+y+")");

            if (curPaint != null) {
                curPoints.add(new Point((int)x,(int)y));
            }
            invalidate();
            return true;
        }
    }

    @Override
    public void onScrollEnd(float x, float y) {
        x = toX(x);
        y = toY(y);

        if (x > 0 && x < mCanvas.getWidth() && y > 0 && y < mCanvas.getHeight()){
            Log.d(TAG, "滑动结束-->("+x+":"+y+")");
            if (curPoints != null) {
                curPoints.add(new Point((int)x,(int)y));
            }

            changePoints();//计算涂抹点是否合理

            invalidate(); // 刷新
        }
    }

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
    public boolean onScale(ScaleGestureDetectorApi27 detector) {
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
}
