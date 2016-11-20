package org.openhab.habclient.dagger;

import org.openhab.habdroid.ui.OpenHABWidgetListActivity;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent
public interface WidgetListActivityComponent {
    void inject(OpenHABWidgetListActivity activity);
}
