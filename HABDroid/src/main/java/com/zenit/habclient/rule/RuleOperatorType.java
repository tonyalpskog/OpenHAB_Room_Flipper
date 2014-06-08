package com.zenit.habclient.rule;

/**
 * Created by Tony Alpskog in 2014.
 */
public enum RuleOperatorType {
    Equal(0, 2, 2, 1, "=", " = %s"),
    NotEqual(1, 2, 2, 1, "!=", " != %s"),
    LessThan(2, 2, 2, 1, "<", " < %s"),
    MoreThan(3, 2, 2, 1, ">", " > %s"),
    LessOrEqual(4, 2, 2, 1, "<=", " <= %s"),
    MoreOrEqual(5, 2, 2, 1, ">=", " >= %s"),
    Between(6, 3, 3, 2, "between", " between %s and %s"),
    Within(7, 3, 3, 2, "within", " within %s and %s"),
    And(8, 2, Integer.MAX_VALUE, 1, "AND", " AND %s"),
    Or(9, 2, Integer.MAX_VALUE, 1, "OR", " OR %s"),
    Before(10, 2, 2, 1, "before", " before %s"),
    After(11, 2, 2, 1, "after", " after %s"),
    BeforeOrEqual(12, 2, 2, 1, "before or equal", " before or equal to %s"),
    AfterOrEqual(13, 2, 2, 1, "after or equal", " after or equal to %s");

    public final static String MISSING_OPERAND = "<Missing operand>";//TODO - TA: language

    public final int Value;
    private final int minimumNumberOfSupportedOperationArgs;
    private final int maximumNumberOfSupportedOperationArgs;
    private final int numberOfPrintSupportedOperationArgs;
    private final String name;
    private final String stringFormat;

    private RuleOperatorType(int id, int minimumNumberOfSupportedOperationArgs, int maximumNumberOfSupportedOperationArgs, int numberOfPrintSupportedOperationArgs, String name, String stringFormat) {
        Value = id;
        this.minimumNumberOfSupportedOperationArgs = minimumNumberOfSupportedOperationArgs;
        this.maximumNumberOfSupportedOperationArgs = maximumNumberOfSupportedOperationArgs;
        this.numberOfPrintSupportedOperationArgs = numberOfPrintSupportedOperationArgs;
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

        String[] argsAsStrings = args;
        for(int i = 0; i < argsAsStrings.length; i++)
            if(argsAsStrings[i] == null)
                argsAsStrings[i] = MISSING_OPERAND;

        StringBuilder formattedString = new StringBuilder();
        if(argsAsStrings.length > numberOfPrintSupportedOperationArgs) {
            for(int i = 0; i < argsAsStrings.length; i += numberOfPrintSupportedOperationArgs) {
                String[] argsAsStringsSubArray = new String[numberOfPrintSupportedOperationArgs];
                int k = 0;
                for (int j = i; j < i + numberOfPrintSupportedOperationArgs; j++)
                    argsAsStringsSubArray[k++] = argsAsStrings[j];

                formattedString.append(String.format(stringFormat, argsAsStringsSubArray));
            }
        } else
            formattedString.append(String.format(stringFormat, argsAsStrings));

        return formattedString.toString();
    }
}
