package org.openhab.test.habclient.command;

import android.content.Context;

import org.openhab.domain.IOpenHABWidgetControl;
import org.openhab.domain.IPopularNameProvider;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.util.IRegularExpression;
import org.openhab.habclient.ApplicationMode;
import org.openhab.habclient.IRoomProvider;
import org.openhab.domain.OpenHABWidgetProvider;
import org.openhab.habclient.Room;
import org.openhab.habclient.command.CommandAnalyzer;
import org.openhab.habclient.command.CommandPhraseMatchResult;
import org.openhab.domain.command.WidgetPhraseMatchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Tony Alpskog in 2014.
 */
@Singleton
public class CommandAnalyzerWrapper extends CommandAnalyzer {
    @Inject
    public CommandAnalyzerWrapper(IRoomProvider roomProvider,
                                  OpenHABWidgetProvider openHABWidgetProvider, Context context,
                                  IOpenHABWidgetControl widgetControl,
                                  IRegularExpression regularExpression,
                                  IPopularNameProvider popularNameProvider) {
        super(roomProvider, openHABWidgetProvider, context, widgetControl, regularExpression, popularNameProvider);
    }

    public List<Room> getRoomsFromPhrases(ArrayList<String> speechResult, ApplicationMode applicationMode) {
        return super.getRoomsFromPhrases(speechResult, applicationMode);
    }

    public List<OpenHABWidget> getListOfWidgetsFromListOfRooms(List<Room> listOfRooms) {
        return super.getListOfWidgetsFromListOfRooms(listOfRooms);
    }

    public List<OpenHABWidget> getUnitsFromPhrases(List<String> commandPhrases, List<Room> listOfRooms) {
        return super.getUnitsFromPhrases2(commandPhrases);
    }

    @Override
    public Map<CommandPhraseMatchResult, WidgetPhraseMatchResult> getHighestWidgetsFromCommandMatchResult(List<CommandPhraseMatchResult> listOfCommandResult) {
        return super.getHighestWidgetsFromCommandMatchResult(listOfCommandResult);
    }

    @Override
    public String getRegExMatch(String source, Pattern pattern) {
        return super.getRegExMatch(source, pattern);
    }
}
