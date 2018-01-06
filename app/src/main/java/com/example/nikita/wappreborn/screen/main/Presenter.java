package com.example.nikita.wappreborn.screen.main;

import android.util.Log;

import com.example.nikita.wappreborn.R;
import com.example.nikita.wappreborn.data.model.Coordinates;
import com.example.nikita.wappreborn.data.network.ApiUtils;
import com.example.nikita.wappreborn.data.model.OpenWeatherMap;
import com.example.nikita.wappreborn.data.model.SearchLocation;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import java.util.Date;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by Nikita on 26.11.2017.
 */

public class Presenter implements MainContract.Presenter{
    public static final String ERROR = "ERROR";

    private ApiUtils mApiUtils;
    private MainContract.View mMainView;
    private final CompositeDisposable  mDisposables = new CompositeDisposable();

    public Presenter(MainContract.View mainView){
        this.mMainView = mainView;
    }

    @Override
    public void start() {
        mApiUtils = new ApiUtils();
    }

    @Override
    public void fetchWeather(String city) {
        Disposable disposable = mApiUtils.getAnswers(city)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<OpenWeatherMap>() {
                    @Override
                    public void onSuccess(OpenWeatherMap weather) {
                        mMainView.showWeatherInView(weather);
                        mMainView.setEnabledButtonsInView();
                    }
                    @Override
                    public void onError(Throwable e) {
                        handleErrors(e);
                    }
                });
        mDisposables.add(disposable);
    }

    @Override
    public void fetchCityWithCoordinates(Coordinates coord) {
        double lat = coord.latitude;
        double lon = coord.longitude;
        Disposable disposable = mApiUtils.getAnswers(lat, lon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<SearchLocation>() {
                    @Override
                    public void onSuccess(SearchLocation location) {
                        String city = location.getCity().getName() + ", " + location.getCity().getCountry();
                        mMainView.showLocationInView(city);
                    }
                    @Override
                    public void onError(Throwable e) {
                        handleErrors(e);
                    }
                    });
        mDisposables.add(disposable);
    }

    @Override
    public void fetchCoordinatesForMapActivity(String city) {
        Disposable disposable = mApiUtils.getAnswers(city)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<OpenWeatherMap>() {
                    @Override
                    public void onSuccess(OpenWeatherMap weather) {
                        double latitude = weather.getCity().getCoord().getLat();
                        double longitude = weather.getCity().getCoord().getLon();
                        Coordinates coord = new Coordinates(latitude, longitude);
                        mMainView.showCoordinatesInView(coord);
                    }
                    @Override
                    public void onError(Throwable e) {
                        handleErrors(e);
                    }
                    });
        mDisposables.add(disposable);
    }

    public void handleErrors(Throwable e) {
        Log.d(ERROR, e.getLocalizedMessage());
        if(e instanceof HttpException){
            int exception = ((HttpException) e).code();
            switch (exception){
                case 400:
                    mMainView.showEmptyLineError();
                    break;
                case 404:
                    mMainView.showEnteredCityError();
                    break;
                default:
                    mMainView.showNetworkConnectionError();
            }
        }
    }

    @Override
    public void stop() {
        mDisposables.clear();
    }
}
