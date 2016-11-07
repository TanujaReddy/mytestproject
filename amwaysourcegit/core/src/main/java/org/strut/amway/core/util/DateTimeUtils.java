package org.strut.amway.core.util;

import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.strut.amway.core.model.DateTimeRange;

public final class DateTimeUtils {

    private DateTimeUtils() {
    }

    public static boolean isInAMonth(final DateTime d1, final DateTime d2) {
        if (d1 == null || d2 == null) {
            return false;
        }

        if (!d1.getZone().equals(d2.getZone())) {
            return false;
        }
        if (d1.getMonthOfYear() == d2.getMonthOfYear() && d1.getYear() == d2.getYear()) {
            return true;
        }
        return false;
    }

    public static DateTime parseClientTimeWithClientTZ(final String clientTimeInString) {
        if (clientTimeInString == null) {
            throw new IllegalStateException("clientTime Param can not be null");
        }
        return ISODateTimeFormat.dateTime().withOffsetParsed().parseDateTime(clientTimeInString);
    }

    public static DateTime parseToUTC(final GregorianCalendar calendar) {
        if (calendar == null) {
            throw new IllegalStateException("calendar param can not be null");
        }
        return new DateTime(calendar, DateTimeZone.UTC);
    }

    public static DateTimeRange getDateTimeRangeByServerTimeOf(final DateTime dateTimeClientTZ) {
        if (dateTimeClientTZ == null) {
            throw new IllegalArgumentException("dateTime param can not be null");
        }

        final DateTime firstDayOfMonth = toServerTime(dateTimeClientTZ.dayOfMonth().withMinimumValue().withTime(0, 0, 0, 0));
        final DateTime lastDayOfMonth = toServerTime(dateTimeClientTZ.dayOfMonth().withMaximumValue().withTime(23, 59, 59, 999));
        return new DateTimeRange(firstDayOfMonth, lastDayOfMonth);
    }

    public static DateTime toServerTime(final DateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("dateTime param can not be null");
        }
        return dateTime.withZone(new DateTime().getZone());
    }

    public static DateTime toServerTime(final String dateTimeInStr) {
        if (StringUtils.isEmpty(dateTimeInStr)) {
            throw new IllegalArgumentException("dateTime param can not be null");
        }
        return new DateTime(dateTimeInStr);
    }

    public static DateTime getStartDateTime(final int minusMonth) {
        return new DateTime().minusMonths(minusMonth).dayOfMonth().withMinimumValue().withTime(0, 0, 0, 0);
    }
}
