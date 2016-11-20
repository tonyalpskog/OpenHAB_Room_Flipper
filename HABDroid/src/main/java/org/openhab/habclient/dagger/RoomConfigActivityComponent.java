package org.openhab.habclient.dagger;

import org.openhab.habclient.RoomConfigActivity;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent
public interface RoomConfigActivityComponent {
    void inject(RoomConfigActivity activity);
}
