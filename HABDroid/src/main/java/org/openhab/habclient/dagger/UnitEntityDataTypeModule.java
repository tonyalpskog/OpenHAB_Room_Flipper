package org.openhab.habclient.dagger;

import org.openhab.domain.IUnitEntityDataTypeProvider;
import org.openhab.domain.UnitEntityDataTypeProvider;

import dagger.Module;
import dagger.Provides;

@Module
public class UnitEntityDataTypeModule {
    @Provides
    public IUnitEntityDataTypeProvider provideUnitEntityDataTypeProvider(UnitEntityDataTypeProvider provider) {
        return provider;
    }
}
