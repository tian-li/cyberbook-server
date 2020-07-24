package app.cyberbook.cyberbookserver.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateTimeUtil {
    // joda-time
//    public static final String STANDARD_FORMAT = "dd/MM/YYYY'T'HH:mm:ss.SSS'Z'";
//
//    // ISOString to millisecond
//    public static Long strToMillisecond(String dateTimeStr) {
//        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
//        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
//        return dateTime.getMillis();
//    }
//
//    // millisecond to ISOString
//    public static String millisecondToStr(Date date) {
//        DateTime dateTime = new DateTime(date);
//        return dateTime.toDateTimeISO();
//    }
//
//    public static String dateToStr(Date date) {
//        DateTime dateTime = new DateTime(date);
//        return dateTime.toString(STANDARD_FORMAT);
//    }
}