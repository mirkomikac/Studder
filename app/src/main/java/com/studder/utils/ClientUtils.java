package com.studder.utils;


import java.text.ParseException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public static String saveMediaToPhoneStorage(String directory, String filename, String mediaEncoded){
        String sdPath = Environment.getExternalStorageDirectory().getPath();

        File dir = new File(sdPath + "/Studder/" + directory);
        File file = new File(dir, filename + ".jpg");

        FileOutputStream fileOutputStream = null;
        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(mediaEncoded.getBytes());
            fileOutputStream.flush();
        } catch (Exception e) {
            return null;
        }finally {
            if(fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return file.getAbsolutePath();
    }

    public static String readMediaFromFile(String path) {
        File file = new File(path);
        if(!file.exists()) {
            return null;
        }

        StringBuilder fileContent = new StringBuilder();

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String line = null;
            while((line = bufferedReader.readLine())!= null) {
                fileContent.append(line);
            }
        } catch (Exception e) {
            return null;
        }finally {
            if(bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    return null;
                }
            }
        }

        return fileContent.toString();
    }

    public static Bitmap getBitmapForMedia(String encodedMedia) {
        byte[] bitmapBytes = Base64.decode(encodedMedia, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
        bmp = bmp.createScaledBitmap(bmp, 200, 200, false);
        return BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
    }

    public static int calculateAge(Date date) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(date);
        int diff = calendar2.get(Calendar.YEAR) - calendar1.get(Calendar.YEAR);
        if (calendar1.get(Calendar.MONTH) > calendar2.get(Calendar.MONTH) || (calendar1.get(Calendar.MONTH)
                == calendar2.get(Calendar.MONTH) && calendar1.get(Calendar.DATE) > calendar2.get(Calendar.DATE))) {
            diff--;
        }
        return diff;

    }

}
