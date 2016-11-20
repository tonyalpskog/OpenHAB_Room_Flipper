package org.openhab.habclient.dagger;

import org.openhab.habclient.rule.RuleOperationFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = {
        UnitEntityDataTypeModule.class,
        RuleProviderModule.class
})
public interface RuleOperationComponent {
    void inject(RuleOperationFragment fragment);
}
