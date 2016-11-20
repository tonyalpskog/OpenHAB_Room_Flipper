package org.openhab.habclient.dagger;

import org.openhab.habclient.rule.RuleOperandDialogFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = {
        UnitEntityDataTypeModule.class,
        RuleProviderModule.class
})
public interface RuleOperandComponent {
    void inject(RuleOperandDialogFragment fragment);
}
