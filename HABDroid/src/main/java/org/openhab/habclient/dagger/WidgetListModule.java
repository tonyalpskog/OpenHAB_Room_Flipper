package org.openhab.habclient.dagger;

import org.openhab.habdroid.ui.IWidgetTypeLayoutProvider;
import org.openhab.habdroid.ui.WidgetTypeLayoutProvider;

import dagger.Module;
import dagger.Provides;

@Module
public class WidgetListModule {
    @Provides
    public IWidgetTypeLayoutProvider provideWidgetTypeLayoutProvider(WidgetTypeLayoutProvider provider) {
        return provider;
    }
}
