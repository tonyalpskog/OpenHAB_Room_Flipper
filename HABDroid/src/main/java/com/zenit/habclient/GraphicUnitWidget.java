package com.zenit.habclient;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.openhab.habdroid.R;

/**
 * Created by Tony Alpskog in 2013.
 */
public class GraphicUnitWidget extends ImageView implements View.OnClickListener, View.OnLongClickListener {

    GraphicUnit gUnit;
    Bitmap originalBitmap;

    public GraphicUnitWidget(Context context) {
        super(context);
    }

    public GraphicUnitWidget(Context context, GraphicUnit graphicUnit) {
        this(context);
        gUnit = graphicUnit;

        int imageResource = R.drawable.ic_lightbulb;
        switch (gUnit.getType()) {
            case SWITCH:
                imageResource = R.drawable.ic_lightbulb;
                break;
            case DIMMER:
                break;
            case ROOM_HEATER:
                break;
            case VENT:
                imageResource = R.drawable.ic_unit_fan;
                break;
        }

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imageResource);
        originalBitmap = bitmap;
        setImageBitmap(bitmap);
        setOnLongClickListener(this);
        setOnClickListener(this);
    }

    public void setOriginalBitmap(Bitmap bitmap) {
        originalBitmap = bitmap;
    }

    @Override
    public boolean onLongClick(View v) {
       Log.d("G-Click", "Long click detected");
       ClipData clipData = ClipData.newPlainText("label","text");
       this.startDrag(clipData, new DragShadow(this), this, 0);
       return false;
    }

    @Override
    public void onClick(View v) {
        Log.d("G-Click", "View status BEFORE = " + (v.isSelected() ? "Selected" : "Not selected"));
        gUnit.setSelected(!gUnit.isSelected());
        Log.d("G-Click", "View status AFTER = " + (v.isSelected()? "Selected" : "Not selected"));
    }

    public void drawSelection(boolean selected) {
        if(selected) {
            Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

            Rect bounds = getDrawable().getBounds();
            int width = bounds.width();
            int height = bounds.height();
            int bitmapWidth = getDrawable().getIntrinsicWidth();
            int bitmapHeight = getDrawable().getIntrinsicHeight();
            Log.d("Bitmap", "Height = " + bitmapHeight + "   Width = " + bitmapWidth);

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);

            Canvas canvas = new Canvas(bitmap);

            canvas.drawCircle(canvas.getHeight()/2, canvas.getWidth()/2, (float) Math.floor(bitmapHeight/2) - Math.round(paint.getStrokeWidth()/2), paint);
            setImageBitmap(bitmap);
        } else {
            setImageBitmap(originalBitmap);
        }
    }
}
