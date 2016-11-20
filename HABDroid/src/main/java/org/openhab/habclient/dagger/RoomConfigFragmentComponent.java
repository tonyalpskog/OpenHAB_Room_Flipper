package org.openhab.habclient.dagger;

import org.openhab.habclient.RoomConfigFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = {
        RestCommunicationModule.class,
        CameraModule.class
})
public interface RoomConfigFragmentComponent {
    void inject(RoomConfigFragment fragment);
}
