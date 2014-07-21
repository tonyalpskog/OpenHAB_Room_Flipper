package org.openhab.domain.rule.operations.number;

import org.junit.Before;
import org.junit.Test;
import org.openhab.domain.rule.UnitEntityDataType;
import org.openhab.domain.rule.operations.number.NumberNotEqualRuleOperation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NumberNotEqualRuleOperationTest {
    private NumberNotEqualRuleOperation mSut;
    
    @Before
    public void setUp() {
        mSut = new NumberNotEqualRuleOperation();
    }

    @Test
    public void getValue_givenOnlyLeft_returnTrue() {
        mSut.setLeft(new UnitEntityDataType<Number>(3, Number.class));
        assertThat(mSut.getValue(), is(equalTo(true)));
    }

    @Test
    public void getValue_givenOnlyRight_returnTrue() {
        mSut.setRight(new UnitEntityDataType<Number>(3, Number.class));
        assertThat(mSut.getValue(), is(equalTo(true)));
    }

    @Test
    public void getValue_givenSameIntegers_returnFalse() {
        mSut.setLeft(new UnitEntityDataType<Number>(10, Number.class));
        mSut.setRight(new UnitEntityDataType<Number>(10, Number.class));
        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getValue_givenSameNumbersWithDoubleAndFloat_returnFalse() {
        mSut.setLeft(new UnitEntityDataType<Number>(10.1d, Number.class));
        mSut.setRight(new UnitEntityDataType<Number>(10.1f, Number.class));
        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getValue_givenDifferentIntegers_returnTrue() {
        mSut.setLeft(new UnitEntityDataType<Number>(9, Number.class));
        mSut.setRight(new UnitEntityDataType<Number>(10, Number.class));
        assertThat(mSut.getValue(), is(equalTo(true)));
    }

    @Test
    public void getValue_givenSameFloats_returnFalse() {
        mSut.setLeft(new UnitEntityDataType<Number>(9f, Number.class));
        mSut.setRight(new UnitEntityDataType<Number>(9f, Number.class));
        assertThat(mSut.getValue(), is(equalTo(false)));
    }
    
    @Test
    public void getValue_givenDifferentFloats_returnTrue() {
        mSut.setLeft(new UnitEntityDataType<Number>(37.f, Number.class));
        mSut.setRight(new UnitEntityDataType<Number>(9f, Number.class));
        assertThat(mSut.getValue(), is(equalTo(true)));
    }

    @Test
    public void getValue_givenSameDoubles_returnFalse() {
        mSut.setLeft(new UnitEntityDataType<Number>(10.2d, Number.class));
        mSut.setRight(new UnitEntityDataType<Number>(10.2d, Number.class));
        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getValue_givenDifferentDoubles_returnFalse() {
        mSut.setLeft(new UnitEntityDataType<Number>(10.2d, Number.class));
        mSut.setRight(new UnitEntityDataType<Number>(10.1d, Number.class));
        assertThat(mSut.getValue(), is(equalTo(true)));
    }
}
