package org.openhab.habclient;

import android.content.Context;
import android.content.Intent;
import android.view.View;

/**
 * Created by Tony Alpskog in 2014.
 */
public class FlipperNavDrawerItem implements INavDrawerItem {
    @Override
    public void itemClickAction(Context context, INavDrawerActivity activity) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    public NavDrawerItemType getType() {
        return null;
    }

    @Override
    public View getView(Context context, View convertView) {
        return null;
    }

}
