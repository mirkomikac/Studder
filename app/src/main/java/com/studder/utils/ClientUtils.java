package com.studder.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClientUtils {

    public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    public static final String DEFAULT_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm";

    public static String formatDateTime(Long time, String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.GERMAN);
        return sdf.format(new Date(time));
    }

    public static Date parseDateTime(String date, String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.GERMAN);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

}
