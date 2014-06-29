package org.openhab.habclient;

import android.graphics.Color;

import org.openhab.domain.util.IColorParser;

public class ColorParser implements IColorParser {
    @Override
    public int parseColor(String color) {
        return Color.parseColor(color);
    }
}
