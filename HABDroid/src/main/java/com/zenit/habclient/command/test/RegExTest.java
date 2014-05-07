package com.zenit.habclient.command.test;

import android.test.InstrumentationTestCase;

import com.zenit.habclient.util.RegExResult;
import com.zenit.habclient.util.RegularExpression;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RegExTest extends InstrumentationTestCase {
    private RegularExpression mRegularExpression;

    public void setUp() {
        mRegularExpression = new RegularExpression();
    }

    public void test_getAllNextMatchAsList() {
//        Matcher matcher = Pattern.compile("(HEJ|HOPP|ALLIHOPA)").matcher("sds HEJdf HOPP PÅ ALLIHOPA HEJ");
//        assertEquals(true, matcher.find());
//        assertEquals(true, Matcher.);
//        assertEquals("Found group = '" + Matcher.group() + "'", 4, Matcher.groupCount());
//        assertEquals("HEJ HOPP ALLIHOPA", Matcher.group(0));
        RegExResult regExResult = mRegularExpression.getAllNextMatchAsList("(HEJ|HOPP|ALLIHOPA)", "sds HEJdf HOPP PÅ ALLIHOPA HEJ", true);
        assertEquals(4, regExResult.GroupList.size());
        assertEquals("HEJ", regExResult.GroupList.get(0));
        assertEquals("HOPP", regExResult.GroupList.get(1));
        assertEquals("ALLIHOPA", regExResult.GroupList.get(2));
        assertEquals("HEJ", regExResult.GroupList.get(3));
    }

    public void test_getAllNextMatchAsList2() {
        RegExResult regExResult = mRegularExpression.getAllNextMatchAsList("hello there (.+)!", "hello there <INTEGER>!", true);
        assertEquals(1, regExResult.GroupList.size());
        assertEquals("<INTEGER>", regExResult.GroupList.get(0));
    }
}
