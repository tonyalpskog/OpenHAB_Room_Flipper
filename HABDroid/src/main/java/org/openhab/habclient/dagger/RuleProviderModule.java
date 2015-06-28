package org.openhab.habclient.dagger;

import org.openhab.habclient.rule.AdapterProvider;
import org.openhab.habclient.rule.IAdapterProvider;

import dagger.Module;
import dagger.Provides;

@Module
public class RuleProviderModule {
    @Provides
    public IAdapterProvider provideAdapterProvider(AdapterProvider provider) { return provider; }
}
