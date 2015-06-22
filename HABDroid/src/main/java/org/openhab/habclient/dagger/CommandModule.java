package org.openhab.habclient.dagger;

import org.openhab.domain.IDeviceCommunicator;
import org.openhab.domain.INodeMessageHandler;
import org.openhab.domain.command.ICommandColorProvider;
import org.openhab.domain.command.ICommandPhrasesProvider;
import org.openhab.habclient.command.CommandColorProvider;
import org.openhab.habclient.command.CommandPhrasesProvider;
import org.openhab.habclient.wear.DeviceCommunicator;
import org.openhab.habclient.wear.WearCommandHandler;

import javax.inject.Singleton;

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

    @Provides @Singleton
    public IDeviceCommunicator provideDeviceCommunicator(DeviceCommunicator deviceCommunicator) {
        return deviceCommunicator;
    }

    @Provides @Singleton
    public INodeMessageHandler provideWearCommandHandler(WearCommandHandler wearCommandHandler) {
        return wearCommandHandler;
    }

}
