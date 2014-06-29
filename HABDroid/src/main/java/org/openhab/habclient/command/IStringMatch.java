package org.openhab.habclient.command;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IStringMatch {
    void add(IMatchAccuracyCalculator accuracyCalculator);
    void remove(IMatchAccuracyCalculator accuracyCalculator);
    double getMatchWithAccuracy(String source, String target);
}
