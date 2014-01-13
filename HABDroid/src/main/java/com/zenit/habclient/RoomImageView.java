package com.zenit.habclient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Tony Alpskog in 2013.
 */
public class RoomImageView extends ImageView {

    OnBackgroundDrawn mOnBackgroundDrawn;

    private final String TAG = "RoomImageView";

    private int scaledBitmapHeight = 0;
    private int scaledBitmapWidth = 0;
    private int scaledBitmapX = 0;
    private int scaledBitmapY = 0;

    public RoomImageView(Context context) {
        this(context, null);
    }

    public RoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int oldHeight = scaledBitmapHeight;
        int oldWidth = scaledBitmapWidth;
        int oldX = scaledBitmapX;
        int oldY = scaledBitmapY;

        updateScaledBitmapDimensions();

        if(oldX != scaledBitmapX || oldY != scaledBitmapY || oldHeight != scaledBitmapHeight || oldWidth != scaledBitmapWidth) {
            Log.d(TAG, "onDraw() - Layout is resized");
            Log.e("Room", "width=" + getScaledBitmapWidth() + " height="+getScaledBitmapHeight() + " x=" + getScaledBitmapX() + " y=" + getScaledBitmapY());
            postOnBackgroundDrawn();
        } else
            Log.d(TAG, "onDraw() - Layout was not resized");
    }

    public int getScaledBitmapHeight() {
        return scaledBitmapHeight;
    }

    public int getScaledBitmapWidth() {
        return scaledBitmapWidth;
    }

    public int getScaledBitmapX() {
        return scaledBitmapX;
    }

    public int getScaledBitmapY() {
        return scaledBitmapY;
    }

    private void updateScaledBitmapDimensions() {

        // Get image matrix values and place them in an array
        float[] f = new float[9];
        getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];
        scaledBitmapX = Math.round(f[Matrix.MTRANS_X]);
        scaledBitmapY = Math.round(f[Matrix.MTRANS_Y]);

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        scaledBitmapWidth = Math.round(origW * scaleX);
        scaledBitmapHeight = Math.round(origH * scaleY);
    }

    public interface OnBackgroundDrawn {
        boolean onBackgroundDrawn(View v);
    }

    public void setOnBackgroundDrawnListener(OnBackgroundDrawn eventListener) {
        mOnBackgroundDrawn = eventListener;
    }

    private boolean postOnBackgroundDrawn() {
        if(mOnBackgroundDrawn != null) {
            mOnBackgroundDrawn.onBackgroundDrawn(this);
            return true;
        }
        return false;
    }
}

