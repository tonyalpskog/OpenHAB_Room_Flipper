package org.openhab.habclient.media;

import android.app.Activity;

/**
 * Created by Tony Alpskog in 2015.
 */
public interface ICamera {
    boolean hasCamera();
    String takePictureWithIntent(int requestCode, Activity activity);
}
