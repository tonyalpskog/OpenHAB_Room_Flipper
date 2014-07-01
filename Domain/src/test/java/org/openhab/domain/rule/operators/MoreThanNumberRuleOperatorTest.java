package org.openhab.domain.rule.operators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MoreThanNumberRuleOperatorTest {
    private MoreThanNumberRuleOperator mSut;
    
    @Before
    public void setUp() throws Exception {

        mSut = new MoreThanNumberRuleOperator();
    }

    @Test
    public void testNumberMoreThan() {
        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            mSut.getOperationResult(1);
            Assert.assertFalse(true);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            mSut.getOperationResult(1, 2, 3);
            Assert.assertFalse(true);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof IllegalArgumentException);
        }

        //Integer 10 > 11 = False
        Assert.assertFalse(mSut.getOperationResult(10, 11));

        //Integer 11 > 10 = True
        Assert.assertTrue(mSut.getOperationResult(11, 10));

        //Integer 10 > Float 11 = False
        Assert.assertFalse(mSut.getOperationResult(10, (float) 11));

        //Double 10.1 > Float 10.1 = False
        Assert.assertFalse(mSut.getOperationResult(10.1, 10.1f));

        //Double 10.1 > Float 10.2 = False
        Assert.assertFalse(mSut.getOperationResult(10.1, 10.2f));

        //Float 8.9 > Float 9 = False
        Assert.assertFalse(mSut.getOperationResult(8.9f, (float) 9));

        //Float 34.7 > Float 9 = True
        Assert.assertTrue(mSut.getOperationResult(34.7f, (float) 9));

        //Float 34.7 > Float 34.7 = False
        Assert.assertFalse(mSut.getOperationResult(34.7f, 34.7f));

        //Double 34.7 > Double 34.7 = False
        Assert.assertFalse(mSut.getOperationResult(34.7, 34.7));

        //Double -3 > Double 10 = False
        Assert.assertFalse(mSut.getOperationResult((double) -3, (double) 10));
    }
}
