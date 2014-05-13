package com.zenit.habclient.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class StringHandler {
    public static String getItemArrayAsString(String[] stringArray) {
        StringBuilder sb = new StringBuilder();
        for (String str : stringArray)
            sb.append((sb.length() > 0? ", " : "") + str);
        return sb.toString();
    }

    /**
     * @return all items from sourceList that doesn't exist in the targetList
     */
    public static List<String> getStringListDiff(List<String> sourceList, List<String> targetList) {

        List<String> result = new ArrayList<String>();
        result.addAll(sourceList.subList(0, sourceList.size()));
        result.removeAll(targetList);
        return result;
    }

    public static int getLevenshteinDistance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    public static int getLevenshteinPercent(String a, String b) {
//        String[] aList = a.split(" ");
        Double result = 1.0 - ((double) getLevenshteinDistance(a, b) / ((a.length() + b.length()) / 2));
        if(result > 1.0)
            result = 1.0;
        result *= 100;
        return  result.intValue();
    }

    public static String replaceSubStrings(String source, String beginIncluded, String endIncluded, String replacement) {
        String result = source;
        int firstBeginIndex = result.indexOf(beginIncluded);
        int firstEndIndex = result.indexOf(endIncluded);
        while(firstBeginIndex > -1 && firstEndIndex > -1  && firstBeginIndex < firstEndIndex) {
            result = (firstBeginIndex > 1? result.substring(0, firstBeginIndex) : "") + replacement + (firstEndIndex < result.length()? result.substring(firstEndIndex + 1) : "");
            firstBeginIndex = result.indexOf(beginIncluded);
            firstEndIndex = result.indexOf(endIncluded);
        }
        return result.trim();
    }

    public static boolean isNullOrEmpty(String target) {
        return target == null || target.isEmpty();
    }
}
