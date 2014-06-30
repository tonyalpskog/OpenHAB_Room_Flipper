package org.openhab.habclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import org.openhab.habdroid.R;
import org.openhab.habdroid.util.AutoRefreshImageView;

/**
 * Created by Tony Alpskog in 2013.
 */
public class GraphicUnitWidget extends AutoRefreshImageView {

    private GraphicUnit gUnit;

    private IGraphicUnitWidgetAdapter mUnitWidgetAdapter;
    public GraphicUnitWidget(Context context) {
        super(context);
    }

    public GraphicUnitWidget(Context context, GraphicUnit graphicUnit) {
        this(context);
        gUnit = graphicUnit;

        final HABApplication application = (HABApplication) context.getApplicationContext();
        final OpenHABSetting setting = application.getOpenHABSetting();
        String iconUrl = setting.getBaseUrl() + "images/" + Uri.encode(gUnit.getOpenHABWidget().getIcon() + ".png");
        setImageUrl(iconUrl, R.drawable.openhabiconsmall, setting.getUsername(), setting.getPassword());
    }

    public GraphicUnit getgUnit() {
        return gUnit;
    }

    public void setAdapter(IGraphicUnitWidgetAdapter adapter) {
        mUnitWidgetAdapter = adapter;
    }

    public void drawSelection(boolean selected, OpenHABSetting setting) {
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
            String iconUrl = setting.getBaseUrl() + "images/" + Uri.encode(gUnit.getOpenHABWidget().getIcon() + ".png");
            setImageUrl(iconUrl, R.drawable.openhabiconsmall, setting.getUsername(), setting.getPassword());
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
