package com.example.nikita.wappreborn.data.network;

import com.example.nikita.wappreborn.data.objects.SearchLocation;
import com.example.nikita.wappreborn.data.objects.OpenWeatherMap;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.Flowable;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Nikita on 19.10.2017.
 */

public class ApiUtils implements IWeatherService {

    public static final String BASE_URL = "http://api.openweathermap.org/";

    @Override
    public Flowable<OpenWeatherMap> getAnswers(String q) {
        Retrofit retrofit = getClient(BASE_URL);
        IWeatherService weatherService = retrofit.create(IWeatherService.class);
        return weatherService.getAnswers(q);
    }

    @Override
    public Flowable<SearchLocation> getAnswers(double lat, double lon) {
        Retrofit retrofit = getClient(BASE_URL);
        IWeatherService weatherService = retrofit.create(IWeatherService.class);
        return weatherService.getAnswers(lat, lon);
    }

    public static Retrofit getClient(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit;
    }
}