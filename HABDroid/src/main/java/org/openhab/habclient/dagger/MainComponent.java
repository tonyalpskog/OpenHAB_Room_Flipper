package org.openhab.habclient.dagger;

import org.openhab.habclient.MainActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = {
        RestCommunicationModule.class
})
public interface MainComponent {
    void inject(MainActivity activity);
}
