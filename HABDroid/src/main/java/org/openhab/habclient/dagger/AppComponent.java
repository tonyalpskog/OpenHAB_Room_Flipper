package org.openhab.habclient.dagger;

import android.content.Context;

import org.openhab.domain.IApplicationModeProvider;
import org.openhab.domain.IEventBus;
import org.openhab.domain.INodeMessageHandler;
import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.IRoomProvider;
import org.openhab.domain.command.ICommandAnalyzer;
import org.openhab.domain.rule.IRuleOperationProvider;
import org.openhab.domain.util.IColorParser;
import org.openhab.domain.util.ILogger;
import org.openhab.habclient.IOpenHABSetting;
import org.openhab.habclient.IRoomDataContainer;
import org.openhab.habclient.auto.IAutoUnreadConversationManager;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {
        AndroidApplicationModule.class,
        EventBusModule.class,
        ApplicationModule.class,
        UtilModule.class,
        WidgetModule.class,
        CommandModule.class
})
@Singleton
public interface AppComponent {
    Context applicationContext();
    IEventBus eventBus();
    IRoomProvider roomProvider();
    IOpenHABWidgetProvider widgetProvider();
    IOpenHABSetting openHABSetting();
    IApplicationModeProvider applicationModeProvider();
    IAutoUnreadConversationManager autoUnreadConversationManager();
    IRuleOperationProvider ruleOperationProvider();
    ILogger logger();
    IColorParser colorParser();
    IRoomDataContainer roomDataContainer();
    ICommandAnalyzer commandAnalyzer();
    INodeMessageHandler nodeMessageHandler();
}
