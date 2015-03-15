package org.openhab.habclient.dagger;

import org.openhab.domain.util.IColorParser;
import org.openhab.domain.util.ILogger;
import org.openhab.domain.util.IRegularExpression;
import org.openhab.domain.util.RegularExpression;
import org.openhab.habclient.ColorParser;
import org.openhab.habdroid.util.AndroidLogger;

import dagger.Module;
import dagger.Provides;

@Module
public class UtilModule {
    @Provides
    public IRegularExpression provideRegularExpression(RegularExpression regularExpression) {
        return regularExpression;
    }
    @Provides
    public IColorParser provideColorParser(ColorParser colorParser) {
        return colorParser;
    }
    @Provides
    public ILogger provideLogger(AndroidLogger logger) {
        return logger;
    }
}
