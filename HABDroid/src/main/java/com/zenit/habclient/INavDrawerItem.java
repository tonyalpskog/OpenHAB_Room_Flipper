package com.zenit.habclient;

import android.content.Context;
import android.view.View;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface INavDrawerItem {
    public void itemClickAction(Context context, INavDrawerActivity activity);
    public NavDrawerItemType getType();
    public View getView(Context context, View convertView);
}
