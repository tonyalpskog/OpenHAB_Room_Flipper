package org.openhab.domain.rule;

import java.util.EnumSet;

/**
 * Created by Tony Alpskog in 2014.
 */
public enum RuleOperatorType {
    Equal,
    NotEqual,
    LessThan,
    MoreThan,
    LessOrEqual,
    MoreOrEqual,
    Between,
    Within,
    And,
    Or,
    Before,
    After,
    BeforeOrEqual,
    AfterOrEqual;

    public static EnumSet<RuleOperatorType> TYPES = EnumSet.of(Equal,
            NotEqual,
            LessThan,
            MoreThan,
            LessOrEqual,
            MoreOrEqual,
            And,
            Or,
            Before,
            After,
            BeforeOrEqual,
            AfterOrEqual);
}
