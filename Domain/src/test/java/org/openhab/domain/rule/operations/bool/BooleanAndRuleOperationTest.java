package org.openhab.domain.rule.operations.bool;

import org.junit.Before;
import org.junit.Test;
import org.openhab.domain.rule.UnitEntityDataType;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BooleanAndRuleOperationTest {
    private BooleanAndRuleOperation mSut;

    @Before
    public void setUp() throws Exception {

        mSut = new BooleanAndRuleOperation();
    }

    @Test
    public void getValue_givenOnlyLeft_returnFalse() {
        mSut.setLeft(new UnitEntityDataType<Boolean>(true, Boolean.class));
        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getValue_givenOnlyRight_returnFalse() {
        mSut.setRight(new UnitEntityDataType<Boolean>(true, Boolean.class));
        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getValue_givenTrueAndFalse_returnFalse() {
        mSut.setLeft(new UnitEntityDataType<Boolean>(true, Boolean.class));
        mSut.setRight(new UnitEntityDataType<Boolean>(false, Boolean.class));

        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getValue_givenFalseAndFalse_returnFalse() {
        mSut.setLeft(new UnitEntityDataType<Boolean>(false, Boolean.class));
        mSut.setRight(new UnitEntityDataType<Boolean>(false, Boolean.class));

        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getValue_givenTrueAndTrue_returnFalse() {
        mSut.setLeft(new UnitEntityDataType<Boolean>(true, Boolean.class));
        mSut.setRight(new UnitEntityDataType<Boolean>(true, Boolean.class));

        assertThat(mSut.getValue(), is(equalTo(true)));
    }

    @Test
    public void getValue_givenFalseAndTrue_returnFalse() {
        mSut.setLeft(new UnitEntityDataType<Boolean>(false, Boolean.class));
        mSut.setRight(new UnitEntityDataType<Boolean>(true, Boolean.class));

        assertThat(mSut.getValue(), is(equalTo(false)));
    }
}
