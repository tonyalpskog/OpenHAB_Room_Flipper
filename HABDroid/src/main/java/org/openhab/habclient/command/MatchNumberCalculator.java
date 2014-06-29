package org.openhab.habclient.command;

import java.util.regex.Matcher;

/**
 * Created by Tony Alpskog in 2014.
 */
public class MatchNumberCalculator implements IMatchAccuracyCalculator {
    @Override
    public double getAccuracy(String source, Matcher matcher, String target) {
        int sourceCount = source.split(" ").length;
        return matcher.groupCount() / sourceCount;
    }
}
