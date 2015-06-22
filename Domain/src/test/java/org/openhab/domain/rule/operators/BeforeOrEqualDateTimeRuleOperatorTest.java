package org.openhab.domain.rule.operators;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BeforeOrEqualDateTimeRuleOperatorTest extends BeforeDateTimeRuleOperatorTest {

    @Override
    @Before
    public void setUp() {
        mSut = new BeforeOrEqualDateTimeRuleOperator();
    }

    @Override
    @Test
    public void getOperationResult_givenTheSameTime_returnTheExpectedValue() throws ParseException {
        assertThat(mSut.getOperationResult(mSut.parseValue("11:30"), mSut.parseValue("11:30")), is(equalTo(true)));
    }
}
