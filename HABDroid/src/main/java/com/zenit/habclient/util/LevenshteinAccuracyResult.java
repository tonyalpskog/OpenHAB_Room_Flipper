package com.zenit.habclient.util;

import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class LevenshteinAccuracyResult {
    List<String> matchingWords;
    double accuracy;

    public LevenshteinAccuracyResult(List<String> matchingWords, double accuracy) {
        this.matchingWords = matchingWords;
        this.accuracy = accuracy;
    }

    public List<String> getMatchingWords() {
        return matchingWords;
    }

    public double getAccuracy() {
        return accuracy;
    }
}
