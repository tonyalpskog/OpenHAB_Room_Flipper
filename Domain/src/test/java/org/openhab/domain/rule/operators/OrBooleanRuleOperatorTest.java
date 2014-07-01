package org.openhab.domain.rule.operators;

import junit.framework.TestCase;

public class OrBooleanRuleOperatorTest extends TestCase {
    private OrBooleanRuleOperator mSut;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        mSut = new OrBooleanRuleOperator();
    }

    public void testBooleanOr() {
        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            mSut.getOperationResult(false);
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            mSut.getOperationResult(false, false, false);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Boolean true OR false = False
        assertTrue(mSut.getOperationResult(true, false));

        //Boolean false OR true = False
        assertTrue(mSut.getOperationResult(false, true));

        //Boolean false OR false = False
        assertFalse(mSut.getOperationResult(false, false));

        //Boolean true OR true = True
        assertTrue(mSut.getOperationResult(true, true));

        //Boolean true OR false OR true = True
        assertTrue(mSut.getOperationResult(true, false, true));

        //Boolean false OR true OR false = True
        assertTrue(mSut.getOperationResult(false, true, false));

        //Boolean false OR false OR false = False
        assertFalse(mSut.getOperationResult(false, false, false));
    }
}
