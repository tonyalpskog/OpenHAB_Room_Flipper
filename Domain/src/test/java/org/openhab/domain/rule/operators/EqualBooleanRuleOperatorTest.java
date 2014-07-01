package org.openhab.domain.rule.operators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EqualBooleanRuleOperatorTest {
    private EqualBooleanRuleOperator mSut;
    
    @Before
    public void setUp() throws Exception {

        mSut = new EqualBooleanRuleOperator();
    }

    @Test
    public void testBooleanEqual() {
        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            mSut.getOperationResult(false);
            Assert.assertFalse(true);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            mSut.getOperationResult(false, true, false);
            Assert.assertFalse(true);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof IllegalArgumentException);
        }

        //Boolean true Equal false = False
        Assert.assertFalse(mSut.getOperationResult(true, false));

        //Boolean false Equal true = False
        Assert.assertFalse(mSut.getOperationResult(false, true));

        //Boolean false Equal false = False
        Assert.assertTrue(mSut.getOperationResult(false, false));

        //Boolean true Equal true = True
        Assert.assertTrue(mSut.getOperationResult(true, true));
    }
}
