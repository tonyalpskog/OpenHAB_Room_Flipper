package org.openhab.habclient.dagger;

import org.openhab.habclient.wear.WearListenerService;

import dagger.Subcomponent;

@ServiceScope
@Subcomponent
public interface WearListenerServiceComponent {
    void inject(WearListenerService service);
}
