package org.openhab.domain.rule.operators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WithinNumberRuleOperatorTest {
    private WithinNumberRuleOperator mSut;

    @Before
    public void setUp() throws Exception {

        mSut = new WithinNumberRuleOperator();
    }

    @Test
    public void testNumberWithin() {
        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            mSut.getOperationResult(1);
            Assert.assertFalse(true);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof IllegalArgumentException);
        }

        try {
            mSut.getOperationResult(1, 2);
            Assert.assertFalse(true);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof IllegalArgumentException);
        }

        try {
            mSut.getOperationResult(1, 2, 3, 4);
            Assert.assertFalse(true);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof IllegalArgumentException);
        }

        //Float 9 within 10 and 13 = False
        Assert.assertFalse(mSut.getOperationResult((float) 9, (float) 10, (float) 13));

        //Float 10 within 10 and 13 = True
        Assert.assertTrue(mSut.getOperationResult((float) 10, (float) 10, (float) 13));

        //Float 11 within 10 and 13 = True
        Assert.assertTrue(mSut.getOperationResult((float) 11, (float) 10, (float) 13));

        //Float 12 within 10 and 13 = True
        Assert.assertTrue(mSut.getOperationResult((float) 12, (float) 10, (float) 13));

        //Float 13 within 10 and 13 = True
        Assert.assertTrue(mSut.getOperationResult((float) 13, (float) 10, (float) 13));

        //Float 14 within 10 and 13 = False
        Assert.assertFalse(mSut.getOperationResult((float) 14, (float) 10, (float) 13));
    }
}
