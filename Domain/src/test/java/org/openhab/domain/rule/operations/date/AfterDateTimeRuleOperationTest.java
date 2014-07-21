package org.openhab.domain.rule.operations.date;

import org.junit.Before;
import org.junit.Test;
import org.openhab.domain.rule.UnitEntityDataType;
import org.openhab.domain.rule.operations.date.AfterDateTimeRuleOperation;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AfterDateTimeRuleOperationTest {
    private AfterDateTimeRuleOperation mSut;

    @Before
    public void setUp() {
        mSut = new AfterDateTimeRuleOperation();
    }

    @Test
    public void getValue_givenOnlyLeft_returnFalse() {
        mSut.setLeft(new UnitEntityDataType<Date>(getDate(2013, Calendar.SEPTEMBER, 23, 13, 37), Date.class));
        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getValue_givenOnlyRight_returnFalse() {
        mSut.setRight(new UnitEntityDataType<Date>(getDate(2013, Calendar.SEPTEMBER, 23, 13, 37), Date.class));
        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getOperationResult_givenTimeBefore_returnFalse() throws ParseException {
        mSut.setLeft(new UnitEntityDataType<Date>("10:15", getTime(10, 15), Date.class));
        mSut.setRight(new UnitEntityDataType<Date>("11:30", getTime(11, 30), Date.class));

        assertThat(mSut.getValue(), is(equalTo(false)));
    }

    @Test
    public void getOperationResult_givenTimeAfter_returnTrue() throws ParseException {
        mSut.setLeft(new UnitEntityDataType<Date>("11:30", getTime(11, 30), Date.class));
        mSut.setRight(new UnitEntityDataType<Date>("10:15", getTime(10, 15), Date.class));

        assertThat(mSut.getValue(), is(equalTo(true)));
    }
    
    @Test
    public void getOperationResult_givenDateTimeBefore_returnFalse() throws ParseException {
        mSut.setLeft(new UnitEntityDataType<Date>("11:30 2014-03-24", getDate(2014, Calendar.MARCH, 24, 11, 30), Date.class));
        mSut.setRight(new UnitEntityDataType<Date>("10:15 2014-03-25", getDate(2014, Calendar.MARCH, 25, 10, 15), Date.class));

        assertThat(mSut.getValue(), is(equalTo(false)));
    }
    
    @Test
    public void getOperationResult_givenDateTimeAfter_returnTrue() throws ParseException {
        mSut.setLeft(new UnitEntityDataType<Date>("10:15 2014-03-24", getDate(2014, Calendar.MARCH, 24, 10, 15), Date.class));
        mSut.setRight(new UnitEntityDataType<Date>("11:30 2014-03-23", getDate(2014, Calendar.MARCH, 23, 11, 30), Date.class));

        assertThat(mSut.getValue(), is(equalTo(true)));
    }

    private Date getDate(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    private Date getTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTime();
    }
}
