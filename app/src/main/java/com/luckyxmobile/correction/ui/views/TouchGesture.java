package com.luckyxmobile.correction.ui.views;

import android.content.Context;
import android.view.MotionEvent;

import cn.forward.androids.ScaleGestureDetectorApi27;
import cn.forward.androids.TouchGestureDetector;

public class TouchGesture {


    private Context context;
    private TouchGestureDetector mTouchGestureDetector;
    private OnTouchGestureScale scaleListener;
    private OnTouchGestureScroll scrollListener;
    private OnTouchGestureSingleTapUp singleTapUpListener;

    public TouchGesture(Context context) {
        this.context = context;
        init();
    }

    public void setScaleListener(OnTouchGestureScale scaleListener) {
        this.scaleListener = scaleListener;
    }

    public void setScrollListener(OnTouchGestureScroll scrollListener) {
        this.scrollListener = scrollListener;
    }

    public void setSingleTapUpListener(OnTouchGestureSingleTapUp singleTapUpListener) {
        this.singleTapUpListener = singleTapUpListener;
    }

    private void init() {
        mTouchGestureDetector = new TouchGestureDetector(context,
                new TouchGestureDetector.OnTouchGestureListener() {

            @Override //点击
            public boolean onSingleTapUp(MotionEvent e) {
                if (singleTapUpListener != null) {
                    return singleTapUpListener.onSingleTapUp(e.getX(), e.getY());
                }
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetectorApi27 detector) {
                if (scaleListener != null) {
                    return scaleListener.onScaleBegin(detector);
                }
                return false;
            }

            @Override // 双指缩放中
            public boolean onScale(ScaleGestureDetectorApi27 detector) {

                if (scaleListener != null) {
                    return scaleListener.onScale(detector);
                }

                return false;
            }

            @Override //滑动开始
            public void onScrollBegin(MotionEvent e) {
                if (scrollListener != null) {
                    scrollListener.onScrollBegin(e.getX(), e.getY());
                }
            }

            @Override //滑动中
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (scrollListener != null) {
                    return scrollListener.onScroll(e2.getX(), e2.getY());
                }
                return false;
            }

            @Override //滑动结束
            public void onScrollEnd(MotionEvent e) {
                if (scrollListener != null) {
                    scrollListener.onScrollEnd(e.getX(), e.getY());
                }
            }
        });

        // 下面两行绘画场景下应该设置间距为大于等于1，否则设为0双指缩放后抬起其中一个手指仍然可以移动
        mTouchGestureDetector.setScaleSpanSlop(1); // 手势前识别为缩放手势的双指滑动最小距离值
        mTouchGestureDetector.setScaleMinSpan(1); // 缩放过程中识别为缩放手势的双指最小距离值
        mTouchGestureDetector.setIsLongpressEnabled(false);
        mTouchGestureDetector.setIsScrollAfterScaled(false);
    }

    public boolean onTouchEvent(MotionEvent event){
        return mTouchGestureDetector.onTouchEvent(event);
    }

    public interface OnTouchGestureScroll {
        void onScrollBegin(float x, float y);
        boolean onScroll(float x, float y);
        void onScrollEnd(float x, float y);
    }

    public interface OnTouchGestureScale {
        boolean onScaleBegin(ScaleGestureDetectorApi27 detector);
        boolean onScale(ScaleGestureDetectorApi27 detector);
    }

    public interface OnTouchGestureSingleTapUp {
        boolean onSingleTapUp(float x, float y);
    }

}
