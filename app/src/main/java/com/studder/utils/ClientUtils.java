package com.studder.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClientUtils {

    public static String formatDateTime(Long time){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.GERMAN);
        return sdf.format(new Date(time));
    }
}
