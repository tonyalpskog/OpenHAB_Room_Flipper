package org.openhab.habclient.dagger;

import org.openhab.habclient.rule.RuleListActivity;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {
        RuleProviderModule.class,
        NotificationModule.class,
        WearModule.class,
        WidgetModule.class
})
public interface RuleListComponent {
    void inject(RuleListActivity activity);
}
