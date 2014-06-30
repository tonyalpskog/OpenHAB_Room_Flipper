package org.openhab.habclient.dagger;

import android.content.Context;

import org.openhab.domain.IOpenHABWidgetControl;
import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.rule.RuleOperationProvider;
import org.openhab.domain.util.IColorParser;
import org.openhab.domain.util.ILogger;
import org.openhab.habclient.AndroidLogger;
import org.openhab.habclient.ColorParser;
import org.openhab.habclient.HABApplication;
import org.openhab.habclient.IOpenHABSetting;
import org.openhab.habclient.IRestCommunication;
import org.openhab.habclient.MainActivity;
import org.openhab.habclient.OpenHABSetting;
import org.openhab.habclient.OpenHABWidgetControl;
import org.openhab.habclient.OpenHABWidgetProvider;
import org.openhab.habclient.RestCommunication;
import org.openhab.habclient.RoomProvider;
import org.openhab.habclient.command.CommandAnalyzer;
import org.openhab.habclient.command.ICommandAnalyzer;
import org.openhab.habdroid.ui.IWidgetTypeLayoutProvider;
import org.openhab.habdroid.ui.OpenHABMainActivity;
import org.openhab.habdroid.ui.OpenHABWidgetListActivity;
import org.openhab.habdroid.ui.WidgetTypeLayoutProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = DomainModule.class,
injects = {
        OpenHABMainActivity.class,
        OpenHABWidgetListActivity.class,
        MainActivity.class,
        HABApplication.class
})
public class ClientModule {
    @Provides @Singleton
    public IWidgetTypeLayoutProvider provideWidgetTypeLayoutProvider(WidgetTypeLayoutProvider provider) {
        return provider;
    }

    @Provides @Singleton
    public IOpenHABWidgetControl provideOpenHABWidgetControl(OpenHABWidgetControl widgetControl) {
        return widgetControl;
    }

    @Provides @Singleton
    public ICommandAnalyzer provideCommandAnalyzer(CommandAnalyzer commandAnalyzer) {
        return commandAnalyzer;
    }

//    @Provides @Singleton
//    public TextToSpeechProvider provideTextToSpeedSpeechProvider(Context context) {
//        return new TextToSpeechProvider(context, Locale.ENGLISH);
//    }

    @Provides @Singleton
    public IRestCommunication provideRestCommunication(RestCommunication restCommunication) {
        return restCommunication;
    }

    @Provides @Singleton
    public RuleOperationProvider provideRuleOperationProvider() {
        //TODO: extract interface and add @Inject on constructor
        return new RuleOperationProvider();
    }

    @Provides @Singleton
    public RoomProvider provideRoomProvider(Context context, ILogger logger,
                                            IColorParser colorParser, IOpenHABSetting setting,
                                            IOpenHABWidgetProvider widgetProvider) {
        //TODO: extract interface and add @Inject on constructor
        return new RoomProvider(context, logger, colorParser, setting, widgetProvider);
    }

    @Provides @Singleton
    public IOpenHABSetting provideOpenHABSetting(OpenHABSetting openHABSetting) {
        return openHABSetting;
    }

    @Provides @Singleton
    public IOpenHABWidgetProvider provideOpenHABWidgetProvider(OpenHABWidgetProvider provider) {
        return provider;
    }

    @Provides @Singleton
    public IColorParser provideColorParser(ColorParser colorParser) {
        return colorParser;
    }

    @Provides @Singleton
    public ILogger provideLogger(AndroidLogger logger) {
        return logger;
    }
}
