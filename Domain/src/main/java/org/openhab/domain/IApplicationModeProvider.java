package org.openhab.domain;

import org.openhab.domain.model.ApplicationMode;

public interface IApplicationModeProvider {
    ApplicationMode getAppMode();

    void setAppMode(ApplicationMode appMode);
}
