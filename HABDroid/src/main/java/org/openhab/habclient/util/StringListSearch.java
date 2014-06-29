package org.openhab.habclient.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class StringListSearch {
    int mMinSearchWordLength;
    String mWordDelimiter;

    public StringListSearch(int minSearchWordLength, String wordDelimiter) {
        mMinSearchWordLength = minSearchWordLength;
        mWordDelimiter = wordDelimiter;
    }

    public boolean isSearchPhraseLegal(String phrase) {
        String[] splittedPhrase = phrase.split(mWordDelimiter);
        for (String word : splittedPhrase) {
            if(word.length() < mMinSearchWordLength)
                return false;
        }
        return true;
    }

    public List<String> getFilteredArray(final List<String> sourceList, String filter) {
        List<String> result = new ArrayList<String>();
        String[] filterItems = filter.split(mWordDelimiter);

        for (String listItem : sourceList) {
            boolean isFound = true;
            for (String filterWord : filterItems) {
                if(filterWord.length() < mMinSearchWordLength)
                    continue;
                if (filterWord.length() <= listItem.length()) {
                    if (!listItem.toLowerCase().contains(filterWord.toLowerCase().trim())) {
                        isFound = false;
                    }
                } else {
                    isFound = false;
                }
            }
            if(isFound)
                result.add(listItem);
        }

        return result;
    }
}
