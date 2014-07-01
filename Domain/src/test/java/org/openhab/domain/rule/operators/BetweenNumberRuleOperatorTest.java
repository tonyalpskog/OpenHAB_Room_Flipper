package org.openhab.domain.rule.operators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BetweenNumberRuleOperatorTest {
    private BetweenNumberRuleOperator mSut;

    @Before
    public void setUp() throws Exception {

        mSut = new BetweenNumberRuleOperator();
    }

    @Test
    public void testNumberBetween() {
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

        //Float 9 between 10 and 13 = False
        Assert.assertFalse(mSut.getOperationResult((float) 9, (float) 10, (float) 13));

        //Float 10 between 10 and 13 = False
        Assert.assertFalse(mSut.getOperationResult((float) 10, (float) 10, (float) 13));

        //Float 10.1 between 10 and 13 = False
        Assert.assertTrue(mSut.getOperationResult(10.1f, (float) 10, (float) 13));

        //Float 11 between 10 and 13 = True
        Assert.assertTrue(mSut.getOperationResult((float) 11, (float) 10, (float) 13));

        //Float 12.9 between 10 and 13 = True
        Assert.assertTrue(mSut.getOperationResult(12.9f, (float) 10, (float) 13));

        //Float 13 between 10 and 13 = False
        Assert.assertFalse(mSut.getOperationResult((float) 13, (float) 10, (float) 13));

        //Float 14 between 10 and 13 = False
        Assert.assertFalse(mSut.getOperationResult((float) 14, (float) 10, (float) 13));
    }
}
