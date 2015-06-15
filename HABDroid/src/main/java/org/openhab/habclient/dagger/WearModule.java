package org.openhab.habclient.dagger;

import org.openhab.habclient.wear.IWearNotificationActions;
import org.openhab.habclient.wear.WearNotificationActions;

import dagger.Module;
import dagger.Provides;

@Module
public class WearModule {
    @Provides
    public IWearNotificationActions provideWearNotificationActions(WearNotificationActions wearNotificationActions) { return wearNotificationActions; }
}
