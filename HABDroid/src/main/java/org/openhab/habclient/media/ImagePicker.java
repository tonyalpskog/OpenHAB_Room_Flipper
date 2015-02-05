package org.openhab.habclient.media;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2015.
 */
public class ImagePicker implements IImagePicker {
    static final int REQUEST_IMAGE_GALLERY_PICK = 1002;

    @Inject
    public ImagePicker() {}
    
    public int pickImageFromGallery(Activity activity) {
        activity.startActivityForResult(getPickImageFromGalleryIntent(), REQUEST_IMAGE_GALLERY_PICK);
        return REQUEST_IMAGE_GALLERY_PICK;
    }

    public int pickImageFromGallery(Fragment fragment) {
        fragment.startActivityForResult(getPickImageFromGalleryIntent(), REQUEST_IMAGE_GALLERY_PICK);
        return REQUEST_IMAGE_GALLERY_PICK;
    }

    private Intent getPickImageFromGalleryIntent() {
        return new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    public String getImagePath(int resultCode, Intent data, ContentResolver contentResolver) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = contentResolver.query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            return picturePath;
        } else {
            return new String();
        }
    }
}
