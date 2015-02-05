package org.openhab.habclient.media;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;

/**
 * Created by Tony Alpskog in 2015.
 */
public interface IImagePicker {
    int pickImageFromGallery(Activity activity);
    int pickImageFromGallery(Fragment fragment);
    String getImagePath(int resultCode, Intent data, ContentResolver contentResolver);
}
