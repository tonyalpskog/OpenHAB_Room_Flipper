package org.openhab.habclient.dagger;

import org.openhab.domain.rule.IRuleProvider;
import org.openhab.domain.rule.RuleProvider;
import org.openhab.habclient.rule.AdapterProvider;
import org.openhab.habclient.rule.IAdapterProvider;

import dagger.Module;
import dagger.Provides;

@Module
public class RuleProviderModule {
    @Provides
    public IRuleProvider provideRuleProvider(RuleProvider provider) { return provider; }

    @Provides
    public IAdapterProvider provideAdapterProvider(AdapterProvider provider) { return provider; }
}
