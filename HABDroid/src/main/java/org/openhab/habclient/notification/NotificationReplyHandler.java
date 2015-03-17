package org.openhab.habclient.notification;

import org.openhab.domain.IApplicationModeProvider;
import org.openhab.domain.INotificationReplyHandler;
import org.openhab.domain.INotificationSender;
import org.openhab.domain.SenderType;
import org.openhab.domain.command.CommandAnalyzerResult;
import org.openhab.domain.command.ICommandAnalyzer;
import org.openhab.habclient.auto.IAutoUnreadConversationManager;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2015.
 */
public class NotificationReplyHandler implements INotificationReplyHandler {
    private final IApplicationModeProvider mApplicationModeProvider;
    private final ICommandAnalyzer mCommandAnalyzer;
    private final INotificationSender mNotificationHost;
    private final IAutoUnreadConversationManager mAutoUnreadConversationManager;

    @Inject
    public NotificationReplyHandler(IApplicationModeProvider applicationModeProvider,
                            ICommandAnalyzer commandAnalyzer,
                            INotificationSender notificationHost,
                            IAutoUnreadConversationManager autoUnreadConversationManager) {
        if(applicationModeProvider == null) throw new IllegalArgumentException("applicationModeProvider is null");
        if(commandAnalyzer == null) throw new IllegalArgumentException("commandAnalyzer is null");
        if(notificationHost == null) throw new IllegalArgumentException("notificationHost is null");
        if(autoUnreadConversationManager == null) throw new IllegalArgumentException("autoUnreadConversationManager is null");
        mApplicationModeProvider = applicationModeProvider;
        mCommandAnalyzer = commandAnalyzer;
        mNotificationHost = notificationHost;
        mAutoUnreadConversationManager = autoUnreadConversationManager;
    }

    @Override
    public void handleReplyMessage(int conversationId, String replyText, long[] vibratePattern) {
        if (replyText == null || replyText.isEmpty())
            return;
        if(mAutoUnreadConversationManager.isOpenHABSystemConversation(conversationId)) {

            //TODO - remove this when Auto simulator is able to reply from user input.
            if (replyText.equals("This is a reply.")) {//Canned reply from Auto simulator
                mNotificationHost.showNotification(SenderType.System, "Command reply", null, "Hello, Android Auto!", vibratePattern);
                return;
            }

            ArrayList<String> replyToBeAnalyzed = new ArrayList<String>(1);
            replyToBeAnalyzed.add(replyText);
//            mApplication.getSpeechResultAnalyzer().analyzeRoomNavigation(replyToBeAnalyzed, HABApplication.getAppMode());
            CommandAnalyzerResult commandAnalyzerResult = mCommandAnalyzer.analyzeCommand(replyToBeAnalyzed, mApplicationModeProvider.getAppMode());
            String commandReplyMessage = commandAnalyzerResult != null ? mCommandAnalyzer.getCommandReply(commandAnalyzerResult) : "Sorry, didn't get that.";
            mNotificationHost.showNotification(SenderType.System, "Command reply", null, commandReplyMessage, vibratePattern);
        } else {
            //TODO - Implement real support for conversations between OpenHAB users.
            mNotificationHost.showNotification(SenderType.User, "Person", null, "Thank you for the reply.", vibratePattern);
        }
    }
}
