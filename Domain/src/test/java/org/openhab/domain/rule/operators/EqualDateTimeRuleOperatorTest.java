package org.openhab.domain.rule.operators;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EqualDateTimeRuleOperatorTest {
    public DateTimeRuleOperator<Date> mSut;

    @Before
    public void setUp() {
        mSut = new EqualDateTimeRuleOperator();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOperationResult_givenLessThanTwoValues_throwIllegalArgumentException() throws ParseException {
        mSut.getOperationResult(mSut.parseValue("12:15"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOperationResult_givenMoreThanTwoValues_throwIllegalArgumentException() throws ParseException {
        mSut.getOperationResult(mSut.parseValue("12:15"), mSut.parseValue("13:30"), mSut.parseValue("14:45"));
    }

    @Test
    public void getOperationResult_givenTheSameTime_returnTheExpectedValue() throws ParseException {
        assertThat(mSut.getOperationResult(mSut.parseValue("11:30"), mSut.parseValue("11:30")), is(equalTo(true)));
    }

    @Test
    public void getOperationResult_givenTimeBefore_returnFalse() throws ParseException {
        assertThat(mSut.getOperationResult(mSut.parseValue("10:15"), mSut.parseValue("11:30")), is(equalTo(false)));
    }

    @Test
    public void getOperationResult_givenTimeAfter_returnFalse() throws ParseException {
        assertThat(mSut.getOperationResult(mSut.parseValue("11:30"), mSut.parseValue("10:15")), is(equalTo(false)));
    }

    @Test
    public void getOperationResult_givenTheSameDateTime_returnTheExpectedValue() throws ParseException {
        assertThat(mSut.getOperationResult(mSut.parseValue("11:30 2014-03-24"), mSut.parseValue("11:30 2014-03-24")), is(equalTo(true)));
    }

    @Test
    public void getOperationResult_givenDateTimeTimeBefore_returnFalse() throws ParseException {
        assertThat(mSut.getOperationResult(mSut.parseValue("10:15 2014-03-24"), mSut.parseValue("11:30 2014-03-24")), is(equalTo(false)));
    }

    @Test
    public void getOperationResult_givenDateTimeDateBefore_returnFalse() throws ParseException {
        assertThat(mSut.getOperationResult(mSut.parseValue("10:15 2014-03-24"), mSut.parseValue("10:15 2014-03-25")), is(equalTo(false)));
    }

    @Test
    public void getOperationResult_givenDateTimeTimeAfter_returnFalse() throws ParseException {
        assertThat(mSut.getOperationResult(mSut.parseValue("11:30 2014-03-24"), mSut.parseValue("10:15 2014-03-24")), is(equalTo(false)));
    }

    @Test
    public void getOperationResult_givenDateTimeDateAfter_returnFalse() throws ParseException {
        assertThat(mSut.getOperationResult(mSut.parseValue("10:15 2014-03-25"), mSut.parseValue("10:15 2014-03-24")), is(equalTo(false)));
    }

    @Test
    public void getOperationResult_givenTheSameWeekDayTime_returnTheExpectedValue() throws ParseException {
        assertThat(mSut.getOperationResult(mSut.parseValue("Måndag 11:30"), mSut.parseValue("Måndag 11:30")), is(equalTo(false)));
    }
}
