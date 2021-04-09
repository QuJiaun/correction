package com.luckyxmobile.correction.utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.global.MyApplication;
import com.luckyxmobile.correction.model.bean.Highlighter;

import java.util.List;

public class PaintUtil {

    public static Paint mPaint;

    private static PorterDuffXfermode[] mode = {
            new PorterDuffXfermode(PorterDuff.Mode.DARKEN),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER),
            new PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    };

    private static final int DARKEN = 0;
    private static final int SRC_OVER = 1;
    private static final int DST_OUT = 2;


    static {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);
        mPaint.setStrokeJoin(Paint.Join.BEVEL);
    }

    public static void setPaintInPdf(int width) {
        setPaint(width, Constants.PAINT_WHITE_OUT, false);
    }

    public static void setRectPaint(boolean isShow) {
        setPaint(0, Constants.PAINT_OCR, isShow);
    }

    public static void setErasePaint(int width) {
        setPaint(width, Constants.PAINT_ERASE, true);
    }

    private static void setPaint(int width, @Constants.HighlighterType int type, boolean isShow) {

        if (type == Constants.PAINT_OCR) {
            mPaint.setStyle(Paint.Style.FILL);
        } else {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(width);
        }

        mPaint.setColor(type2Color(type));

        if (type == Constants.PAINT_ERASE) {
            mPaint.setXfermode(mode[DST_OUT]);
            mPaint.setAlpha(150);
        } else if (type == Constants.PAINT_WHITE_OUT) {
            mPaint.setXfermode(mode[DARKEN]);
            mPaint.setAlpha(255);
        } else {
            mPaint.setXfermode(isShow ? mode[DARKEN] : mode[SRC_OVER]);
            mPaint.setAlpha(isShow ? 150 :255);
        }
    }

    public static void setPaint(Highlighter highlighter, boolean isShow) {
       setPaint(highlighter.getWidth(), highlighter.getType(), isShow);
    }

    public static Path pointsToPath(List<Point> points){
        Path path = new Path();

        if (points.isEmpty()){
            return path;
        }

        float x = (float) points.get(0).x, y = (float) points.get(0).y;
        float mLastX, mLastY;

        path.moveTo(x,y);
        mLastX = x; mLastY = y;

        for (int i = 1; i < points.size(); i++) {

            x = (float) points.get(i).x; y = (float) points.get(i).y;

            path.quadTo(mLastX,mLastY,(mLastX+x)/2,(mLastY+y)/2);

            mLastX = x; mLastY = y;
        }

        return path;

    }

    private static int type2Color(@Constants.HighlighterType int type) {
        Context context = MyApplication.getContext();
        switch (type) {
            case Constants.PAINT_BLUE:
                return context.getColor(R.color.highlighter_blue);
            case Constants.PAINT_RED:
                return context.getColor(R.color.highlighter_red);
            case Constants.PAINT_YELLOW:
                return context.getColor(R.color.highlighter_yellow);
            case Constants.PAINT_GREEN:
                return R.color.highlighter_green;
            case Constants.PAINT_ERASE:
                return context.getColor(R.color.highlighter_erase);
            case Constants.PAINT_WHITE_OUT:
                return context.getColor(R.color.highlighter_white_out);
            case Constants.PAINT_OCR:
                return context.getColor(R.color.highlighter_OCR);
            default:
                throw new RuntimeException("getHighlighter : type is null");
        }
    }
}
