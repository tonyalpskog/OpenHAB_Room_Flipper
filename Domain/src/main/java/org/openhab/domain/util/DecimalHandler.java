package org.openhab.domain.util;

import java.text.DecimalFormat;

/**
 * Created by Tony Alpskog in 2014.
 */
public class DecimalHandler {
    private static DecimalFormat getDecimalFormat(int numberOfDecimals) {
        StringBuilder sb = new StringBuilder(numberOfDecimals);
        for (int i = 0; i < numberOfDecimals; ++i)
            sb. append("0");

        return new DecimalFormat("#." + sb.toString());
    }

    public static Double getFixNumberOfDecimals(Double decimalNumber, int numberOfDecimals) {
        return decimalNumber.valueOf(getDecimalFormat(numberOfDecimals).format(decimalNumber));
    }

    public static Float getFixNumberOfDecimals(Float decimalNumber, int numberOfDecimals) {
        return decimalNumber.valueOf(getDecimalFormat(numberOfDecimals).format(decimalNumber));
    }
}
