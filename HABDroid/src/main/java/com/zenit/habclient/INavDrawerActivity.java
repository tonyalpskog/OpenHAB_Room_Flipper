package com.zenit.habclient;

import android.support.v4.widget.DrawerLayout;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface INavDrawerActivity {
    public void openSitemap(String sitemapUrl);
    public DrawerLayout getDrawerLayout();
}
