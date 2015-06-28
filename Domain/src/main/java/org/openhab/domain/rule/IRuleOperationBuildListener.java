package org.openhab.domain.rule;

import org.openhab.domain.rule.operators.RuleOperator;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IRuleOperationBuildListener {
    public enum RuleOperationSelectionInterface {
        NA(0),
        UNIT(1),
        NEW_OPERATION(2),
        EXISTING_OPERATION(3),
        STATIC(4),
        OPERATOR(5);

        public final int Value;

        private RuleOperationSelectionInterface(int value) {
            Value = value;
        }
    }

    public enum RuleOperationDialogButtonInterface {
        CANCEL(0),
        DONE(1),
        NEXT(3);

        public final int Value;

        private RuleOperationDialogButtonInterface(int value) {
            Value = value;
        }
    }
    public <T> void onOperationBuildResult(RuleOperationSelectionInterface ruleOperationSelectionInterface,
                                           RuleOperationDialogButtonInterface ruleOperationDialogButtonInterface,
                                           IEntityDataType<T> operand,
                                           int operandPosition,
                                           RuleOperator<T> ruleOperator);
}
