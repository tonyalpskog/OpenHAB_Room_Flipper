package org.openhab.habclient.dagger;

import org.openhab.habdroid.ui.OpenHABWidgetListActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class)
public interface WidgetListActivityComponent {
    void inject(OpenHABWidgetListActivity activity);
}
