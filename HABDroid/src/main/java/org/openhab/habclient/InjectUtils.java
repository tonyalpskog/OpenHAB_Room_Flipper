package org.openhab.habclient;

import android.app.Activity;
import android.app.Fragment;
import android.app.Service;

public final class InjectUtils {
    private InjectUtils() {}

    public static void inject(Activity activity) {
        final HABApplication application = (HABApplication) activity.getApplication();
        application.inject(activity);
    }

    public static void inject(Service service) {
        final HABApplication application = (HABApplication) service.getApplication();
        application.inject(service);
    }

    public static void inject(Fragment fragment) {
        final HABApplication application = (HABApplication) fragment.getActivity().getApplication();
        application.inject(fragment);
    }
}
