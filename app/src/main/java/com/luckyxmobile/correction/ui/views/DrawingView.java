package com.luckyxmobile.correction.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.model.BeanUtils;
import com.luckyxmobile.correction.model.bean.Highlighter;
import com.luckyxmobile.correction.model.bean.ImageParam;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.utils.BitmapUtils;
import com.luckyxmobile.correction.utils.FilesUtils;
import com.luckyxmobile.correction.utils.GsonUtils;
import com.luckyxmobile.correction.utils.PaintUtil;
import com.luckyxmobile.correction.utils.ImageTask;
import com.luckyxmobile.correction.utils.OpenCVUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
    private TouchGesture touchGesture;

    private OnScrollListener scrollListener;

    private TopicImage topicImage;
    private List<Highlighter> highlighterList;
    private List<Highlighter> undoList;

    private int curType;
    private int curWidth;
    private List<Point> curPoints;

    private Bitmap mBgBitmap;
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
        highlighterList = BeanUtils.findAll(topicImage);
        undoList = new ArrayList<>();

        setCurType(Constants.PAINT_BLUE);
        setImageParam(GsonUtils.json2Obj(topicImage.getImageParam(), ImageParam.class));
        setCurPaintWidth(topicImage.getWord_size());
    }

    public void setScrollListener(OnScrollListener listener) {
        scrollListener = listener;
        listener.scrollEnd();
    }

    public boolean startOCR() {
        List<Highlighter> ocrResult;
        ocrResult = OpenCVUtil.getInstance().HSV(topicImage.getPath(), topicImage.getWord_size());
        if (ocrResult == null || ocrResult.isEmpty()) {
            Toast.makeText(getContext(), "未识别出任何内容....", Toast.LENGTH_SHORT).show();
            topicImage.setOcr(false);
            topicImage.setToDefault("ocr");
            return false;
        } else {
            highlighterList.addAll(ocrResult);
            topicImage.setOcr(true);
            setImageParam(null);
            return true;
        }
    }

    public void removeOCR() {
        Iterator<Highlighter> iterator = highlighterList.iterator();
        while (iterator.hasNext()){
            Highlighter highlighter = iterator.next();
            if (highlighter.getRect() != null) {
                iterator.remove();
            }
        }
        topicImage.setOcr(false);
        setImageParam(new ImageParam());
    }

    public void saveDrawingImage() {
        FilesUtils.getInstance().saveCacheTopicImage(topicImage, mBgBitmap);
    }

    public List<Highlighter> getHighlighterList() {
        return highlighterList;
    }

    public void recycle() {
        if (mBgBitmap != null) {
            mBgBitmap.recycle();
        }
        ImageTask.getInstance().clearTopicImage(topicImage);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 画布和图片共用一个坐标系，只需要处理屏幕坐标系到图片（画布）坐标系的映射关系(toX toY)
        canvas.translate(mBitmapTransX, mBitmapTransY);
        canvas.scale(mBitmapScale, mBitmapScale);

        canvas.drawBitmap(mBgBitmap, 0, 0, null);

        for (Highlighter highlighter : highlighterList) {
            canvas.save();
            if (highlighter.getRect() != null) {
                PaintUtil.setRectPaint(true);
                canvas.drawRect(highlighter.getRect(), PaintUtil.mPaint);
            } else {
                PaintUtil.setPaint(highlighter, true);
                Path path = PaintUtil.pointsToPath(highlighter.getPointList());
                canvas.drawPath(path, PaintUtil.mPaint);
            }
            canvas.restore();
        }

        if (curType == Constants.PAINT_ERASE && curPoints != null) {
            PaintUtil.setErasePaint(curWidth);
            Path path = PaintUtil.pointsToPath(curPoints);
            canvas.drawPath(path, PaintUtil.mPaint);
        }
    }

    public final float toX(float touchX) {
        return (touchX - mBitmapTransX) / mBitmapScale;
    }

    public final float toY(float touchY) {
        return (touchY - mBitmapTransY) / mBitmapScale;
    }

    private void onTouchEvent() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        touchGesture = new TouchGesture(getContext());
        touchGesture.setScaleListener(this);
        touchGesture.setScrollListener(this);
    }

    private void erasePoints() {
        if (curPoints == null || curPoints.isEmpty() || curType != Constants.PAINT_ERASE) {
            return;
        }

        Iterator<Highlighter> iterator = highlighterList.iterator();

        boolean flag;

        while (iterator.hasNext()) {
            Highlighter highlighter = iterator.next();
            flag = false;

            Rect rect = highlighter.getRect();
            if (rect != null) {
                for (Point point : curPoints) {
                    if (rect.contains(point.x, point.y)) {
                        undoList.add(highlighter);
                        iterator.remove();
                        break;
                    }
                }
                continue;
            }
            List<Point> pointList = highlighter.getPointList();
            for (Point p1 : curPoints) {
                if (flag) break;
                for (Point p2 : pointList) {
                    if (Math.abs(p1.x - p2.x) < highlighter.getWidth()
                        && Math.abs(p1.y - p2.y) < highlighter.getWidth()) {
                        undoList.add(highlighter);
                        iterator.remove();
                        flag = true;
                        break;
                    }
                }
            }
        }
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

        if (rectWidth * rectHeight < 625) {
            Log.d(TAG,"涂抹区域太小，删除以上涂抹点");
            highlighterList.remove(highlighterList.size()-1);
        }else{
           undoList.clear();
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
        return !undoList.isEmpty();
    }

    public boolean redo(){

        if (redoAble()){
            Highlighter tmp = undoList.get(undoList.size()-1);
            highlighterList.add(tmp);
            undoList.remove(tmp);
            invalidate();
        }
        return redoAble();
    }

    //撤销
    public boolean undoAble() {
        return !highlighterList.isEmpty();
    }

    public boolean undo(){

        if (undoAble()){
            Highlighter tmp = highlighterList.get(highlighterList.size()-1);
            undoList.add(tmp);
            highlighterList.remove(tmp);
            invalidate();
        }
        return undoAble();
    }

    public void setCurType(int type) {
        curType = type;
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
    }

    public int getCurWidth() {
        return curWidth;
    }

    public void setImageParam(ImageParam param) {
        topicImage.setImageParam(GsonUtils.obj2Json(param));
        if (mBgBitmap != null) {
            mBgBitmap.recycle();
        }
        this.mBgBitmap = BitmapUtils.getBitmap(topicImage);
        imageWidth = mBgBitmap.getWidth();
        imageHeight = mBgBitmap.getHeight();
        invalidate();
    }

    @Override
    public void onScrollBegin(float x, float y) {
        int X = (int) toX(x);
        int Y = (int) toY(y);

        Log.d(TAG, "滑动开始-->("+X+":"+Y+")");
        if (X > 0 && X < imageWidth && Y > 0 && Y < imageHeight){

            curPoints = new ArrayList<>();
            curPoints.add(new Point(X,Y));

            if (curType != Constants.PAINT_ERASE) {
                Highlighter curHighlighter = new Highlighter(curType);
                curHighlighter.setWidth(curWidth);
                curHighlighter.setPointList(curPoints);
                highlighterList.add(curHighlighter);
            }
        }
    }

    @Override
    public boolean onScroll(float x, float y) {

        int X = (int) toX(x);
        int Y = (int) toY(y);

        if (X > 0 && X < imageWidth && Y > 0 && Y < imageHeight) {
            Log.d(TAG, "滑动中-->(" + X + ":" + Y + ")");
            if (curPoints != null) {
                curPoints.add(new Point(X, Y));
                invalidate();
                return true;
            } else {
                onScrollBegin(x, y);
            }
        }
        return false;
    }

    @Override
    public void onScrollEnd(float x, float y) {
        int X = (int) toX(x);
        int Y = (int) toY(y);

        if (X > 0 && X < imageWidth && Y > 0 && Y < imageHeight){
            Log.d(TAG, "滑动结束-->("+X+":"+Y+")");
            if (curPoints != null) {
                curPoints.add(new Point(X,Y));
            }
        }
        if (curType == Constants.PAINT_ERASE) {
            erasePoints();
        } else {
            undoList.clear();
        }
        invalidate();
        curPoints = null;
        if (scrollListener != null) {
            scrollListener.scrollEnd();
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

    public interface OnScrollListener {
        void scrollEnd();
    }
}
