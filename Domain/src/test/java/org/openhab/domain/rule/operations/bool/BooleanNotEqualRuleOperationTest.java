package org.openhab.domain.rule.operations.bool;

import org.junit.Before;
import org.junit.Test;
import org.openhab.domain.rule.UnitEntityDataType;
import org.openhab.domain.rule.operations.NotEqualRuleOperation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BooleanNotEqualRuleOperationTest {
    private NotEqualRuleOperation<Boolean> mSut;

    @Before
    public void setUp() throws Exception {

        mSut = new NotEqualRuleOperation<Boolean>();
    }

    @Test
    public void getValue_givenOnlyRightWithFalse_returnTrue() {
        mSut.setRight(new UnitEntityDataType<Boolean>(false, Boolean.class));
        assertThat(mSut.getValue(), is(equalTo(true)));
    }

    @Test
    public void getValue_givenOnlyLeftWithFalse_returnTrue() {
        mSut.setLeft(new UnitEntityDataType<Boolean>(false, Boolean.class));
        assertThat(mSut.getValue(), is(equalTo(true)));
    }

    @Test
    public void getValue_givenOnlyRightWithTrue_returnTrue() {
        mSut.setRight(new UnitEntityDataType<Boolean>(true, Boolean.class));
        assertThat(mSut.getValue(), is(equalTo(true)));
    }

    @Test
    public void getValue_givenOnlyLeftWithTrue_returnTrue() {
        mSut.setRight(new UnitEntityDataType<Boolean>(true, Boolean.class));
        assertThat(mSut.getValue(), is(equalTo(true)));
    }

    @Test
    public void getValue_givenFalseAndFalse_returnFalse() {
        mSut.setLeft(new UnitEntityDataType<Boolean>(false, Boolean.class));
        mSut.setRight(new UnitEntityDataType<Boolean>(false, Boolean.class));
        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getValue_givenFalseAndTrue_returnTrue() {
        mSut.setLeft(new UnitEntityDataType<Boolean>(false, Boolean.class));
        mSut.setRight(new UnitEntityDataType<Boolean>(true, Boolean.class));
        assertThat(mSut.getValue(), is(equalTo(true)));
    }

    @Test
    public void getValue_givenTrueAndFalse_returnTrue() {
        mSut.setLeft(new UnitEntityDataType<Boolean>(true, Boolean.class));
        mSut.setRight(new UnitEntityDataType<Boolean>(false, Boolean.class));
        assertThat(mSut.getValue(), is(equalTo(true)));
    }

    @Test
    public void getValue_givenTrueAndTrue_returnFalse() {
        mSut.setLeft(new UnitEntityDataType<Boolean>(true, Boolean.class));
        mSut.setRight(new UnitEntityDataType<Boolean>(true, Boolean.class));
        assertThat(mSut.getValue(), is(equalTo(false)));
    }
}
