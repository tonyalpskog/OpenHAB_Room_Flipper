package org.openhab.domain;

import org.openhab.domain.util.StringHandler;

import javax.inject.Inject;

public class PopularNameProvider implements IPopularNameProvider {
    @Inject
    public PopularNameProvider() {
    }

    @Override
    public String getPopularNameFromWidgetLabel(String openHABWidgetLabel) {
        return StringHandler.replaceSubStrings(openHABWidgetLabel, "[", "]", "");
    }

}
