package org.openhab.habclient.dagger;

import org.openhab.habdroid.ui.OpenHABInfoActivity;

import dagger.Subcomponent;

@Subcomponent
public interface InfoActivityComponent {
    void inject(OpenHABInfoActivity activity);
}
