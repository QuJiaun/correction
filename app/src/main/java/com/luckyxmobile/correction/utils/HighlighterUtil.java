package com.luckyxmobile.correction.utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.model.bean.TopicImage;

import java.util.List;

public class HighlighterUtil {

    public static Paint clearPaint() {
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        return paint;
    }

    public static void setHighlighter(Context context, Paint paint, int type) {

        if (type == Constants.PAINT_ERASE) {
            paint.setXfermode(new  PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            paint.setAlpha(255);
        } else if (type == Constants.PAINT_WHITE_OUT) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            paint.setAlpha(255);
        } else {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
            paint.setAlpha(150);
        }

        paint.setColor(context.getColor(type));
    }

    public static Paint defaultHighlighter(Context context) {
        return getHighlighter(context, Constants.PAINT_THIN, Constants.PAINT_BLUE, true);
    }

    public static Paint getHighlighter(Context context, int width, int color, boolean isShow) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStrokeJoin(Paint.Join.BEVEL);

        paint.setStrokeWidth(width);

        paint.setColor(context.getColor(color));

        paint.setXfermode(isShow ?
                new PorterDuffXfermode(PorterDuff.Mode.DARKEN)
                : new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

        paint.setAlpha(isShow ? 150 :255);

        if (color == Constants.PAINT_ERASE) {
            paint.setXfermode(new  PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            paint.setAlpha(255);
        } else if (color == Constants.PAINT_WHITE_OUT) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            paint.setAlpha(255);
        }

        return paint;
    }

    public static Paint getHighlighter(Context context, TopicImage.Highlighter highlighter, boolean isShow) {
        return getHighlighter(context, highlighter.getWidth(), highlighter.getType(), isShow);
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
}
