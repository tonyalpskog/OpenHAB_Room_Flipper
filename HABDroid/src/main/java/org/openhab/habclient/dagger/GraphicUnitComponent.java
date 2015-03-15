package org.openhab.habclient.dagger;

import org.openhab.habclient.GraphicUnitWidget;

import dagger.Component;

@ViewScope
@Component(dependencies = AppComponent.class)
public interface GraphicUnitComponent {
    void inject(GraphicUnitWidget widget);
}
