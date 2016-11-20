package org.openhab.habclient.dagger;

import org.openhab.habclient.UnitPlacementFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = {
        RestCommunicationModule.class
})
public interface UnitPlacementComponent {
    void inject(UnitPlacementFragment fragment);
}
