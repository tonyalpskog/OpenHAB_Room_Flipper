package org.openhab.habclient.dagger;

import org.openhab.habclient.SpeechService;

import dagger.Component;

@ServiceScope
@Component(dependencies = AppComponent.class)
public interface SpeechComponent {
    void inject(SpeechService service);
}
