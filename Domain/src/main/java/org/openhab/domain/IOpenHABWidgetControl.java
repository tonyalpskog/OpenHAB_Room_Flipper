package org.openhab.domain;

import org.openhab.domain.model.OpenHABItem;
import org.openhab.domain.model.OpenHABWidget;

public interface IOpenHABWidgetControl {
    boolean sendItemCommandFromWidget(String widgetId, String command);

    void sendItemCommand(String itemName, String command);

    void sendItemCommand(OpenHABWidget item, String command);
}
