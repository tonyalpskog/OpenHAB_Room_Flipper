package org.openhab.habclient.dagger;

import org.openhab.habdroid.ui.OpenHABMainActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = {
        RestCommunicationModule.class
})
public interface MainActivityComponent {
    void inject(OpenHABMainActivity activity);
}
