package org.openhab.habclient.media;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;

/**
 * Created by Tony Alpskog in 2015.
 */
public interface ICamera {
    boolean hasCamera();
    int takePhoto(Activity activity);
    int takePhoto(Fragment fragment);
    String getPhotoPath(int resultCode, Intent data);
}
