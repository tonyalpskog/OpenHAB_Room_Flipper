package org.openhab.habclient.dagger;

import org.openhab.habclient.RoomConfigFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = {
        RestCommunicationModule.class,
        CameraModule.class
})
public interface RoomConfigFragmentComponent {
    void inject(RoomConfigFragment fragment);
}
