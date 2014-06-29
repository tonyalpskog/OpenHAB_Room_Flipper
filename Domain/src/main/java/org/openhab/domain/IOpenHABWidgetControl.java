package org.openhab.domain;

import org.openhab.domain.model.OpenHABItem;

public interface IOpenHABWidgetControl {
    boolean sendItemCommandFromWidget(String widgetId, String command);

    void sendItemCommand(String itemName, String command);

    void sendItemCommand(OpenHABItem item, String command);
}
