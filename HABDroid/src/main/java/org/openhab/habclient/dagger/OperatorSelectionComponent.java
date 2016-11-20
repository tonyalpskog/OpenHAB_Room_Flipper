package org.openhab.habclient.dagger;

import org.openhab.habclient.rule.OperatorSelectionDialogFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = {
        UnitEntityDataTypeModule.class
})
public interface OperatorSelectionComponent {
    void inject(OperatorSelectionDialogFragment fragment);
}
