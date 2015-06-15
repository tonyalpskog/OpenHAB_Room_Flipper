package org.openhab.habclient.media;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2015.
 */
//TODO - Implement Camera2 for API21 later [Tony Alpskog 2015-02-02]
public class Camera implements ICamera {
    static final int REQUEST_PHOTO_CAPTURE = 1001;
    private String mCurrentPhotoPath;
    private final Context mContext;

    @Inject
    public Camera(Context context) {
        mContext = context;
    }

    public boolean hasCamera() {
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
    
    public int takePhoto(Activity activity) {
        Intent intent = getTakePhotoIntent();
        if(!intent.equals(null))
            activity.startActivityForResult(intent, REQUEST_PHOTO_CAPTURE);
        return REQUEST_PHOTO_CAPTURE;
    }

    public int takePhoto(Fragment fragment) {
        Intent intent = getTakePhotoIntent();
        if(!intent.equals(null))
            fragment.startActivityForResult(intent, REQUEST_PHOTO_CAPTURE);
        return REQUEST_PHOTO_CAPTURE;
    }

    private Intent getTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            File imageFile = null;
            try {
                imageFile = createImageFile();
            } catch (IOException ex) {
                Log.e("Image file", "Error while generating a file");
            }
            if (imageFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(imageFile));
                return takePictureIntent;
            }
        }
        return null;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "RoomFlipper_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public String getPhotoPath(int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK)
            return mCurrentPhotoPath;
        return new String();
    }
}
