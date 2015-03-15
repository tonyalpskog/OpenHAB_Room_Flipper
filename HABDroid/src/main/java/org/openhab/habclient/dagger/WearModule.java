package org.openhab.habclient.dagger;

import org.openhab.habclient.wear.IWearCommandHost;
import org.openhab.habclient.wear.WearCommandHost;

import dagger.Module;
import dagger.Provides;

@Module
public class WearModule {
    @Provides
    public IWearCommandHost provideWearCommandHost(WearCommandHost wearCommandHost) { return wearCommandHost; }
}
