package org.openhab.habclient.dagger;

import org.openhab.domain.IEventBus;
import org.openhab.domain.util.IRegularExpression;
import org.openhab.domain.util.RegularExpression;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

@Module(library = true, includes = AndroidModule.class)
public class DomainModule {
    @Provides @Singleton
    public IRegularExpression provideRegularExpression(RegularExpression regularExpression) {
        return regularExpression;
    }

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
