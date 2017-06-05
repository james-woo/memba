package com.james.memba.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    /*
     * Convert long into pretty date, e.g. Jun 1, 2017
     */
    public static String longToDate(long time) {
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("MMM d, yyyy", Locale.US);
        return formatter.format(date);
    }
}
