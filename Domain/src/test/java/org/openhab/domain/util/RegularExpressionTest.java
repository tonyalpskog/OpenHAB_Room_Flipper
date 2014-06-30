package org.openhab.domain.util;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RegularExpressionTest {
    private RegularExpression mRegularExpression;

    @Before
    public void setUp() {
        mRegularExpression = new RegularExpression();
    }

    @Test
    public void test_getAllNextMatchAsList() {
//        Matcher matcher = Pattern.compile("(HEJ|HOPP|ALLIHOPA)").matcher("sds HEJdf HOPP PÅ ALLIHOPA HEJ");
//        assertEquals(true, matcher.find());
//        assertEquals(true, Matcher.);
//        assertEquals("Found group = '" + Matcher.group() + "'", 4, Matcher.groupCount());
//        assertEquals("HEJ HOPP ALLIHOPA", Matcher.group(0));
        RegExResult regExResult = mRegularExpression.getAllNextMatchAsList("(HEJ|HOPP|ALLIHOPA)", "sds HEJdf HOPP PÅ ALLIHOPA HEJ", true);
        Assert.assertEquals(4, regExResult.GroupList.size());
        Assert.assertEquals("HEJ", regExResult.GroupList.get(0));
        Assert.assertEquals("HOPP", regExResult.GroupList.get(1));
        Assert.assertEquals("ALLIHOPA", regExResult.GroupList.get(2));
        Assert.assertEquals("HEJ", regExResult.GroupList.get(3));
    }

    @Test
    public void test_getAllNextMatchAsList2() {
        RegExResult regExResult = mRegularExpression.getAllNextMatchAsList("hello there (.+)!", "hello there <INTEGER>!", true);
        Assert.assertEquals(1, regExResult.GroupList.size());
        Assert.assertEquals("<INTEGER>", regExResult.GroupList.get(0));
    }
}
