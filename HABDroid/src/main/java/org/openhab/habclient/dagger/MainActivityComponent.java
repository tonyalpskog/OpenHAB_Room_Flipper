package org.openhab.habclient.dagger;

import org.openhab.habdroid.ui.OpenHABMainActivity;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {
        RestCommunicationModule.class
})
public interface MainActivityComponent {
    void inject(OpenHABMainActivity activity);
}
