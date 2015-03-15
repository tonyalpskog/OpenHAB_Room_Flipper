package org.openhab.habclient.dagger;

import org.openhab.habclient.UnitPlacementFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = {
        RestCommunicationModule.class
})
public interface UnitPlacementComponent {
    void inject(UnitPlacementFragment fragment);
}
