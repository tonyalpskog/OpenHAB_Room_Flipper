package org.openhab.habclient.dagger;

import org.openhab.habclient.rule.RuleOperandDialogFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = {
        UnitEntityDataTypeModule.class,
        RuleProviderModule.class
})
public interface RuleOperandComponent {
    void inject(RuleOperandDialogFragment fragment);
}
