package org.openhab.habclient.dagger;

import org.openhab.habclient.RoomConfigActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = {

})
public interface RoomConfigActivityComponent {
    void inject(RoomConfigActivity activity);
}
