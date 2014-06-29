package org.openhab.domain.rule;

public enum RuleActionType {
    COMMAND(0),
    MESSAGE(1);

    public final int Value;

    private RuleActionType(int value) {
        Value = value;
    }
}
