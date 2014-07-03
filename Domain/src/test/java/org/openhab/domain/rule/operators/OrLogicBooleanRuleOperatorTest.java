package org.openhab.domain.rule.operators;

import junit.framework.TestCase;

import org.openhab.domain.rule.LogicBoolean;

public class OrLogicBooleanRuleOperatorTest extends TestCase {
    private OrLogicBooleanRuleOperator mSut;

    private final LogicBoolean LogicalBoolean_TRUE = new LogicBoolean(true);
    private final LogicBoolean LogicalBoolean_FALSE = new LogicBoolean(false);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        mSut = new OrLogicBooleanRuleOperator();
    }

    public void testBooleanOr() {
        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            mSut.getOperationResult(LogicalBoolean_FALSE);
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            mSut.getOperationResult(LogicalBoolean_FALSE, LogicalBoolean_FALSE, LogicalBoolean_FALSE);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Boolean true OR false = False
        assertTrue(mSut.getOperationResult(LogicalBoolean_TRUE, LogicalBoolean_FALSE));

        //Boolean false OR true = False
        assertTrue(mSut.getOperationResult(LogicalBoolean_FALSE, LogicalBoolean_TRUE));

        //Boolean false OR false = False
        assertFalse(mSut.getOperationResult(LogicalBoolean_FALSE, LogicalBoolean_FALSE));

        //Boolean true OR true = True
        assertTrue(mSut.getOperationResult(LogicalBoolean_TRUE, LogicalBoolean_TRUE));

        //Boolean true OR false OR true = True
        assertTrue(mSut.getOperationResult(LogicalBoolean_TRUE, LogicalBoolean_FALSE, LogicalBoolean_TRUE));

        //Boolean false OR true OR false = True
        assertTrue(mSut.getOperationResult(LogicalBoolean_FALSE, LogicalBoolean_TRUE, LogicalBoolean_FALSE));

        //Boolean false OR false OR false = False
        assertFalse(mSut.getOperationResult(LogicalBoolean_FALSE, LogicalBoolean_FALSE, LogicalBoolean_FALSE));
    }
}
