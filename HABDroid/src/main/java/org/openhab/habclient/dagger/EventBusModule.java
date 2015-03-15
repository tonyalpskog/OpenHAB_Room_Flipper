package org.openhab.habclient.dagger;

import org.openhab.domain.IEventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

@Module
public class EventBusModule {
    @Provides @Singleton
    public IEventBus provideEventBus() {
        return new IEventBus() {
            @Override
            public void post(Object event) {
                EventBus.getDefault().post(event);
            }

            @Override
            public void postSticky(Object event) {
                EventBus.getDefault().postSticky(event);
            }

            @Override
            public void registerSticky(Object subscriber) {
                EventBus.getDefault().registerSticky(subscriber);
            }
        };
    }
}
