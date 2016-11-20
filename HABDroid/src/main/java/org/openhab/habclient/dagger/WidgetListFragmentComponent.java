package org.openhab.habclient.dagger;

import org.openhab.habdroid.ui.OpenHABWidgetListFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = {
        WidgetListModule.class,
        RestCommunicationModule.class
})
public interface WidgetListFragmentComponent {
    void inject(OpenHABWidgetListFragment fragment);
}
