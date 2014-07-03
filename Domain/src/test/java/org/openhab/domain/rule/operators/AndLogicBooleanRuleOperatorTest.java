package org.openhab.domain.rule.operators;

import junit.framework.TestCase;

import org.openhab.domain.rule.LogicBoolean;

public class AndLogicBooleanRuleOperatorTest extends TestCase {
    private AndLogicBooleanRuleOperator mSut;

    private final LogicBoolean LogicalBoolean_TRUE = new LogicBoolean(true);
    private final LogicBoolean LogicalBoolean_FALSE = new LogicBoolean(false);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        mSut = new AndLogicBooleanRuleOperator();
    }

    public void testBooleanAnd() {
        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            assertTrue(mSut.getOperationResult(LogicalBoolean_FALSE));
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            assertTrue(mSut.getOperationResult(LogicalBoolean_TRUE, LogicalBoolean_TRUE, LogicalBoolean_TRUE));
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Boolean true AND false = False
        assertFalse(mSut.getOperationResult(LogicalBoolean_TRUE, LogicalBoolean_FALSE));

        //Boolean false AND true = False
        assertFalse(mSut.getOperationResult(LogicalBoolean_FALSE, LogicalBoolean_TRUE));

        //Boolean false AND false = False
        assertFalse(mSut.getOperationResult(LogicalBoolean_FALSE, LogicalBoolean_FALSE));

        //Boolean true AND true = True
        assertTrue(mSut.getOperationResult(LogicalBoolean_TRUE, LogicalBoolean_TRUE));
    }
}
