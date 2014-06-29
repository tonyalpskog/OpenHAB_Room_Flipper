package org.openhab.domain;

import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.model.OpenHABWidgetDataSource;

public interface IOpenHABWidgetProvider {
    OpenHABWidget getWidgetByItemName(String itemName);

    void setOpenHABWidgets(OpenHABWidgetDataSource openHABWidgetDataSource);
}
