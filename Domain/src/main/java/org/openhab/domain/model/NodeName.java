package org.openhab.domain.model;

/**
 * Created by Tony Alpskog in 2014.
 */
public enum NodeName {
    item(0, "item"),
    name(1, "name"),
    state(2, "state"),
    link(3, "link"),
    id(4, "id"),
    title(5, "title"),
    icon(6, "icon"),
    label(7, "label"),
    homepage(8, "homepage"),
    leaf(9, "leaf"),
    linkedPage(10, "linkedPage"),
    widget(11, "widget"),
    widgetId(12, "widgetId"),
    url(13, "url"),
    minValue(14, "minValue"),
    maxValue(15, "maxValue"),
    step(16, "step"),
    refresh(17, "refresh"),
    period(18, "period"),
    height(19, "height"),
    mapping(20, "mapping"),
    command(21, "command"),
    iconcolor(22, "iconcolor"),
    labelcolor(23, "labelcolor"),
    valuecolor(24, "valuecolor"),
    type(25, "type");

    public final String Name;
    public final int Id;

    private NodeName(int id, String name) {
        Id = id;
        Name = name;
    }

}
