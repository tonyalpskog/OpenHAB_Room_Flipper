package org.openhab.habclient.dagger;

import org.openhab.domain.DocumentFactory;
import org.openhab.domain.IDocumentFactory;
import org.openhab.domain.IOpenHABWidgetControl;
import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.IPopularNameProvider;
import org.openhab.domain.IUnitEntityDataTypeProvider;
import org.openhab.domain.PopularNameProvider;
import org.openhab.domain.UnitEntityDataTypeProvider;
import org.openhab.domain.command.ICommandColorProvider;
import org.openhab.domain.command.ICommandPhrasesProvider;
import org.openhab.domain.rule.IRuleOperationProvider;
import org.openhab.domain.rule.RuleOperationProvider;
import org.openhab.domain.rule.IRuleProvider;
import org.openhab.domain.rule.RuleProvider;
import org.openhab.domain.util.IColorParser;
import org.openhab.domain.util.ILogger;
import org.openhab.domain.wear.IWearCommandHost;
import org.openhab.habclient.AndroidLogger;
import org.openhab.domain.ApplicationModeProvider;
import org.openhab.habclient.ColorParser;
import org.openhab.habclient.GraphicUnitWidget;
import org.openhab.domain.IApplicationModeProvider;
import org.openhab.habclient.IOpenHABSetting;
import org.openhab.domain.IRestCommunication;
import org.openhab.habclient.IRoomDataContainer;
import org.openhab.habclient.IRoomImageProvider;
import org.openhab.domain.IRoomProvider;
import org.openhab.habclient.MainActivity;
import org.openhab.habclient.OpenHABSetting;
import org.openhab.habclient.OpenHABWidgetControl;
import org.openhab.domain.OpenHABWidgetProvider;
import org.openhab.habclient.RestCommunication;
import org.openhab.habclient.RoomConfigActivity;
import org.openhab.habclient.RoomConfigFragment;
import org.openhab.habclient.RoomDataContainer;
import org.openhab.habclient.RoomFlipperFragment;
import org.openhab.habclient.RoomImageProvider;
import org.openhab.habclient.RoomProvider;
import org.openhab.habclient.SpeechService;
import org.openhab.habclient.UnitContainerView;
import org.openhab.habclient.UnitPlacementFragment;
import org.openhab.domain.command.CommandAnalyzer;
import org.openhab.domain.command.ICommandAnalyzer;
import org.openhab.habclient.command.CommandColorProvider;
import org.openhab.habclient.command.CommandPhrasesProvider;
import org.openhab.habclient.rule.AdapterProvider;
import org.openhab.habclient.rule.IAdapterProvider;
import org.openhab.habclient.rule.OperatorSelectionDialogFragment;
import org.openhab.habclient.rule.RuleActionDialogFragment;
import org.openhab.habclient.rule.RuleActionFragment;
import org.openhab.habclient.rule.RuleEditActivity;
import org.openhab.habclient.rule.RuleListActivity;
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
        RuleListActivity.class,
        UnitOperandSelectionDialogFragment.class,
        GraphicUnitWidget.class, //TODO: create adapter for this instead
        UnitContainerView.class, //TODO: create adapter for this instead
        RuleOperandDialogFragment.class,
        SpeechService.class,
        AdapterProvider.class,
        WearCommandHost.class
})
public class ClientModule {
    @Provides @Singleton
    public ICommandColorProvider provideCommandColorProvider(CommandColorProvider provider) {
        return provider;
    }
    
    @Provides @Singleton
    public ICommandPhrasesProvider provideCommandPhrasesProvider(CommandPhrasesProvider provider) {
        return provider;
    }

    @Provides @Singleton
    public IDocumentFactory provideDocumentFactory(DocumentFactory documentFactory) {
        return documentFactory;
    }

    @Provides @Singleton
    public IPopularNameProvider providePopularNameProvider(PopularNameProvider popularNameProvider) {
        return popularNameProvider;
    }

    @Provides @Singleton
    public IRoomImageProvider provideRoomImageProvider(RoomImageProvider roomImageProvider) {
        return roomImageProvider;
    }

    @Provides @Singleton
    public IApplicationModeProvider provideApplicationModeProvider(ApplicationModeProvider provider) {
        return provider;
    }

    @Provides @Singleton
    public IRoomDataContainer provideRoomDataContainer(RoomDataContainer roomDataContainer)  {
        return roomDataContainer;
    }

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
    public IRuleProvider provideRuleProvider(RuleProvider provider) { return provider; }

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

    @Provides @Singleton
    public IUnitEntityDataTypeProvider provideUnitEntityDataTypeProvider(UnitEntityDataTypeProvider provider) { return provider; }

    @Provides @Singleton
    public IAdapterProvider provideAdapterProvider(AdapterProvider provider) { return provider; }
}
