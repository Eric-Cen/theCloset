package com.mcarving.thecloset.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * {@Link MyPreferences} stores and retrieves the category list from
 * shared preferences.
 */
public class MyPreferences {

    private static final String TAG = "MyPreferences";
    public static final String TIME_PATTERN = "yyyy/MM/dd HH:mm:ss";
    public static final String DISPLAY_TIME_PATTERN = "MM/dd/yyyy";

    public static final String PREFS_WEATHER_INFO = "weather_info";
    public static final String PREFS_DATE_STRING = "date";


    // MM/dd/yyyy
    public static String getSimpleCurrentDate(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String dateString = sp.getString(PREFS_DATE_STRING, null);
        if (dateString != null) {
            SimpleDateFormat format = new SimpleDateFormat(TIME_PATTERN);
            Date d1 = null;
            try {
                d1 = format.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            return simpleDateFormat.format(d1);

        } else {

            return null;
        }
    }

    public static boolean setCurrentDate(Context context, String dateString) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(PREFS_DATE_STRING, dateString);

        return editor.commit();
    }

    // return true if there are greater 60-minutes difference.
    // true -> update the weather information
    // false -> does nothing for the service request
    public static boolean shouldUpdateWeatherInfo(Context context, String newDate) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String currentDate = sp.getString(PREFS_DATE_STRING, null);

        if (currentDate == null) {
            return true;
        }

        SimpleDateFormat format = new SimpleDateFormat(TIME_PATTERN);
        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(currentDate);
            d2 = format.parse(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diff = d2.getTime() - d1.getTime();
        long minutes = TimeUnit.MICROSECONDS.toMinutes(diff);

        // update weather if it is 60 minutes or longer
        //if(minutes > 59){
        if (minutes > 1) {
            return true;
        } else {
            return false;
        }

    }

    public static String getWeatherInfo(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREFS_WEATHER_INFO, null);
    }

    public static boolean setWeatherInfo(Context context, String weatherInfo) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(PREFS_WEATHER_INFO, weatherInfo);

        return editor.commit();
    }

}
