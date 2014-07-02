package org.openhab.habclient;

public interface IApplicationModeProvider {
    ApplicationMode getAppMode();

    void setAppMode(ApplicationMode appMode);
}
