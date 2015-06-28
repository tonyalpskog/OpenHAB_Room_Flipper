package org.openhab.domain.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class ListStringSearch<T> {
    int mMinSearchWordLength;
    String mWordDelimiter;

    public ListStringSearch(int minSearchWordLength, String wordDelimiter) {
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

    public List<T> getFilteredArray(final List<T> sourceList, String filter) {
        List<T> result = new ArrayList<T>();
        String[] filterItems = filter.split(mWordDelimiter);

        for (T listItem : sourceList) {
            boolean isFound = true;
            for (String filterWord : filterItems) {
                if(filterWord.length() < mMinSearchWordLength)
                    continue;
                if (filterWord.length() <= listItem.toString().length()) {
                    if (!listItem.toString().toLowerCase().contains(filterWord.toLowerCase().trim())) {
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
