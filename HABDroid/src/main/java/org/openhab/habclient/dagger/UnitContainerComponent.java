package org.openhab.habclient.dagger;

import org.openhab.habclient.UnitContainerView;

import dagger.Component;

@ViewScope
@Component(dependencies = AppComponent.class, modules = {
        WidgetModule.class,
        RoomImageModule.class
})
public interface UnitContainerComponent {
    void inject(UnitContainerView unitContainerComponent);
}
