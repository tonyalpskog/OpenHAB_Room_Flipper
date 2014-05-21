package com.zenit.habclient.util.test;

import android.test.InstrumentationTestCase;

import com.zenit.habclient.util.StringHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class StringHandlerTest extends InstrumentationTestCase {
    public void test_getStringListDiff() {
        List<String> source = new ArrayList<String>();
        source.add("ABBA");
        source.add("BEATLES");
        source.add("CESARS PALACE");
        source.add("DIANA ROSS");
        List<String> target = new ArrayList<String>();
        target.add("CESARS PALACE");
        target.add("ABBA");
        List<String> result = StringHandler.getStringListDiff(source, target);
        assertEquals(4, source.size());
        assertEquals(2, target.size());
        assertEquals(2, result.size());
        assertEquals("BEATLES", result.get(0));
        assertEquals("DIANA ROSS", result.get(1));
    }

    public void test_getLevenshteinDistance() {
        assertEquals(4, StringHandler.getLevenshteinDistance("OUTDOOR", "OUTSIDE"));
    }

    public void test_getLevenshteinPercent() {
        assertEquals(42, StringHandler.getLevenshteinPercent("OUTDOOR", "OUTSIDE"));
    }

    public void test_replaceSubStrings() {
        String result2 = StringHandler.replaceSubStrings("Switch on <unit>", "<", ">", "(.+)");
        assertEquals("Switch on (.+)", result2);

        String result = StringHandler.replaceSubStrings("One <two> three <four> five", "<", ">", "(.+)");
        assertEquals("One (.+) three (.+) five", result);
    }

    public void test_isNullOrEmpty() {
        String testString = "fkl43";
        assertEquals(false, StringHandler.isNullOrEmpty(testString));

        testString = "";
        assertEquals(true, StringHandler.isNullOrEmpty(testString));

        testString = null;
        assertEquals(true, StringHandler.isNullOrEmpty(testString));
    }
}
