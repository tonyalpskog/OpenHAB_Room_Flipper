package org.openhab.habclient.dagger;

import org.openhab.domain.IOpenHABWidgetControl;
import org.openhab.habclient.OpenHABWidgetControl;

import dagger.Module;
import dagger.Provides;

@Module
public class WidgetModule {
    @Provides
    public IOpenHABWidgetControl provideOpenHABWidgetControl(OpenHABWidgetControl widgetControl) {
        return widgetControl;
    }
}
