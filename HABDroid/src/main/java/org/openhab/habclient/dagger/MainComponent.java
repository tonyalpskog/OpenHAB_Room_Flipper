package org.openhab.habclient.dagger;

import org.openhab.habclient.MainActivity;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {
        RestCommunicationModule.class
})
public interface MainComponent {
    void inject(MainActivity activity);
}
