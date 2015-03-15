package org.openhab.habclient.dagger;

import org.openhab.habclient.rule.RuleActionDialogFragment;
import org.openhab.habclient.rule.RuleActionFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = {
        RuleProviderModule.class,
        UnitEntityDataTypeModule.class
})
public interface RuleActionComponent {
    void inject(RuleActionDialogFragment fragment);
    void inject(RuleActionFragment fragment);
}
