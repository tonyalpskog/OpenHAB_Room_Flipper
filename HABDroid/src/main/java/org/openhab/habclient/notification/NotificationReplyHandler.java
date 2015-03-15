package org.openhab.habclient.notification;

import org.openhab.domain.IApplicationModeProvider;
import org.openhab.domain.INotificationReplyHandler;
import org.openhab.domain.INotificationSender;
import org.openhab.domain.SenderType;
import org.openhab.domain.command.CommandAnalyzerResult;
import org.openhab.domain.command.ICommandAnalyzer;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2015.
 */
public class NotificationReplyHandler implements INotificationReplyHandler {
    private final IApplicationModeProvider mApplicationModeProvider;
    private final ICommandAnalyzer mCommandAnalyzer;
    private final INotificationSender mNotificationHost;

    @Inject
    public NotificationReplyHandler(IApplicationModeProvider applicationModeProvider,
                            ICommandAnalyzer commandAnalyzer,
                            INotificationSender notificationHost) {
        mApplicationModeProvider = applicationModeProvider;
        mCommandAnalyzer = commandAnalyzer;
        mNotificationHost = notificationHost;
    }

    @Override
    public void handleReplyMessage(String replyText, long[] vibratePattern) {
        ArrayList<String> replyToBeAnalyzed = new ArrayList<String>(1);
        replyToBeAnalyzed.add(replyText);
//            mApplication.getSpeechResultAnalyzer().analyzeRoomNavigation(replyToBeAnalyzed, HABApplication.getAppMode());
        CommandAnalyzerResult commandAnalyzerResult = mCommandAnalyzer.analyzeCommand(replyToBeAnalyzed, mApplicationModeProvider.getAppMode());
        String commandReplyMessage =  commandAnalyzerResult != null? mCommandAnalyzer.getCommandReply(commandAnalyzerResult) : "Sorry, didn't get that.";
        mNotificationHost.showNotification(SenderType.System, "Command reply", null, commandReplyMessage, vibratePattern);
    }
}
