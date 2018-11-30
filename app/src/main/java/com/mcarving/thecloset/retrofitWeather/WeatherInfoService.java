package com.mcarving.thecloset.retrofitWeather;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mcarving.thecloset.R;
import com.mcarving.thecloset.data.MyPreferences;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherInfoService extends IntentService {

    private static final String TAG = "WeatherInfoService";

    private static final double ABSOLUTE_ZERO_TEMP = -459.67; //in Fahrenheit scale


    public WeatherInfoService() {
        super("WeatherInfoService");
    }

    public static void startWeatherInfoService(Context context) {
        Intent i = new Intent(context, WeatherInfoService.class);

        context.startService(i);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String timeStamp = new SimpleDateFormat(MyPreferences.TIME_PATTERN)
                .format(Calendar.getInstance().getTime());

        // check preference if the weather is up-to-date, 60 minutes difference
        // if longer than 60 minutes, run the weather update
        // and store in preference, date & weather info string
        // if not, ignore the service request
        if (MyPreferences.shouldUpdateWeatherInfo(getApplicationContext(), timeStamp)) {
            //Log.d(TAG, "onHandleIntent: loading data");
            loadWeatherData();
        } else {
            //Log.d(TAG, "onHandleIntent: ignore weather update request");
        }

    }

    // retrieve weahter data from OpenWeatherMap api
    public void loadWeatherData() {
        WeatherInfoProxy retrofitRequest =
                new Retrofit.Builder()
                        .baseUrl(WeatherInfoProxy.WEB_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(WeatherInfoProxy.class);

        String regex = "^[0-9]{5}";
        Pattern pattern = Pattern.compile(regex);

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String zipCodeStr = sp.getString(getResources().getString(R.string.pref_zipcode_key), "");

        Matcher matcher = pattern.matcher(zipCodeStr);
        if (!matcher.matches()) {
            Log.d(TAG,
                    "loadWeatherData: Invalid ZIP code! Please enter a valid ZIP code in settings.");
        } else {
            Log.d(TAG, "loadWeatherData: getting data from the web");

            Call<WeatherResult> call = retrofitRequest
                    .getWeatherResult(zipCodeStr + ",us", WeatherInfoProxy.API_KEY);


            try {
                WeatherResult result = call.execute().body();

                String weatherDescription = "";
                double temp = ABSOLUTE_ZERO_TEMP;

                try {
                    Weather weather = result.getWeather().get(0);
                    weatherDescription = weather.getDescription();

                    Main main = result.getMain();
                    temp = main.getTemp();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                temp = temp - 273.15; // convert Kelvin to Celsius
                temp = Math.round(temp * 9 / 5 + 32); // convert Celsius to Fahrenheit
                String weatherInfo = weatherDescription
                        + "\n"
                        + temp + " Fahrenheit";
                saveWeatherInfoToPreference(getApplicationContext(), weatherInfo);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "onFailure: Retrofit error");
                Log.d(TAG, "onFailure: can't retrieve weather data");
            }

            /*
            call.enqueue(new Callback<WeatherResult>() {
                @Override
                public void onResponse(Call<WeatherResult> call, Response<WeatherResult> response) {
                    // save the weather string and current date into preference
                    // get current date

                    String weatherDescription = "";
                    double temp = ABSOLUTE_ZERO_TEMP;
                    WeatherResult result = response.body();
                    try {
                        Weather weather = result.getWeather().get(0);
                        weatherDescription = weather.getDescription();
                        Log.d(TAG, "onResponse: weather description = " + weatherDescription);

                        Main main = result.getMain();
                        temp = main.getTemp();
                        Log.d(TAG, "onResponse: temp = " + temp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    temp = temp - 273.15; // convert Kelvin to Celsius
                    temp = Math.round(temp * 9 / 5 + 32); // convert Celsius to Fahrenheit
                    String weatherInfo = weatherDescription
                            + "\n"
                            + temp + " Fahrenheit";
                    saveWeatherInfoToPreference(getApplicationContext(), weatherInfo);
                }

                @Override
                public void onFailure(Call<WeatherResult> call, Throwable t) {
                    Log.d(TAG, "onFailure: Retrofit error");
                    Log.d(TAG, "onFailure: can't retrieve weather data");
                }
            }); */
        }
    }

    // save dateStamp and weatherInfo to Preference
    public void saveWeatherInfoToPreference(Context context, String weatherInfo) {
        String timeStamp = new SimpleDateFormat(MyPreferences.TIME_PATTERN)
                .format(Calendar.getInstance().getTime());

        MyPreferences.setCurrentDate(context, timeStamp);

        MyPreferences.setWeatherInfo(context, weatherInfo);
    }
}
