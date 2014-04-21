package com.zenit.habclient.command;

import java.util.regex.Matcher;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IMatchAccuracyCalculator {
    double getAccuracy(String source, Matcher matcher, String target);
}
