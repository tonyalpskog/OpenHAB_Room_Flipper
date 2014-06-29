package org.openhab.domain.business.rule;

public enum RuleActionValueType {
    STATIC(0),
    SOURCE_UNIT(1),
    TEXT(2),
    NA(3);

    public final int Value;

    private RuleActionValueType(int value) {
        Value = value;
    }
}
