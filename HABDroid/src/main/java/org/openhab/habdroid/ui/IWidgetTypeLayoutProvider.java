package org.openhab.habdroid.ui;

import org.openhab.domain.model.OpenHABWidgetType;

public interface IWidgetTypeLayoutProvider {
    int getRowLayoutId(OpenHABWidgetType widgetType);
    int getControlLayoutId(OpenHABWidgetType type);
}
