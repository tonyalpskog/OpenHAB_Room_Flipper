package org.openhab.habclient.dagger;

import org.openhab.domain.ITestInformation;
import org.openhab.domain.TestInformation;
import org.openhab.domain.util.IRegularExpression;
import org.openhab.domain.util.RegularExpression;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, includes = AndroidModule.class)
public class DomainModule {
    @Provides @Singleton
    public IRegularExpression provideRegularExpression(RegularExpression regularExpression) {
        return regularExpression;
    }

    @Provides @Singleton
    public ITestInformation providesTestInformation(TestInformation testInformation) { return testInformation; }
}
