package org.strut.amway.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.strut.amway.core.model.DateTimeRange;

public class DateTimeUtilsTest {

    @Test
    public void checkTwoDateTimeAreInAMonth() {
        DateTime dt1 = new DateTime(2014, 5, 15, 15, 10, 50, 111);
        DateTime dt2 = new DateTime(2014, 5, 1, 9, 20, 10, 222);
        assertTrue(DateTimeUtils.isInAMonth(dt1, dt2));
    }

    @Test
    public void checkTwoDateTimeAreNotInAMonth() {
        DateTime dt1 = new DateTime(2014, 5, 15, 15, 10, 50, 111);
        DateTime dt2 = new DateTime(2014, 3, 1, 9, 20, 10, 222);
        assertFalse(DateTimeUtils.isInAMonth(dt1, dt2));

        dt1 = new DateTime(2014, 5, 15, 15, 10, 50, 111);
        dt2 = new DateTime(2013, 5, 1, 9, 20, 10, 222);
        assertFalse(DateTimeUtils.isInAMonth(dt1, dt2));
    }

    @Test
    public void checkTwoDateTimeAreInAMonthThatShouldBeFalseWhenDateTime1IsNull() {
        DateTime dt1 = null;
        DateTime dt2 = new DateTime(2014, 5, 1, 9, 20, 10, 222);
        assertFalse(DateTimeUtils.isInAMonth(dt1, dt2));
    }

    @Test
    public void checkTwoDateTimeAreInAMonthThatShouldBeFalseWhenDateTime2IsNull() {
        DateTime dt1 = new DateTime(2014, 5, 15, 15, 10, 50, 111);
        DateTime dt2 = null;
        assertFalse(DateTimeUtils.isInAMonth(dt1, dt2));
    }

    @Test
    public void checkTwoDateTimeAreInAMonthWithDifferentTimeZone() {
        DateTime dt1 = new DateTime(2014, 5, 15, 15, 10, 50, 111, DateTimeZone.forOffsetHours(8));
        DateTime dt2 = new DateTime(2014, 3, 1, 9, 20, 10, 222, DateTimeZone.forOffsetHours(7));
        assertFalse(DateTimeUtils.isInAMonth(dt1, dt2));
    }

    @Test
    public void shouldParseClientTimeWithClientTZ() {
        final String clientTZ = "+03:00";
        final String date = "2014-11-10";
        final int hour = 14;
        final int minute = 28;
        final int second = 40;
        final int milissecond = 111;
        final String iso8601Time = date + "T" + hour + ":" + minute + ":" + second + "." + milissecond + clientTZ;
        final DateTime result = DateTimeUtils.parseClientTimeWithClientTZ(iso8601Time);
        assertNotNull(result);
        assertEquals(clientTZ, result.getZone().getID());
        assertEquals(hour, result.getHourOfDay());
        assertEquals(minute, result.getMinuteOfHour());
        assertEquals(second, result.getSecondOfMinute());
        assertEquals(milissecond, result.getMillisOfSecond());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionOnParseClientTimeWithClientTZWhenParamIsNull() {
        final String iso8601Time = null;
        DateTimeUtils.parseClientTimeWithClientTZ(iso8601Time);
    }

    @Test
    public void shouldParseToUTCProperly() {
        final GregorianCalendar calendar = new GregorianCalendar(2014, 11, 13, 10, 10, 10);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
        final DateTime result = DateTimeUtils.parseToUTC(calendar);
        assertNotNull(result);
        assertEquals(calendar.get(Calendar.DAY_OF_MONTH), result.getDayOfMonth());
        // calendar month start with zero-based
        assertEquals(calendar.get(Calendar.MONTH) + 1, result.getMonthOfYear());
        assertEquals(calendar.get(Calendar.YEAR), result.getYear());
        int expectedHour = calendar.get(Calendar.HOUR_OF_DAY) - (calendar.getTimeZone().getRawOffset() / 1000 / 60 / 60);
        assertEquals(expectedHour, result.getHourOfDay());
        assertEquals(calendar.get(Calendar.MINUTE), result.getMinuteOfHour());
        assertEquals(calendar.get(Calendar.SECOND), result.getSecondOfMinute());
        assertEquals(DateTimeZone.UTC, result.getZone());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionOnParseToUTCWhenCalendarParamIsNull() {
        final GregorianCalendar calendar = null;
        DateTimeUtils.parseToUTC(calendar);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnToServerTimeWhenParamIsNull() {
        final DateTime dateTime = null;
        DateTimeUtils.toServerTime(dateTime);
    }

    @Test
    public void shouldGetDateTimeServerTimeProperly() {
        final DateTime dateTime = new DateTime(2014, 11, 13, 10, 10, 10, 10, DateTimeZone.forOffsetHours(3));
        final DateTime result = DateTimeUtils.toServerTime(dateTime);

        assertNotNull(result);
        assertEquals(dateTime.withZone(new DateTime().getZone()), result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnToServerTimeWithParamIsEmpty() {
        final String dateTimeInStr = "";
        DateTimeUtils.toServerTime(dateTimeInStr);
    }

    @Test
    public void shouldGetDateTimeServerTimeProperlyFromClientTime() {
        final DateTime dateTime = new DateTime(2014, 11, 13, 10, 10, 10, 10, DateTimeZone.UTC);
        final DateTime result = DateTimeUtils.toServerTime(dateTime.toString());

        assertNotNull(result);
        assertEquals(dateTime.withZone(new DateTime().getZone()), result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnGetDateTimeRangeByServerTimeOfWhenParamIsNull() {
        final DateTime dateTime = null;
        DateTimeUtils.getDateTimeRangeByServerTimeOf(dateTime);
    }

    @Test
    public void shouldGetDateTimeRangeByServerTimeOfProperly() {
        int year = 2014;
        int month = 11;
        int day = 13;
        int hour = 10;
        int minute = 10;
        int second = 10;
        int milisecond = 10;
        int zoneOffset = 3;
        final DateTime dateTime = new DateTime(year, month, day, hour, minute, second, milisecond, DateTimeZone.forOffsetHours(zoneOffset));
        final DateTimeRange result = DateTimeUtils.getDateTimeRangeByServerTimeOf(dateTime);

        final DateTime firstDayOfMonth = new DateTime(year, month, 1, 0, 0, 0, 0, DateTimeZone.forOffsetHours(zoneOffset));
        final DateTime lastDayOfMonth = new DateTime(year, month, 30, 23, 59, 59, 999, DateTimeZone.forOffsetHours(zoneOffset));
        final DateTime serverTime = new DateTime();
        assertNotNull(result);
        assertEquals(firstDayOfMonth.withZone(serverTime.getZone()), result.getStartTime());
        assertEquals(lastDayOfMonth.withZone(serverTime.getZone()), result.getEndTime());
    }

}
