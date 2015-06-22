package org.openhab.habclient.dagger;

import org.openhab.habclient.wear.WearListenerService;

import dagger.Component;

@ServiceScope
@Component(dependencies = AppComponent.class)
public interface WearListenerServiceComponent {
    void inject(WearListenerService service);
}
