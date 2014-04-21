package com.zenit.habclient.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RegularExpression {

    public RegularExpression() {
    }

    public RegExResult getAllNextMatchAsList(String regEx, String target) {
        RegExResult regExResult = new RegExResult();
        Matcher matcher = Pattern.compile(regEx).matcher(target);
        regExResult.Matcher = matcher;
        regExResult.GroupList = new ArrayList<String>();
        while(matcher.find())
            regExResult.GroupList.add(matcher.group());
        return regExResult;
    }
}
