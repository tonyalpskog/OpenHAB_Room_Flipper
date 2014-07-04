package org.openhab.domain;

import org.openhab.domain.model.ApplicationMode;

import javax.inject.Inject;

public class ApplicationModeProvider implements IApplicationModeProvider {
    private ApplicationMode mAppMode = ApplicationMode.Unknown;

    @Inject
    public ApplicationModeProvider() {

    }

    @Override
    public ApplicationMode getAppMode() {
        return mAppMode;
    }

    @Override
    public void setAppMode(ApplicationMode appMode) {
        mAppMode = appMode;
    }
}
