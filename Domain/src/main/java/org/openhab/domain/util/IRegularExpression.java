package org.openhab.domain.util;

import java.util.List;

public interface IRegularExpression {
    RegExResult getAllNextMatchAsList(String regEx, String target, boolean caseInsensitive);

    RegExAccuracyResult getStringMatchAccuracy(List<String> sourceWordsList, String target);

    String getRegExStringForMatchAccuracySource(String[] source);

    String getRegExStringForMatchAccuracySource(List<String> source);
}
