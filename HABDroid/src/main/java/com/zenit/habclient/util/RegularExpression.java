package com.zenit.habclient.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RegularExpression {

    public RegularExpression() {
    }

    public RegExResult getAllNextMatchAsList(String regEx, String target, boolean caseInsensitive) {
        RegExResult regExResult = new RegExResult();
        Matcher matcher = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE).matcher(target);
        regExResult.Matcher = matcher;
        regExResult.GroupList = new ArrayList<String>();
        while(matcher.find())
            for(int i = 1; i <= matcher.groupCount(); i++)
                if(matcher.group(i) != null && !matcher.group(i).isEmpty())
                    regExResult.GroupList.add(matcher.group(i));
        return regExResult;
    }

    public RegExAccuracyResult getStringMatchAccuracy(List<String> sourceWordsList, String target) {
        double wordCountAccuracy = 0;
        double orderAccuracy = 0;
        double lengthDifferenceAccuracy = 0;
        int totalMatchLength = 0;

        String regEx = getRegExStringForMatchAccuracySource(sourceWordsList);

        List<String> matchingWords = new ArrayList<String>();
        target = target.toUpperCase();
        RegExResult regExResult = getAllNextMatchAsList(regEx, target, true);
        wordCountAccuracy = Double.valueOf(regExResult.GroupList.size()) / Double.valueOf(sourceWordsList.size());
        int lastListMatchIndex = -1;
        for (int i = 0; i < regExResult.GroupList.size(); i++) {
            //TODO - Call LevenshteinDistance for all non-regex-matching strings
            totalMatchLength += regExResult.GroupList.get(i).length() + 1;
            int listMatchIndex = sourceWordsList.indexOf(regExResult.GroupList.get(i));
            if(listMatchIndex == -1) {
                for(int sourceIndex = 0; sourceIndex < sourceWordsList.size(); sourceIndex++) {
                    if (regExResult.GroupList.get(i).toUpperCase().contains(sourceWordsList.get(sourceIndex).toUpperCase())) {
                        listMatchIndex = sourceIndex;
                        break;
                    }
                }
            }
            matchingWords.add(sourceWordsList.get(listMatchIndex));
            if (listMatchIndex > lastListMatchIndex) {
                lastListMatchIndex = listMatchIndex;
                orderAccuracy++;
                continue;
            }
        }
        if (orderAccuracy > 0)
            orderAccuracy = orderAccuracy / Double.valueOf(sourceWordsList.size());

        totalMatchLength -= 1;
        if(target.length() > 0)
            lengthDifferenceAccuracy = totalMatchLength > target.length()? Double.valueOf(target.length()) / Double.valueOf(totalMatchLength) : Double.valueOf(totalMatchLength) / Double.valueOf(target.length());
        if(lengthDifferenceAccuracy < 0)
            lengthDifferenceAccuracy = 0;

        return new RegExAccuracyResult(matchingWords, sourceWordsList.size() > 0? (wordCountAccuracy + orderAccuracy + lengthDifferenceAccuracy) / 3 : 0);
    }

    public String getRegExStringForMatchAccuracySource(String[] source) {
        StringBuilder sb = new StringBuilder();
        for(String partOfLabel : source)
            sb.append((sb.length() > 1? "|" : "") + "(" + partOfLabel.toUpperCase() + ")");

        return sb.toString();
    }

    public String getRegExStringForMatchAccuracySource(List<String> source) {
        return getRegExStringForMatchAccuracySource(source.toArray(new String[0]));
    }
}
