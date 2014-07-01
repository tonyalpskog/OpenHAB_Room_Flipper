package org.openhab.domain.rule.operators;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class EqualNumberRuleOperatorTest {
    private EqualNumberRuleOperator mSut;

    @Before
    public void setUp() {
        mSut = new EqualNumberRuleOperator();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOperationResult_givenOneValue_throwIllegalArgumentException() {
        mSut.getOperationResult(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOperationResult_givenMoreThanTwoValues_throwIllegalArgumentException() {
        mSut.getOperationResult(1, 2, 3);
    }

    @Test
    public void getOperationResult_givenSameNumbers_returnTrue() {
        assertThat(mSut.getOperationResult(10, 10), is(equalTo(true)));
    }

    @Test
    public void getOperationResult_givenSameNumbersOfDifferentType_returnTrue() {
        assertThat(mSut.getOperationResult(10, (float) 10), is(equalTo(true)));
    }

    @Test
    public void getOperationResult_givenSameNumbersOfDoubleAndFloat_returnTrue() {
        assertThat(mSut.getOperationResult(10.1d, 10.1f), is(equalTo(true)));
    }

    @Test
    public void getOperationResult_givenDifferentIntegers_returnFalse() {
        assertThat(mSut.getOperationResult(9, 10), is(equalTo(false)));
    }

    @Test
    public void getOperationResult_givenSameFloats_returnTrue() {
        assertThat(mSut.getOperationResult(9.1f, 9.1f), is(equalTo(true)));
    }

    @Test
    public void getOperationResult_givenDifferentFloats_returnFalse() {
        assertThat(mSut.getOperationResult(9.1f, 3.1f), is(equalTo(false)));
    }

    @Test
    public void getOperationResult_givenSameDoubles_returnTrue() {
        assertThat(mSut.getOperationResult(10.2d, 10.2d), is(equalTo(true)));
    }
}
