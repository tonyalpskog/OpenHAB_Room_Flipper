package org.openhab.domain.rule.operations.number;

import org.junit.Before;
import org.junit.Test;
import org.openhab.domain.rule.UnitEntityDataType;
import org.openhab.domain.rule.operations.number.NumberEqualRuleOperation;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class NumberEqualRuleOperationTest {
    private NumberEqualRuleOperation mSut;
    
    @Before
    public void setUp() {
        mSut = new NumberEqualRuleOperation();
    }

    @Test
    public void getValue_givenOnlyLeftValue_returnFalse() {
        mSut.setLeft(new UnitEntityDataType<Number>(1, Number.class));
        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getValue_givenOnlyRightValue_returnFalse() {
        mSut.setRight(new UnitEntityDataType<Number>(1, Number.class));
        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getValue_givenSameNumbers_returnTrue() {
        mSut.setLeft(new UnitEntityDataType<Number>(10, Number.class));
        mSut.setRight(new UnitEntityDataType<Number>(10, Number.class));
        assertThat(mSut.getValue(), is(equalTo(true)));
    }

    @Test
    public void getValue_givenSameNumbersOfDifferentType_returnTrue() {
        mSut.setLeft(new UnitEntityDataType<Number>(10, Number.class));
        mSut.setRight(new UnitEntityDataType<Number>(10f, Number.class));

        assertThat(mSut.getValue(), is(equalTo(true)));
    }

    @Test
    public void getValue_givenSameNumbersOfDoubleAndFloat_returnTrue() {
        mSut.setLeft(new UnitEntityDataType<Number>(10.1d, Number.class));
        mSut.setRight(new UnitEntityDataType<Number>(10.1d, Number.class));

        assertThat(mSut.getValue(), is(equalTo(true)));
    }

    @Test
    public void getValue_givenDifferentIntegers_returnFalse() {
        mSut.setLeft(new UnitEntityDataType<Number>(9, Number.class));
        mSut.setRight(new UnitEntityDataType<Number>(10, Number.class));

        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getValue_givenSameFloats_returnTrue() {
        mSut.setLeft(new UnitEntityDataType<Number>(9.1f, Number.class));
        mSut.setRight(new UnitEntityDataType<Number>(9.1f, Number.class));

        assertThat(mSut.getValue(), is(equalTo(true)));
    }

    @Test
    public void getValue_givenDifferentFloats_returnFalse() {
        mSut.setLeft(new UnitEntityDataType<Number>(9.1f, Number.class));
        mSut.setRight(new UnitEntityDataType<Number>(3.1f, Number.class));

        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getValue_givenSameDoubles_returnTrue() {
        mSut.setLeft(new UnitEntityDataType<Number>(10.2d, Number.class));
        mSut.setRight(new UnitEntityDataType<Number>(10.2d, Number.class));

        assertThat(mSut.getValue(), is(equalTo(true)));
    }
}
