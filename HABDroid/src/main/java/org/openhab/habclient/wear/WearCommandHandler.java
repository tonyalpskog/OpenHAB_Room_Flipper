package org.openhab.habclient.wear;

import android.content.Context;
import android.util.Log;

import org.openhab.domain.IApplicationModeProvider;
import org.openhab.domain.INodeMessageHandler;
import org.openhab.domain.IDeviceCommunicator;
import org.openhab.domain.command.CommandAnalyzerResult;
import org.openhab.domain.command.ICommandAnalyzer;
import org.openhab.domain.util.StringHandler;
import org.openhab.habdroid.R;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2015.
 */
public class WearCommandHandler implements INodeMessageHandler {
    private final IApplicationModeProvider mApplicationModeProvider;
    private final ICommandAnalyzer mCommandAnalyzer;
    private final Context mContext;

    @Inject
    public WearCommandHandler(IApplicationModeProvider applicationModeProvider
            , ICommandAnalyzer commandAnalyzer
            , Context context) {
        if(applicationModeProvider == null) throw new IllegalArgumentException("applicationModeProvider is null");
        if(commandAnalyzer == null) throw new IllegalArgumentException("commandAnalyzer is null");
        if(context == null) throw new IllegalArgumentException("context is null");
        mApplicationModeProvider = applicationModeProvider;
        mCommandAnalyzer = commandAnalyzer;
        mContext = context;
    }

    @Override
    public void handleNodeMessage(IDeviceCommunicator nodeCommunicator, String path, String message, String senderNodeId) {
        if(StringHandler.isNullOrEmpty(message) || StringHandler.isNullOrEmpty(senderNodeId)) {
            String commandReplyMessage = mContext.getString(R.string.empty_command);
            nodeCommunicator.sendMessage(WearListenerService.EXTERNAL_WEAR_COMMAND, commandReplyMessage, senderNodeId);
        }

        if(StringHandler.isNullOrEmpty(senderNodeId))
            Log.w("WearCommandHandler", "Missing sender node");

        ArrayList<String> commandToBeAnalyzed = new ArrayList<String>(1);
        commandToBeAnalyzed.add(message);
        CommandAnalyzerResult commandAnalyzerResult = mCommandAnalyzer.analyzeCommand(commandToBeAnalyzed, mApplicationModeProvider.getAppMode());
        String commandReplyMessage = commandAnalyzerResult != null ? mCommandAnalyzer.getCommandReply(commandAnalyzerResult) : mContext.getString(R.string.unknown_command);
        nodeCommunicator.sendMessage(WearListenerService.EXTERNAL_WEAR_COMMAND, commandReplyMessage, senderNodeId);
    }
}
