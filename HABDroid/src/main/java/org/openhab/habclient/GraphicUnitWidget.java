package org.openhab.habclient;

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

import org.openhab.domain.IApplicationModeProvider;
import org.openhab.domain.model.ApplicationMode;
import org.openhab.domain.model.GraphicUnit;
import org.openhab.habdroid.R;
import org.openhab.domain.model.OpenHABWidgetType;
import org.openhab.habdroid.ui.OpenHABMainActivity;
import org.openhab.habdroid.util.AutoRefreshImageView;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2013.
 */
public class GraphicUnitWidget extends AutoRefreshImageView implements View.OnClickListener, View.OnLongClickListener {

    private GraphicUnit gUnit;
    private UnitContainerView mUnitContainerView;
    private HABApplication mApplication;
    @Inject IOpenHABSetting mOpenHABSetting;
    @Inject IApplicationModeProvider mApplicationModeProvider;

    public GraphicUnitWidget(Context context) {
        super(context);

        mApplication = (HABApplication) context.getApplicationContext();
        mApplication.inject(this);
    }
    public GraphicUnitWidget(Context context, GraphicUnit graphicUnit, UnitContainerView unitContainerView) {
        this(context);

        gUnit = graphicUnit;
        mUnitContainerView = unitContainerView;

        String iconUrl = mOpenHABSetting.getBaseUrl() + "images/" + Uri.encode(gUnit.getOpenHABWidget().getIcon() + ".png");
        setImageUrl(iconUrl, R.drawable.openhabiconsmall, mOpenHABSetting.getUsername(), mOpenHABSetting.getPassword());
        setOnLongClickListener(this);
        setOnClickListener(this);
    }

    public GraphicUnit getgUnit() {
        return gUnit;
    }

    @Override
    public boolean onLongClick(View v) {
       Log.d("G-Click", "Long click detected");
       if(mApplicationModeProvider.getAppMode() == ApplicationMode.UnitPlacement) {
           ClipData clipData = ClipData.newPlainText("label","text");
           this.startDrag(clipData, new DragShadow(this), this, 0);
       }
       return false;
    }

    @Override
    public void onClick(View v) {
        Log.d("G-Click", "Short click detected");
        if(mApplicationModeProvider.getAppMode() == ApplicationMode.UnitPlacement) {
            Log.d("G-Click", "View status BEFORE = " + (v.isSelected() ? "Selected" : "Not selected"));
            gUnit.setSelected(!gUnit.isSelected());
            setSelected(gUnit.isSelected());
            drawSelection(gUnit.isSelected());
            Log.d("G-Click", "View status AFTER = " + (v.isSelected()? "Selected" : "Not selected"));
        } else if(mApplicationModeProvider.getAppMode() == ApplicationMode.RoomFlipper) {
            if(gUnit.getOpenHABWidget().getType() == OpenHABWidgetType.Group) {
                // Get launch intent for application
                Intent widgetListIntent = new Intent(getContext(), OpenHABMainActivity.class);
                widgetListIntent.setAction("SHOW_PAGE_AS_LIST");//TODO - Centralize this parameter
                widgetListIntent.putExtra("pageUrl", "openhab://sitemaps/demo/" + gUnit.getOpenHABWidget().getLinkedPage().getId() /*GF_Kitchen"*/ /*"https://demo.openhab.org:8443/rest/sitemaps/demo/GF_Kitchen"*/);
                Log.d(HABApplication.getLogTag(), "SHOW_PAGE_AS_LIST  Intent for: " + "openhab://sitemaps/demo/" + gUnit.getOpenHABWidget().getLinkedPage().getId());

                // Start launch activity
                getContext().startActivity(widgetListIntent);
            } else
                if(gUnit.getOpenHABWidget().getType().HasDynamicControl)
                    mUnitContainerView.drawControlInRoom(gUnit);
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
            String iconUrl = mOpenHABSetting.getBaseUrl() + "images/" + Uri.encode(gUnit.getOpenHABWidget().getIcon() + ".png");
            setImageUrl(iconUrl, R.drawable.openhabiconsmall, mOpenHABSetting.getUsername(), mOpenHABSetting.getPassword());
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
