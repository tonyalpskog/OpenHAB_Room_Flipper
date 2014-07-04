package org.openhab.test.habclient.command;

import org.openhab.domain.IOpenHABWidgetControl;
import org.openhab.domain.IPopularNameProvider;
import org.openhab.domain.IRoomProvider;
import org.openhab.domain.OpenHABWidgetProvider;
import org.openhab.domain.command.CommandAnalyzer;
import org.openhab.domain.command.CommandPhraseMatchResult;
import org.openhab.domain.command.ICommandColorProvider;
import org.openhab.domain.command.ICommandPhrasesProvider;
import org.openhab.domain.command.WidgetPhraseMatchResult;
import org.openhab.domain.model.ApplicationMode;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.model.Room;
import org.openhab.domain.util.ILogger;
import org.openhab.domain.util.IRegularExpression;

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
                                  OpenHABWidgetProvider openHABWidgetProvider,
                                  IOpenHABWidgetControl widgetControl,
                                  IRegularExpression regularExpression,
                                  IPopularNameProvider popularNameProvider,
                                  ICommandPhrasesProvider commandPhrasesProvider,
                                  ILogger logger, ICommandColorProvider commandColorProvider) {
        super(roomProvider, openHABWidgetProvider, widgetControl, regularExpression, popularNameProvider,
                commandPhrasesProvider, logger, commandColorProvider);
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
