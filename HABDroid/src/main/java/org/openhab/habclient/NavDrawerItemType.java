package org.openhab.habclient;

/**
 * Created by Tony Alpskog in 2014.
 */
public enum NavDrawerItemType {
    Sitemap(0),
    Flipper(1);

    public final int Value;

    private NavDrawerItemType(int value) {
        Value = value;
    }
}
