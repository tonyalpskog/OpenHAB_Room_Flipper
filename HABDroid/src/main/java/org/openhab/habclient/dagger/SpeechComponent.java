package org.openhab.habclient.dagger;

import org.openhab.habclient.SpeechService;

import dagger.Subcomponent;

@ServiceScope
@Subcomponent
public interface SpeechComponent {
    void inject(SpeechService service);
}
