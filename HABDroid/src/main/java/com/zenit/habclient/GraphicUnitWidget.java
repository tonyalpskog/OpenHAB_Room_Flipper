package com.zenit.habclient;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.openhab.habdroid.R;
import org.openhab.habdroid.model.OpenHABWidgetType;
import org.openhab.habdroid.ui.OpenHABMainActivity;
import org.openhab.habdroid.ui.OpenHABWidgetListActivity;
import org.openhab.habdroid.util.AutoRefreshImageView;

/**
 * Created by Tony Alpskog in 2013.
 */
public class GraphicUnitWidget extends AutoRefreshImageView implements View.OnClickListener, View.OnLongClickListener {

    GraphicUnit gUnit;

    public GraphicUnitWidget(Context context) {
        super(context);
    }

    public GraphicUnitWidget(Context context, GraphicUnit graphicUnit) {
        this(context);
        gUnit = graphicUnit;
        String iconUrl = HABApplication.getOpenHABSetting().getBaseUrl() + "images/" + Uri.encode(gUnit.getOpenHABWidget().getIcon() + ".png");
        setImageUrl(iconUrl, R.drawable.openhabiconsmall, HABApplication.getOpenHABSetting().getUsername(), HABApplication.getOpenHABSetting().getPassword());
        setOnLongClickListener(this);
        setOnClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
       Log.d("G-Click", "Long click detected");
       if(HABApplication.getAppMode() == ApplicationMode.UnitPlacement) {
           ClipData clipData = ClipData.newPlainText("label","text");
           this.startDrag(clipData, new DragShadow(this), this, 0);
       }
       return false;
    }

    @Override
    public void onClick(View v) {
        Log.d("G-Click", "Short click detected");
        if(HABApplication.getAppMode() == ApplicationMode.UnitPlacement) {
            Log.d("G-Click", "View status BEFORE = " + (v.isSelected() ? "Selected" : "Not selected"));
            gUnit.setSelected(!gUnit.isSelected());
            Log.d("G-Click", "View status AFTER = " + (v.isSelected()? "Selected" : "Not selected"));
        } else if(HABApplication.getAppMode() == ApplicationMode.RoomFlipper) {
            if(gUnit.getOpenHABWidget().getType() == OpenHABWidgetType.Group) {
                // Get launch intent for application
                Intent widgetListIntent = new Intent(getContext(), OpenHABMainActivity.class);
                widgetListIntent.setAction("SHOW_PAGE_AS_LIST");//TODO - Centralize this parameter
                widgetListIntent.putExtra("pageUrl", "openhab://sitemaps/demo/" + gUnit.getOpenHABWidget().getLinkedPage().getId() /*GF_Kitchen"*/ /*"https://demo.openhab.org:8443/rest/sitemaps/demo/GF_Kitchen"*/);

                // Start launch activity
                getContext().startActivity(widgetListIntent);
            } else
                if(gUnit.getOpenHABWidget().getType().HasDynamicControl)
                    gUnit.getUnitContainerView().drawControlInRoom(gUnit);
                else
                    Toast.makeText(getContext(), "Unit action is not (yet) supported for this unit type", Toast.LENGTH_SHORT).show();
        }
    }

    public void drawSelection(boolean selected) {
        if(selected) {
            Bitmap bitmap = drawableToBitmap(getDrawable()).copy(Bitmap.Config.ARGB_8888, true);
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
//            setImageBitmap(originalBitmap);
            String iconUrl = HABApplication.getOpenHABSetting().getBaseUrl() + "images/" + Uri.encode(gUnit.getOpenHABWidget().getIcon() + ".png");
            setImageUrl(iconUrl, R.drawable.openhabiconsmall, HABApplication.getOpenHABSetting().getUsername(), HABApplication.getOpenHABSetting().getPassword());
        }
    }

    private Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
