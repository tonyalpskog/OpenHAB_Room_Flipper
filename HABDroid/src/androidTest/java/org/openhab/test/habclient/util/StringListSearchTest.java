package org.openhab.test.habclient.util;

import android.test.InstrumentationTestCase;

import org.openhab.habclient.util.StringListSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class StringListSearchTest  extends InstrumentationTestCase {

    public void test_dev_split() {
        String[] splittedString = "hello how\tare   you".split("\\s+");

        assertEquals(4, splittedString.length);
        assertEquals("hello", splittedString[0]);
        assertEquals("how", splittedString[1]);
        assertEquals("are", splittedString[2]);
        assertEquals("you", splittedString[3]);
    }

    public void test_isSearchPhraseLegal() {
        StringListSearch stringListSearch = new StringListSearch(3, "\\s+");

        assertEquals(true, stringListSearch.isSearchPhraseLegal("hello how  \tare  you"));
    }

    public void test_getFilteredArray() {
        StringListSearch stringListSearch = new StringListSearch(2, "\\s+");

        List<String> sourceList = new ArrayList<String>();
        sourceList.add("Hello");
        sourceList.add("There");
        sourceList.add("Everyone");
        sourceList.add("How");
        sourceList.add("Are");
        sourceList.add("You");

        List<String> tempList = stringListSearch.getFilteredArray(sourceList, "e");

        //Check the source list - Shall be unfiltered due to minimum word length < 2
        assertEquals(true, tempList.contains("Hello"));
        assertEquals(true, tempList.contains("There"));
        assertEquals(true, tempList.contains("Everyone"));
        assertEquals(true, tempList.contains("How"));
        assertEquals(true, tempList.contains("Are"));
        assertEquals(true, tempList.contains("You"));

        tempList = stringListSearch.getFilteredArray(sourceList, "er");

        assertEquals(false, tempList.contains("Hello"));
        assertEquals(true, tempList.contains("There"));
        assertEquals(true, tempList.contains("Everyone"));
        assertEquals(false, tempList.contains("How"));
        assertEquals(false, tempList.contains("Are"));
        assertEquals(false, tempList.contains("You"));

        tempList = stringListSearch.getFilteredArray(sourceList, "ery");

        assertEquals(false, tempList.contains("Hello"));
        assertEquals(false, tempList.contains("There"));
        assertEquals(true, tempList.contains("Everyone"));
        assertEquals(false, tempList.contains("How"));
        assertEquals(false, tempList.contains("Are"));
        assertEquals(false, tempList.contains("You"));

        tempList = stringListSearch.getFilteredArray(sourceList, "erys");

        assertEquals(false, tempList.contains("Hello"));
        assertEquals(false, tempList.contains("There"));
        assertEquals(false, tempList.contains("Everyone"));
        assertEquals(false, tempList.contains("How"));
        assertEquals(false, tempList.contains("Are"));
        assertEquals(false, tempList.contains("You"));

        tempList = stringListSearch.getFilteredArray(sourceList, "er re");

        assertEquals(false, tempList.contains("Hello"));
        assertEquals(true, tempList.contains("There"));
        assertEquals(false, tempList.contains("Everyone"));
        assertEquals(false, tempList.contains("How"));
        assertEquals(false, tempList.contains("Are"));
        assertEquals(false, tempList.contains("You"));

        tempList = stringListSearch.getFilteredArray(sourceList, "er o re");

        assertEquals(false, tempList.contains("Hello"));
        assertEquals(true, tempList.contains("There"));
        assertEquals(false, tempList.contains("Everyone"));
        assertEquals(false, tempList.contains("How"));
        assertEquals(false, tempList.contains("Are"));
        assertEquals(false, tempList.contains("You"));
    }
}
