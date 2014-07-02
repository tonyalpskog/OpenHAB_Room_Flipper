package org.openhab.domain;

import org.openhab.domain.command.WidgetPhraseMatchResult;
import org.openhab.domain.model.OpenHABItemType;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.model.OpenHABWidgetDataSource;
import org.openhab.domain.model.OpenHABWidgetType;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IOpenHABWidgetProvider {
    List<OpenHABWidget> getWidgetList(OpenHABWidgetType type);

    OpenHABWidget getWidgetByItemName(String itemName);

    void setOpenHABWidgets(OpenHABWidgetDataSource openHABWidgetDataSource);

    UUID getUpdateUUID();

    List<OpenHABWidget> getWidgetList(Set<OpenHABWidgetType> category);

    List<WidgetPhraseMatchResult> getWidgetByLabel(String searchLabel);

    OpenHABWidget getWidgetByID(String id);

    List<String> getItemNameListByWidgetType(Set<OpenHABWidgetType> widgetTypes);

    List<String> getItemNamesByType(OpenHABItemType type);
}
