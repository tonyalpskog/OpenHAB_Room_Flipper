package org.openhab.habclient.dagger;

import org.openhab.habdroid.ui.OpenHABWidgetListFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = {
        WidgetListModule.class,
        RestCommunicationModule.class
})
public interface WidgetListFragmentComponent {
    void inject(OpenHABWidgetListFragment fragment);
}
