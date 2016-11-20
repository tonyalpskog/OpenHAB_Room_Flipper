package org.openhab.habclient.dagger;

import org.openhab.habclient.rule.UnitOperandSelectionDialogFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = {
        UnitEntityDataTypeModule.class
})
public interface UnitOperandSelectionComponent {
    void inject(UnitOperandSelectionDialogFragment fragment);
}
