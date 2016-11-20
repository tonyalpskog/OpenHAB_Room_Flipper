package org.openhab.habclient.dagger;

import org.openhab.habclient.rule.RuleEditActivity;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {
        WidgetModule.class,
        RuleProviderModule.class,
        NotificationModule.class,
        WearModule.class
})
public interface RuleEditActivityComponent {
    void inject(RuleEditActivity activity);
}
