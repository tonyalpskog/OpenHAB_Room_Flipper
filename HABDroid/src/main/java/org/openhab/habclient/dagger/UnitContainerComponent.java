package org.openhab.habclient.dagger;

import org.openhab.habclient.UnitContainerView;

import dagger.Subcomponent;

@ViewScope
@Subcomponent(modules = {
        WidgetModule.class,
        RoomImageModule.class
})
public interface UnitContainerComponent {
    void inject(UnitContainerView unitContainerComponent);
}
