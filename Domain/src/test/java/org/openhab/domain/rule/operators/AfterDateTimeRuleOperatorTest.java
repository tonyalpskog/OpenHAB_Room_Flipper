package org.openhab.domain.rule.operators;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AfterDateTimeRuleOperatorTest {
    private AfterDateTimeRuleOperator mSut;

    @Before
    public void setUp() {
        mSut = new AfterDateTimeRuleOperator();
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
    public void getOperationResult_givenTimeBefore_returnFalse() throws ParseException {
        assertThat(mSut.getOperationResult(mSut.parseValue("10:15"), mSut.parseValue("11:30")), is(equalTo(false)));
    }

    @Test
    public void getOperationResult_givenTimeAfter_returnTrue() throws ParseException {
        assertThat(mSut.getOperationResult(mSut.parseValue("11:30"), mSut.parseValue("10:15")), is(equalTo(true)));
    }
    
    @Test
    public void getOperationResult_givenDateTimeBefore_returnFalse() throws ParseException {
        assertThat(mSut.getOperationResult(mSut.parseValue("11:30 2014-03-24"), mSut.parseValue("10:15 2014-03-25")), is(equalTo(false)));
    }
    
    @Test
    public void getOperationResult_givenDateTimeAfter_returnTrue() throws ParseException {
        final Date first = mSut.parseValue("10:15 2014-03-24");
        final Date second = mSut.parseValue("11:30 2014-03-23");

        assertThat(mSut.getOperationResult(first, second), is(equalTo(true)));
    }
}
