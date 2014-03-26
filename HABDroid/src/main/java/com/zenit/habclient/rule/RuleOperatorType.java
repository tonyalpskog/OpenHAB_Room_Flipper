package com.zenit.habclient.rule;

/**
 * Created by Tony Alpskog in 2014.
 */
public enum RuleOperatorType {
    Equal(0, 2, 2, "=", " = %s"),
    NotEqual(1, 2, 2, "!=", " != %s"),
    LessThan(2, 2, 2, "<", " < %s"),
    MoreThan(3, 2, 2, ">", " > %s"),
    LessOrEqual(4, 2, 2, "<=", " <= %s"),
    MoreOrEqual(5, 2, 2, ">=", " >= %s"),
    Between(6, 3, 3, "between", " between %s and %s"),
    Within(7, 3, 3, "within", " within %s and %s"),
    And(8, 2, Integer.MAX_VALUE, "AND", " AND %s"),
    Or(9, 2, 2, "OR", " OR %s"),
    Before(10, 2, 2, "before", " before %s"),
    After(11, 2, 2, "after", " after %s"),
    BeforeOrEqual(12, 2, 2, "before or equal", " before or equal to %s"),
    AfterOrEqual(13, 2, 2, "after or equal", " after or equal to %s");

    public final int Value;
    private final int minimumNumberOfSupportedOperationArgs;
    private final int maximumNumberOfSupportedOperationArgs;
    private final String name;
    private final String stringFormat;

    private RuleOperatorType(int id, int minimumNumberOfSupportedOperationArgs, int maximumNumberOfSupportedOperationArgs, String name, String stringFormat) {
        Value = id;
        this.minimumNumberOfSupportedOperationArgs = minimumNumberOfSupportedOperationArgs;
        this.maximumNumberOfSupportedOperationArgs = maximumNumberOfSupportedOperationArgs;
        this.name = name;
        this.stringFormat = stringFormat;
    }

    public String getName() {
        return name;
    }

    public int getMinimumNumberOfSupportedOperationArgs() {
        return minimumNumberOfSupportedOperationArgs;
    }

    public int getMaximumNumberOfSupportedOperationArgs() {
        return maximumNumberOfSupportedOperationArgs;
    }

    public String getFormattedString(String... args) throws IllegalArgumentException {
        if(args.length < (minimumNumberOfSupportedOperationArgs - 1) || args.length > (maximumNumberOfSupportedOperationArgs - 1))
            throw new IllegalArgumentException(args.length + " arguments passed when number of supported arguments = " + (minimumNumberOfSupportedOperationArgs == maximumNumberOfSupportedOperationArgs? (minimumNumberOfSupportedOperationArgs - 1): "between " + (minimumNumberOfSupportedOperationArgs - 1) + " and " + (maximumNumberOfSupportedOperationArgs - 1)));

        String formattedString;
        formattedString = String.format(stringFormat, (String[]) args);

        return formattedString;
    }
}
