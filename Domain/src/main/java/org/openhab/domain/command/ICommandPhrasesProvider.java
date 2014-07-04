package org.openhab.domain.command;

public interface ICommandPhrasesProvider {
    String[] getCommandPhrases(OpenHABWidgetCommandType commandType);
}
