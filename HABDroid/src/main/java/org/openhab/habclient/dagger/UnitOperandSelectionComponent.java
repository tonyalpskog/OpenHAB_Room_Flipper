package org.openhab.habclient.dagger;

import org.openhab.habclient.rule.UnitOperandSelectionDialogFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = {
        UnitEntityDataTypeModule.class
})
public interface UnitOperandSelectionComponent {
    void inject(UnitOperandSelectionDialogFragment fragment);
}
