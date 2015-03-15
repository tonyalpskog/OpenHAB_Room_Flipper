package org.openhab.habclient.dagger;

import org.openhab.habclient.rule.RuleListActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = {
        RuleProviderModule.class,
        NotificationModule.class,
        WearModule.class,
        WidgetModule.class
})
public interface RuleListComponent {
    void inject(RuleListActivity activity);
}
