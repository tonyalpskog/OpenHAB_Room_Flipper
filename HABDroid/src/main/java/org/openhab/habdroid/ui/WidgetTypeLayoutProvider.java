package org.openhab.habdroid.ui;

import org.openhab.domain.model.OpenHABWidgetType;
import org.openhab.habdroid.R;

import java.util.HashMap;

public class WidgetTypeLayoutProvider implements IWidgetTypeLayoutProvider {
    private static final HashMap<OpenHABWidgetType, WidgetTypeResources> sResources = new HashMap<OpenHABWidgetType, WidgetTypeResources>();
    static {
        sResources.put(OpenHABWidgetType.GenericItem, new WidgetTypeResources(R.layout.openhabwidgetlist_genericitem, -1));
        sResources.put(OpenHABWidgetType.Root, new WidgetTypeResources(R.layout.openhabwidgetlist_genericitem, -1));
        sResources.put(OpenHABWidgetType.Frame, new WidgetTypeResources(R.layout.openhabwidgetlist_frameitem, -1));
        sResources.put(OpenHABWidgetType.Group, new WidgetTypeResources(R.layout.openhabwidgetlist_groupitem, -1));
        sResources.put(OpenHABWidgetType.Switch, new WidgetTypeResources(R.layout.openhabwidgetlist_switchitem, R.layout.openhabwidget_control_switchitem));
        sResources.put(OpenHABWidgetType.ItemText, new WidgetTypeResources(R.layout.openhabwidgetlist_textitem, R.layout.openhabwidget_control_textitem));
        sResources.put(OpenHABWidgetType.SitemapText, new WidgetTypeResources(R.layout.openhabwidgetlist_textitem, R.layout.openhabwidget_control_textitem));
        sResources.put(OpenHABWidgetType.Slider, new WidgetTypeResources(R.layout.openhabwidgetlist_slideritem, R.layout.openhabwidget_control_slideritem));
        sResources.put(OpenHABWidgetType.Image, new WidgetTypeResources(R.layout.openhabwidgetlist_imageitem, -1));
        sResources.put(OpenHABWidgetType.Selection, new WidgetTypeResources(R.layout.openhabwidgetlist_selectionitem, -1));
        sResources.put(OpenHABWidgetType.SelectionSwitch, new WidgetTypeResources(R.layout.openhabwidgetlist_sectionswitchitem, -1));
        sResources.put(OpenHABWidgetType.RollerShutter, new WidgetTypeResources(R.layout.openhabwidgetlist_rollershutteritem, -1));
        sResources.put(OpenHABWidgetType.Setpoint, new WidgetTypeResources(R.layout.openhabwidgetlist_setpointitem, -1));
        sResources.put(OpenHABWidgetType.Chart, new WidgetTypeResources(R.layout.openhabwidgetlist_chartitem, -1));
        sResources.put(OpenHABWidgetType.Video, new WidgetTypeResources(R.layout.openhabwidgetlist_videoitem, -1));
        sResources.put(OpenHABWidgetType.Web, new WidgetTypeResources(R.layout.openhabwidgetlist_webitem, -1));
        sResources.put(OpenHABWidgetType.Color, new WidgetTypeResources(R.layout.openhabwidgetlist_coloritem, -1));
    }

    public WidgetTypeLayoutProvider() {

    }

    @Override
    public int getRowLayoutId(OpenHABWidgetType type) {
        final WidgetTypeResources resources = sResources.get(type);
        if(resources == null)
            return -1;

        return resources.getRowLayoutId();
    }

    @Override
    public int getControlLayoutId(OpenHABWidgetType type) {
        final WidgetTypeResources resources = sResources.get(type);
        if(resources == null)
            return -1;

        return resources.getControlLayoutId();
    }

    private static class WidgetTypeResources {
        private final int rowLayoutId;
        private final int controlLayoutId;

        public WidgetTypeResources(int rowLayoutId, int controlLayoutId) {

            this.rowLayoutId = rowLayoutId;
            this.controlLayoutId = controlLayoutId;
        }

        public int getRowLayoutId() {
            return rowLayoutId;
        }

        public int getControlLayoutId() {
            return controlLayoutId;
        }
    }
}
