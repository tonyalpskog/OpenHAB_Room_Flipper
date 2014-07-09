package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.RuleOperatorType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
* Created by Tony Alpskog in 2014.
*/
public abstract class DateTimeRuleOperator<T> extends RuleOperator<T> {

    public final SimpleDateFormat TimeMinuteStringFormat = new SimpleDateFormat("HH:mm");
    public final SimpleDateFormat TimeSecondsFormat = new SimpleDateFormat("HH:mm:ss");
    public final SimpleDateFormat DateStringFormat = new SimpleDateFormat("yyyy-MM-dd");
    public final SimpleDateFormat WeekTimerStringFormat = new SimpleDateFormat("EE HH:mm");
    public final SimpleDateFormat TimeMinuteDateStringFormat = new SimpleDateFormat("HH:mm yyyy-MM-dd");
    public final SimpleDateFormat TimeSecondsDateFormat = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");

    protected final SimpleDateFormat[] DateStringFormatArray = new SimpleDateFormat[] {
            TimeSecondsDateFormat,
            TimeMinuteDateStringFormat,
            WeekTimerStringFormat,
            DateStringFormat,
            TimeSecondsFormat,
            TimeMinuteStringFormat
    };

    public DateTimeRuleOperator(RuleOperatorType type, Boolean supportsMultipleOperations) {
        super(type, supportsMultipleOperations);
    }

    @Override
    public T parseValue(String valueAsString) throws ParseException {
        Calendar cal = GregorianCalendar.getInstance();
        ParseException parseException = null;
        for(SimpleDateFormat sdf : DateStringFormatArray) {
            try {
                cal.setTime(sdf.parse(valueAsString));
                return (T) cal.getTime();
            } catch(ParseException e) {
                parseException = e;
            }
        }

        throw parseException;
    }
}
