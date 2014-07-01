package org.openhab.domain.rule.operators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NotEqualBooleanRuleOperatorTest {
    private NotEqualBooleanRuleOperator mSut;
    
    @Before
    public void setUp() throws Exception {

        mSut = new NotEqualBooleanRuleOperator();
    }

    @Test
    public void testBooleanNotEqual() {
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

        //Boolean true NotEqual false = False
        Assert.assertTrue(mSut.getOperationResult(true, false));

        //Boolean false NotEqual true = False
        Assert.assertTrue(mSut.getOperationResult(false, true));

        //Boolean false NotEqual false = False
        Assert.assertFalse(mSut.getOperationResult(false, false));

        //Boolean true NotEqual true = True
        Assert.assertFalse(mSut.getOperationResult(true, true));
    }
}
