package com.example.nikita.wappreborn.data.network;

/**
 * Created by Nikita on 19.10.2017.
 */

import com.example.nikita.wappreborn.data.model.SearchLocation;
import com.example.nikita.wappreborn.data.model.OpenWeatherMap;

import io.reactivex.Flowable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("/data/2.5/forecast/daily?appid=a7566f90e4ed0120ac27665a49f3bc9a")
    Single<OpenWeatherMap> getAnswers(@Query("q") String q);

    @GET("/data/2.5/forecast/daily?appid=a7566f90e4ed0120ac27665a49f3bc9a")
    Single<SearchLocation> getAnswers(@Query("lat") double lat, @Query("lon") double lon);
}
