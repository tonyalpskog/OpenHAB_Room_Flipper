package org.openhab.domain.util;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class ListStringSearchTest {
    @Test
    public void test_dev_split() {
        String[] splittedString = "hello how\tare   you".split("\\s+");

        Assert.assertEquals(4, splittedString.length);
        Assert.assertEquals("hello", splittedString[0]);
        Assert.assertEquals("how", splittedString[1]);
        Assert.assertEquals("are", splittedString[2]);
        Assert.assertEquals("you", splittedString[3]);
    }

    @Test
    public void test_isSearchPhraseLegal() {
        ListStringSearch listStringSearch = new ListStringSearch(3, "\\s+");

        Assert.assertEquals(true, listStringSearch.isSearchPhraseLegal("hello how  \tare  you"));
    }

    @Test
    public void test_getFilteredArray() {
        ListStringSearch listStringSearch = new ListStringSearch(2, "\\s+");

        List<String> sourceList = new ArrayList<String>();
        sourceList.add("Hello");
        sourceList.add("There");
        sourceList.add("Everyone");
        sourceList.add("How");
        sourceList.add("Are");
        sourceList.add("You");

        List<String> tempList = listStringSearch.getFilteredArray(sourceList, "e");

        //Check the source list - Shall be unfiltered due to minimum word length < 2
        Assert.assertEquals(true, tempList.contains("Hello"));
        Assert.assertEquals(true, tempList.contains("There"));
        Assert.assertEquals(true, tempList.contains("Everyone"));
        Assert.assertEquals(true, tempList.contains("How"));
        Assert.assertEquals(true, tempList.contains("Are"));
        Assert.assertEquals(true, tempList.contains("You"));

        tempList = listStringSearch.getFilteredArray(sourceList, "er");

        Assert.assertEquals(false, tempList.contains("Hello"));
        Assert.assertEquals(true, tempList.contains("There"));
        Assert.assertEquals(true, tempList.contains("Everyone"));
        Assert.assertEquals(false, tempList.contains("How"));
        Assert.assertEquals(false, tempList.contains("Are"));
        Assert.assertEquals(false, tempList.contains("You"));

        tempList = listStringSearch.getFilteredArray(sourceList, "ery");

        Assert.assertEquals(false, tempList.contains("Hello"));
        Assert.assertEquals(false, tempList.contains("There"));
        Assert.assertEquals(true, tempList.contains("Everyone"));
        Assert.assertEquals(false, tempList.contains("How"));
        Assert.assertEquals(false, tempList.contains("Are"));
        Assert.assertEquals(false, tempList.contains("You"));

        tempList = listStringSearch.getFilteredArray(sourceList, "erys");

        Assert.assertEquals(false, tempList.contains("Hello"));
        Assert.assertEquals(false, tempList.contains("There"));
        Assert.assertEquals(false, tempList.contains("Everyone"));
        Assert.assertEquals(false, tempList.contains("How"));
        Assert.assertEquals(false, tempList.contains("Are"));
        Assert.assertEquals(false, tempList.contains("You"));

        tempList = listStringSearch.getFilteredArray(sourceList, "er re");

        Assert.assertEquals(false, tempList.contains("Hello"));
        Assert.assertEquals(true, tempList.contains("There"));
        Assert.assertEquals(false, tempList.contains("Everyone"));
        Assert.assertEquals(false, tempList.contains("How"));
        Assert.assertEquals(false, tempList.contains("Are"));
        Assert.assertEquals(false, tempList.contains("You"));

        tempList = listStringSearch.getFilteredArray(sourceList, "er o re");

        Assert.assertEquals(false, tempList.contains("Hello"));
        Assert.assertEquals(true, tempList.contains("There"));
        Assert.assertEquals(false, tempList.contains("Everyone"));
        Assert.assertEquals(false, tempList.contains("How"));
        Assert.assertEquals(false, tempList.contains("Are"));
        Assert.assertEquals(false, tempList.contains("You"));
    }
}
