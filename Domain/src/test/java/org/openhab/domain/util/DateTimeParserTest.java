package org.openhab.domain.util;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DateTimeParserTest {
    private DateTimeParser mSut;
    
    @Before
    public void setUp() {
        mSut = new DateTimeParser();
    }

    @Test(expected = ParseException.class)
    public void parseValue_givenUnparsableTime_throwParseException() throws ParseException {
        mSut.parseValue("12.15");
    }

    @Test(expected = ParseException.class)
    public void parseValue_givenUnparsableDate_throwParseException() throws ParseException {
        mSut.parseValue("2014/03/02");
    }

    @Test
    public void parseValue_givenValidTime_returnSameDate() throws ParseException {
        final Date expected = getDate(1970, 0, 1, 12, 15, 0, 0);
        final Date actual = mSut.parseValue("12:15");
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void parseValue_givenValidDate_returnSameDate() throws ParseException {
        final Date expected = getDate(2014, 2, 2, 0, 0, 0, 0);
        final Date actual = mSut.parseValue("2014-03-02");
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void parseValue_givenValidTimeAndDate_returnSameDate() throws ParseException {
        final Date expected = getDate(2014, 2, 2, 20, 39, 0, 0);
        final Date actual = mSut.parseValue("20:39 2014-03-02");
        assertThat(actual, is(equalTo(expected)));
    }

    private Date getDate(Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer second, Integer ms) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new java.util.Date());
        if(year != null) cal.set(Calendar.YEAR, year);
        if(month != null) cal.set(Calendar.MONTH, month);
        if(day != null) cal.set(Calendar.DAY_OF_MONTH, day);
        if(hour != null) cal.set(Calendar.HOUR_OF_DAY, hour);
        if(minute != null) cal.set(Calendar.MINUTE, minute);
        if(second != null) cal.set(Calendar.SECOND, second);
        if(ms != null) cal.set(Calendar.MILLISECOND, ms);
        return cal.getTime();
    }
}
