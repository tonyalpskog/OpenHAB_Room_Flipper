package org.openhab.habclient.dagger;

import org.openhab.habclient.rule.OperatorSelectionDialogFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = {
        UnitEntityDataTypeModule.class
})
public interface OperatorSelectionComponent {
    void inject(OperatorSelectionDialogFragment fragment);
}
