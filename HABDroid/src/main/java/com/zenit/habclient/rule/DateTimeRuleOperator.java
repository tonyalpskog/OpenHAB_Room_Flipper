package com.zenit.habclient.rule;

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

    protected final SimpleDateFormat[] DateStringFormatArray = new SimpleDateFormat[]{TimeMinuteStringFormat, TimeSecondsFormat, DateStringFormat, WeekTimerStringFormat};

    public DateTimeRuleOperator(RuleOperatorType type, Boolean supportsMultipleOperations) {
        super(type, supportsMultipleOperations);
    }

    @Override
    public T parseValue(String valueAsString) throws ParseException {
        Calendar cal = GregorianCalendar.getInstance();
        boolean successfulParse = false;
        ParseException parseException = null;
        for(SimpleDateFormat sdf : DateStringFormatArray) {
            try {
                cal.setTime(sdf.parse(valueAsString));
                successfulParse = true;
                break;
            } catch(ParseException e) {
                parseException = e;
            }
        }

        if(!successfulParse) throw parseException;

        return (T) cal.getTime();
    }
}
