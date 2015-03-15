package org.openhab.habclient.dagger;

import org.openhab.habclient.media.Camera;
import org.openhab.habclient.media.ICamera;
import org.openhab.habclient.media.IImagePicker;
import org.openhab.habclient.media.ImagePicker;

import dagger.Module;
import dagger.Provides;

@Module
public class CameraModule {
    @Provides
    public ICamera provideCamera(Camera camera) { return camera; }

    @Provides
    public IImagePicker provideImagePicker(ImagePicker imagePicker) { return imagePicker; }

}
