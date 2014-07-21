package org.openhab.domain.rule.operations.number;

import org.junit.Before;
import org.junit.Test;
import org.openhab.domain.rule.UnitEntityDataType;
import org.openhab.domain.rule.operations.number.BetweenNumberRuleOperation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BetweenNumberRuleOperationTest {
    private BetweenNumberRuleOperation mSut;

    @Before
    public void setUp() throws Exception {
        mSut = new BetweenNumberRuleOperation();
    }

    @Test
    public void getValue_givenNumberMoreThanLeftAndLessThanRight_returnTrue() {
        setNumber(11f, 10f, 13f);
        assertThat(mSut.getValue(), is(equalTo(true)));
    }

    @Test
    public void getValue_givenNumberLessThanLeftAndLessThanRight_returnFalse() {
        setNumber(9f, 10f, 13f);
        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getValue_givenNumberMoreThanLeftAndMoreThanRight_returnFalse() {
        setNumber(20f, 10f, 13f);
        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getValue_givenNumberEqualToLeftAndLessThanRight_returnFalse() {
        setNumber(10f, 10f, 13f);
        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getValue_givenNumberEqualToLeftAndEqualToRight_returnFalse() {
        setNumber(10f, 10f, 10f);
        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getValue_givenNumberMoreThanLeftAndEqualToRight_returnFalse() {
        setNumber(15f, 10f, 15f);
        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    private void setNumber(Number number, Number left, Number right) {
        mSut.setCompareValue(new UnitEntityDataType<Number>(number, Number.class));
        mSut.setLeft(new UnitEntityDataType<Number>(left, Number.class));
        mSut.setRight(new UnitEntityDataType<Number>(right, Number.class));
    }
}
