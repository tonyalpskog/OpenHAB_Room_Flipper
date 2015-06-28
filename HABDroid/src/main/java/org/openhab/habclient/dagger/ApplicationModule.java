package org.openhab.habclient.dagger;

import org.openhab.domain.ApplicationModeProvider;
import org.openhab.domain.IApplicationModeProvider;
import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.IPopularNameProvider;
import org.openhab.domain.IRoomProvider;
import org.openhab.domain.OpenHABWidgetProvider;
import org.openhab.domain.PopularNameProvider;
import org.openhab.domain.command.CommandAnalyzer;
import org.openhab.domain.command.ICommandAnalyzer;
import org.openhab.domain.rule.IRuleOperatorProvider;
import org.openhab.domain.rule.IRuleProvider;
import org.openhab.domain.rule.RuleOperatorProvider;
import org.openhab.domain.rule.RuleProvider;
import org.openhab.habclient.IOpenHABSetting;
import org.openhab.habclient.IRoomDataContainer;
import org.openhab.habclient.OpenHABSetting;
import org.openhab.habclient.RoomDataContainer;
import org.openhab.habclient.RoomProvider;
import org.openhab.habclient.auto.AutoUnreadConversationManager;
import org.openhab.habclient.auto.IAutoUnreadConversationManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    @Provides @Singleton
    public IApplicationModeProvider provideApplicationModeProvider(ApplicationModeProvider provider) {
        return provider;
    }

    @Provides @Singleton
    public IRoomDataContainer provideRoomDataContainer(RoomDataContainer roomDataContainer)  {
        return roomDataContainer;
    }

    @Provides @Singleton
    public ICommandAnalyzer provideCommandAnalyzer(CommandAnalyzer commandAnalyzer) {
        return commandAnalyzer;
    }

    @Provides @Singleton
    public IRuleOperatorProvider provideRuleOperatorProvider(RuleOperatorProvider provider) {
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

    @Provides
    public IPopularNameProvider providePopularNameProvider(PopularNameProvider popularNameProvider) {
        return popularNameProvider;
    }

    @Provides @Singleton
    public IOpenHABWidgetProvider provideOpenHABWidgetProvider(OpenHABWidgetProvider provider) {
        return provider;
    }

    @Provides @Singleton
    public IAutoUnreadConversationManager provideAutoUnreadConversationManager(
            AutoUnreadConversationManager autoUnreadConversationManager) {
        return autoUnreadConversationManager;
    }

    @Provides @Singleton
    public IRuleProvider provideRuleProvider(RuleProvider provider) { return provider; }

}
