package org.openhab.domain.rule.operators;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NotEqualNumberRuleOperatorTest {
    private NotEqualNumberRuleOperator mSut;

    @Before
    public void setUp() {
        mSut = new NotEqualNumberRuleOperator();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOperationResult_givenOneArgument_throwIllegalArgumentException() {
        mSut.getOperationResult(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOperationResult_givenMoreThanTwoArguments_throwIllegalArgumentException() {
        mSut.getOperationResult(1, 2, 3);
    }

    @Test
    public void getOperationResult_givenSameIntegers_returnFalse() {
        assertThat(mSut.getOperationResult(10, 10), is(equalTo(false)));
    }

    @Test
    public void getOperationResult_givenSameNumbersWithDoubleAndFloat_returnFalse() {
        assertThat(mSut.getOperationResult(10.1d, 10.1f), is(equalTo(false)));
    }

    @Test
    public void getOperationResult_givenDifferentIntegers_returnTrue() {
        assertThat(mSut.getOperationResult(9, 10), is(equalTo(true)));
    }

    @Test
    public void getOperationResult_givenSameFloats_returnFalse() {
        assertThat(mSut.getOperationResult(9f, 9f), is(equalTo(false)));
    }
    
    @Test
    public void getOperationResult_givenDifferentFloats_returnTrue() {
        assertThat(mSut.getOperationResult(34.7f, 9f), is(equalTo(true)));
    }

    @Test
    public void getOperationResult_givenSameDoubles_returnFalse() {
        assertThat(mSut.getOperationResult(10.2d, 10.2d), is(equalTo(false)));
    }

    @Test
    public void getOperationResult_givenDifferentDoubles_returnFalse() {
        assertThat(mSut.getOperationResult(10.2d, 10.1d), is(equalTo(true)));
    }
}
