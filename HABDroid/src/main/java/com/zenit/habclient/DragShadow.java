package com.zenit.habclient;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.View;

/**
 * Created by Tony Alpskog in 2013.
 */
//Use DragShadow to modify the object shown when dragged.
public class DragShadow extends View.DragShadowBuilder {

    int shadowDiameter = 200;

    public DragShadow(View v) {
        super(v);

    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        super.onDrawShadow(canvas);

        int strokeWidth = 4;

        Paint circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setAlpha(50);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setMaskFilter(new BlurMaskFilter(shadowDiameter/4, BlurMaskFilter.Blur.INNER));

        canvas.drawCircle(shadowDiameter/2, shadowDiameter/2, shadowDiameter/2, circlePaint);
    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point touchPoint) {
        Log.d("Unit", "DragShadow touchPoint before = " + touchPoint.x + "/" + touchPoint.y);
        shadowSize.set(shadowDiameter, shadowDiameter);
        touchPoint.set(/*shadowDiameter/6 - getView().getWidth()*/shadowDiameter/2, /*shadowDiameter/6 - getView().getHeight()*/shadowDiameter/2);
        Log.d("Unit", "DragShadow touchPoint after = " + touchPoint.x + "/" + touchPoint.y);
    }
}

