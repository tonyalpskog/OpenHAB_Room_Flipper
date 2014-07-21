package org.openhab.domain.rule.operations.number;

import org.junit.Before;
import org.junit.Test;
import org.openhab.domain.rule.UnitEntityDataType;
import org.openhab.domain.rule.operations.number.MoreOrEqualNumberRuleOperation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MoreOrEqualNumberRuleOperationTest {
    private MoreOrEqualNumberRuleOperation mSut;

    @Before
    public void setUp() throws Exception {

        mSut = new MoreOrEqualNumberRuleOperation();
    }

    @Test
    public void getValue_givenOnlyLeft_returnFalse() {
        mSut.setLeft(new UnitEntityDataType<Number>(3, Number.class));
        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getValue_givenOnlyRight_returnFalse() {
        mSut.setRight(new UnitEntityDataType<Number>(3, Number.class));
        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getValue_givenLeftNumberLargerThanRight_returnTrue() {
        mSut.setLeft(new UnitEntityDataType<Number>(5, Number.class));
        mSut.setRight(new UnitEntityDataType<Number>(3, Number.class));
        assertThat(mSut.getValue(), is(equalTo(true)));
    }

    @Test
    public void getValue_givenLeftNumberLessThanRight_returnFalse() {
        mSut.setLeft(new UnitEntityDataType<Number>(1, Number.class));
        mSut.setRight(new UnitEntityDataType<Number>(3, Number.class));
        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getValue_givenLeftNumberEqualToRight_returnTrue() {
        mSut.setLeft(new UnitEntityDataType<Number>(3, Number.class));
        mSut.setRight(new UnitEntityDataType<Number>(3, Number.class));
        assertThat(mSut.getValue(), is(equalTo(true)));
    }
}
