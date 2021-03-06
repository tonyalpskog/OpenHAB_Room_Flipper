package org.openhab.habclient.dagger;

import org.openhab.habclient.RoomFlipperFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = {
        NotificationModule.class,
        WearModule.class,
        RoomImageModule.class,
        RestCommunicationModule.class
})
public interface RoomFlipperComponent {
    void inject(RoomFlipperFragment fragment);
}
