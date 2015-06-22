package org.openhab.domain.rule.operators;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AfterOrEqualDateTimeRuleOperatorTest extends AfterDateTimeRuleOperatorTest {

    @Override
    @Before
    public void setUp() {
        mSut = new AfterOrEqualDateTimeRuleOperator();
    }

    @Override
    @Test
    public void getOperationResult_givenTheSameTime_returnTheExpectedValue() throws ParseException {
        assertThat(mSut.getOperationResult(mSut.parseValue("11:30"), mSut.parseValue("11:30")), is(equalTo(true)));
    }

    @Override
    @Test
    public void getOperationResult_givenTheSameWeekDayTime_returnTheExpectedValue() throws ParseException {
        assertThat(mSut.getOperationResult(mSut.parseValue("Måndag 11:30"), mSut.parseValue("Måndag 11:30")), is(equalTo(true)));
    }
}
