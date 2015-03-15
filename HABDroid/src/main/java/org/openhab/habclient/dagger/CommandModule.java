package org.openhab.habclient.dagger;

import org.openhab.domain.command.ICommandColorProvider;
import org.openhab.domain.command.ICommandPhrasesProvider;
import org.openhab.habclient.command.CommandColorProvider;
import org.openhab.habclient.command.CommandPhrasesProvider;

import dagger.Module;
import dagger.Provides;

@Module
public class CommandModule {
    @Provides
    public ICommandColorProvider provideCommandColorProvider(CommandColorProvider provider) {
        return provider;
    }

    @Provides
    public ICommandPhrasesProvider provideCommandPhrasesProvider(CommandPhrasesProvider provider) {
        return provider;
    }
}
