package org.openhab.habclient.dagger;

import org.openhab.domain.IOpenHABWidgetControl;
import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.rule.IRuleOperationProvider;
import org.openhab.domain.rule.RuleOperationProvider;
import org.openhab.domain.util.IColorParser;
import org.openhab.domain.util.ILogger;
import org.openhab.domain.wear.IWearCommandHost;
import org.openhab.habclient.AndroidLogger;
import org.openhab.habclient.ColorParser;
import org.openhab.habclient.GraphicUnitWidget;
import org.openhab.habclient.HABApplication;
import org.openhab.habclient.IOpenHABSetting;
import org.openhab.habclient.IRestCommunication;
import org.openhab.habclient.IRoomProvider;
import org.openhab.habclient.MainActivity;
import org.openhab.habclient.OpenHABSetting;
import org.openhab.habclient.OpenHABWidgetControl;
import org.openhab.habclient.OpenHABWidgetProvider;
import org.openhab.habclient.RestCommunication;
import org.openhab.habclient.RoomConfigActivity;
import org.openhab.habclient.RoomConfigFragment;
import org.openhab.habclient.RoomFlipperFragment;
import org.openhab.habclient.RoomProvider;
import org.openhab.habclient.SpeechService;
import org.openhab.habclient.UnitContainerView;
import org.openhab.habclient.UnitPlacementFragment;
import org.openhab.habclient.command.CommandAnalyzer;
import org.openhab.habclient.command.ICommandAnalyzer;
import org.openhab.habclient.rule.OperatorSelectionDialogFragment;
import org.openhab.habclient.rule.RuleActionDialogFragment;
import org.openhab.habclient.rule.RuleActionFragment;
import org.openhab.habclient.rule.RuleEditActivity;
import org.openhab.habclient.rule.RuleOperandDialogFragment;
import org.openhab.habclient.rule.RuleOperationFragment;
import org.openhab.habclient.rule.UnitOperandSelectionDialogFragment;
import org.openhab.habclient.wear.WearCommandHost;
import org.openhab.habdroid.ui.IWidgetTypeLayoutProvider;
import org.openhab.habdroid.ui.OpenHABMainActivity;
import org.openhab.habdroid.ui.OpenHABWidgetListActivity;
import org.openhab.habdroid.ui.OpenHABWidgetListFragment;
import org.openhab.habdroid.ui.WidgetTypeLayoutProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = DomainModule.class,
injects = {
        OpenHABMainActivity.class,
        OpenHABWidgetListActivity.class,
        OpenHABWidgetListFragment.class,
        MainActivity.class,
        RoomConfigActivity.class,
        RoomFlipperFragment.class,
        RoomConfigFragment.class,
        UnitPlacementFragment.class,
        OperatorSelectionDialogFragment.class,
        RuleActionDialogFragment.class,
        RuleActionFragment.class,
        RuleOperationFragment.class,
        RuleEditActivity.class,
        UnitOperandSelectionDialogFragment.class,
        GraphicUnitWidget.class, //TODO: create adapter for this instead
        UnitContainerView.class, //TODO: create adapter for this instead
        RuleOperandDialogFragment.class,
        SpeechService.class,
        WearCommandHost.class,
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
    public IRuleOperationProvider provideRuleOperationProvider(RuleOperationProvider provider) {
        return provider;
    }

    @Provides @Singleton
    public IRoomProvider provideRoomProvider(RoomProvider roomProvider) {
        return roomProvider;
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

    @Provides @Singleton
    public IWearCommandHost provideWearCommandHost(WearCommandHost wearCommandHost) { return wearCommandHost; }
}
