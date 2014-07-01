package org.openhab.domain.rule.operators;

import junit.framework.TestCase;

public class AndBooleanRuleOperatorTest extends TestCase {
    private AndBooleanRuleOperator mSut;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        mSut = new AndBooleanRuleOperator();
    }

    public void testBooleanAnd() {
        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            assertTrue(mSut.getOperationResult(false));
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            assertTrue(mSut.getOperationResult(true, true, true));
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Boolean true AND false = False
        assertFalse(mSut.getOperationResult(true, false));

        //Boolean false AND true = False
        assertFalse(mSut.getOperationResult(false, true));

        //Boolean false AND false = False
        assertFalse(mSut.getOperationResult(false, false));

        //Boolean true AND true = True
        assertTrue(mSut.getOperationResult(true, true));
    }
}
