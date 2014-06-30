package org.openhab.habclient;

import android.graphics.Color;

import org.openhab.domain.util.IColorParser;

import javax.inject.Inject;

public class ColorParser implements IColorParser {
    @Inject
    public ColorParser() {
    }

    @Override
    public int parseColor(String color) {
        return Color.parseColor(color);
    }
}
