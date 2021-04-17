package com.luckyxmobile.correction.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.luckyxmobile.correction.model.BeanUtils;
import com.luckyxmobile.correction.model.bean.Highlighter;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.utils.BitmapUtils;
import com.luckyxmobile.correction.utils.PaintUtil;

import java.util.ArrayList;
import java.util.List;
import cn.forward.androids.ScaleGestureDetectorApi27;

public class ShowDrawingView extends View implements TouchGesture.OnTouchGestureSingleTapUp, TouchGesture.OnTouchGestureScale {

    /**背景图, 前景图*/
    private Bitmap mBgBitmap, mFgBitmap;
    private float mBitmapTransX;
    private float mBitmapTransY;
    private float mBitmapScale = 1;
    private final List<HighlighterArea> highlighterAreaArrayList = new ArrayList<>();
    private TouchGesture touchGesture;

    public ShowDrawingView(Context context){
        super(context);
        //初始化点击事件
        onTouchEvent();
    }

    public ShowDrawingView(Context context, AttributeSet attrs) {
        super(context,attrs);
        //初始化点击事件
        onTouchEvent();
    }

    public void init(TopicImage topicImage){

        if (topicImage == null) {
            return;
        }

        setImageBitmap(topicImage);

        if (topicImage.getHighlighterList() != null) {
            for (Highlighter highlighter : BeanUtils.findAll(topicImage)) {
                highlighterAreaArrayList.add(new HighlighterArea(highlighter));
            }

            invalidate();
        }
    }

    /**
     * 初始化图片
     */
    private void setImageBitmap(TopicImage topicImage){
        this.mBgBitmap = BitmapUtils.getBitmap(topicImage);
        this.mFgBitmap = Bitmap.createBitmap(mBgBitmap.getWidth(),mBgBitmap.getHeight(), Bitmap.Config.RGB_565);

        invalidate();
    }

    /**
     * 点击操作
     */
    private void onTouchEvent() {
        // 由手势识别器处理手势
        
        touchGesture = new TouchGesture(getContext());
        touchGesture.setSingleTapUpListener(this);
//        touchGesture.setScaleListener(this);
    }

    private float toX(float touchX) {
        return (touchX - mBitmapTransX) / mBitmapScale;
    }

    private float toY(float touchY) {
        return (touchY - mBitmapTransY) / mBitmapScale;
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 画布和图片共用一个坐标系，只需要处理屏幕坐标系到图片（画布）坐标系的映射关系(toX toY)
        canvas.translate(mBitmapTransX, mBitmapTransY);
        canvas.scale(mBitmapScale, mBitmapScale);

        canvas.drawBitmap(mFgBitmap, 0, 0, null);
        canvas.drawBitmap(mBgBitmap, 0, 0, null);

        canvas.save();
        for (HighlighterArea area: highlighterAreaArrayList){
            Highlighter highlighter = area.highlighter;
            if (highlighter.getRect() != null) {
                if (!area.isShow) {
                    PaintUtil.setRectPaint(false);
                    canvas.drawRect(highlighter.getRect(), PaintUtil.mPaint);
                }
            } else {
                Path path = PaintUtil.pointsToPath(highlighter.getPointList());
                PaintUtil.setPaint(highlighter, area.isShow);
                canvas.drawPath(path, PaintUtil.mPaint);
            }
        }
        canvas.restore();

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

    // 缩放手势操作相关
    Float mLastFocusX, mLastFocusY;
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
        if (mBitmapScale < 0.5f) {
            mBitmapScale = 0.5f;
        }

        invalidate();

        mLastFocusX = mTouchCentreX;
        mLastFocusY = mTouchCentreY;

        return true;
    }

    @Override
    public boolean onSingleTapUp(float x, float y) {

        boolean isIn = false;

        for (HighlighterArea area : highlighterAreaArrayList) {
            Point point = new Point((int)toX(x),(int)toY(y));
            if (area.judgePointInPoints(point)) {
                area.isShow = !area.isShow;
                isIn = true;
            }
        }

        if (isIn) {
            invalidate();
        }
        
        return isIn;
    }

    private static class HighlighterArea {

        boolean isShow;
        Highlighter highlighter;

        public HighlighterArea(Highlighter highlighter) {
            this.isShow = false;
            this.highlighter = highlighter;
        }

        boolean judgePointInPoints(Point point){
            if (highlighter.getRect() != null) {
                Rect rect = highlighter.getRect();
                return rect.contains(point.x, point.y);
            }
            for (Point p: highlighter.getPointList()){
                if (Math.abs(p.x-point.x)< highlighter.getWidth()
                        && Math.abs(p.y-point.y)< highlighter.getWidth()){
                    if (highlighter.getType() != Constants.PAINT_WHITE_OUT
                            && highlighter.getType() != Constants.PAINT_ERASE) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
