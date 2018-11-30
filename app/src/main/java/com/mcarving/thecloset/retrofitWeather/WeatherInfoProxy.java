package com.mcarving.thecloset.retrofitWeather;

import com.mcarving.thecloset.BuildConfig;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherInfoProxy {
    // api.openweathermap.org/data/2.5/weather?zip=94040,us&appid=jgsaslgdkjls
    String API_KEY = BuildConfig.OPEN_WEATHER_API_KEY;
    String WEB_URL = "https://api.openweathermap.org";

    @GET("/data/2.5/weather?")
    Call<WeatherResult> getWeatherResult(@Query("zip") String zipCode,
                                  @Query("appid") String apiKey);
}
