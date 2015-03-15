package org.openhab.habclient.dagger;

import org.openhab.habclient.rule.RuleOperationFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = {
        UnitEntityDataTypeModule.class,
        RuleProviderModule.class
})
public interface RuleOperationComponent {
    void inject(RuleOperationFragment fragment);
}
