package com.zenit.habclient.util;

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
}
