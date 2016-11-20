package org.openhab.habclient.dagger;

import org.openhab.habclient.GraphicUnitWidget;

import dagger.Subcomponent;

@ViewScope
@Subcomponent
public interface GraphicUnitComponent {
    void inject(GraphicUnitWidget widget);
}
