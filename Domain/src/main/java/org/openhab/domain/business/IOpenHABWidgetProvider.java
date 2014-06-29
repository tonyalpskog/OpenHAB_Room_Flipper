package org.openhab.domain.business;

import org.openhab.domain.model.OpenHABWidget;

public interface IOpenHABWidgetProvider {
    OpenHABWidget getWidgetByItemName(String itemName);
}
