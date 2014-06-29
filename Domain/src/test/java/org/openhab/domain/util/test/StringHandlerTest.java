package org.openhab.domain.util.test;

import org.junit.Assert;
import org.junit.Test;
import org.openhab.domain.util.StringHandler;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by Tony Alpskog in 2014.
 */
public class StringHandlerTest {
    @Test
    public void test_getStringListDiff() {
        //Arrange
        final List<String> source = Arrays.asList("ABBA", "BEATLES", "CESARS PALACE", "DIANA ROSS");
        final List<String> target = Arrays.asList("CESARS PALACE", "ABBA");

        //Act
        final List<String> result = StringHandler.getStringListDiff(source, target);

        //Assert
        assertThat(result.size(), is(equalTo(2)));
        assertThat(result.get(0), is(equalTo("BEATLES")));
        assertThat(result.get(1), is(equalTo("DIANA ROSS")));
    }

    @Test
    public void test_getLevenshteinDistance() {
        assertThat(StringHandler.getLevenshteinDistance("OUTDOOR", "OUTSIDE"), is(equalTo(4)));
    }

    @Test
    public void test_getLevenshteinPercent() {
        assertThat(StringHandler.getLevenshteinPercent("OUTDOOR", "OUTSIDE"), is(equalTo(42)));
    }

    @Test
    public void test_replaceSubStrings() {
        final String result = StringHandler.replaceSubStrings("One <two> three <four> five", "<", ">", "(.+)");
        assertThat(result, is(equalTo("One (.+) three (.+) five")));
    }

    @Test
    public void replaceSubStrings_givenSwitchOnUnitString_shouldReturnCorrectFormat() {
        final String result2 = StringHandler.replaceSubStrings("Switch on <unit>", "<", ">", "(.+)");
        Assert.assertEquals("Switch on (.+)", result2);
    }

    @Test
    public void isNullOrEmpty_givenStringIsNotEmptyOrNull_shouldReturnFalse() {
        assertThat(StringHandler.isNullOrEmpty("fkl43"), is(false));
    }

    @Test
    public void isNullOrEmpty_givenStringIsEmpty_shouldReturnTrue() {
        assertThat(StringHandler.isNullOrEmpty(""), is(true));
    }

    @Test
    public void isNullOrEmpty_givenStringIsNull_shouldReturnTrue() {
        assertThat(StringHandler.isNullOrEmpty(null), is(true));
    }
}
