package com.example.nikita.wappreborn.screen.main;

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
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by Nikita on 26.11.2017.
 */

public class MainPresenter implements MainContract.IMainPresenter{

    private static final double CONST_FOR_TRANSLATION_TEMPERATURE_1 = 1.8;
    private static final double CONST_FOR_TRANSLATION_TEMPERATURE_2 = 459.67;
    private static final int CONST_FOR_TRANSLATION_TEMPERATURE_3 = 32;
    private static final double CONST_FOR_TRANSLATION_TEMPERATURE_4 = 0.55555555556;
    private static final int BEGINNING_OF_DAY_NAME = 4;
    private static final int END_OF_DAY_NAME = 11;
    private static final int BEGINNING_OF_DAY_NAME_FROM_API = 0;
    private static final int END_OF_DAY_NAME_FROM_API = 3;
    private static final String MONDAY = "Mon";
    private static final String TUESDAY = "Tue";
    private static final String WEDNESDAY = "Wed";
    private static final String THURSDAY = "Thu";
    private static final String FRIDAY = "Fri";
    private static final String SATURDAY = "Sat";
    private static final String SUNDAY = "Sun";

    private ApiUtils mApiUtils;
    private MainContract.IMainView mMainView;
    private OpenWeatherMap mWeather;
    private final CompositeDisposable  mDisposables = new CompositeDisposable();

    public MainPresenter(MainContract.IMainView mainView){
        this.mMainView = mainView;
    }

    @Override
    public void start() {
        mApiUtils = new ApiUtils();
    }

    @Override
    public void fetchWeather(String city) {
        Disposable weather = mApiUtils.getAnswers(city)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<OpenWeatherMap>() {
                    @Override
                    public void onNext(OpenWeatherMap weather) {
                        mWeather = weather;
                        updateData();
                    }
                    @Override
                    public void onError(Throwable e) {
                        fetchErrors(e);
                    }
                    @Override
                    public void onComplete() {
                        mMainView.setEnabledButtonsInView();
                    }});
        mDisposables.add(weather);
    }

    @Override
    public void fetchCityWithCoordinates(Coordinates coord) {
        double lat = coord.latitude;
        double lon = coord.longitude;
        Disposable weather = mApiUtils.getAnswers(lat, lon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<SearchLocation>() {
                    @Override
                    public void onNext(SearchLocation location) {
                        String city = location.getCity().getName() + ", " + location.getCity().getCountry();
                        mMainView.showLocationInView(city);
                    }
                    @Override
                    public void onError(Throwable e) {
                        fetchErrors(e);
                    }
                    @Override
                    public void onComplete() { }});
        mDisposables.add(weather);
    }


    @Override
    public void fetchCoordinatesForMapActivity(String city) {
        Disposable weather = mApiUtils.getAnswers(city)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<OpenWeatherMap>() {
                    @Override
                    public void onNext(OpenWeatherMap weather) {
                        double latitude = weather.getCity().getCoord().getLat();
                        double longitude = weather.getCity().getCoord().getLon();
                        Coordinates coord = new Coordinates(latitude, longitude);
                        mMainView.setCoordinates(coord);
                    }
                    @Override
                    public void onError(Throwable e) {
                        fetchErrors(e);
                    }
                    @Override
                    public void onComplete() { }});
        mDisposables.add(weather);
    }

    @Override
    public void updateData(){
        mMainView.showDateInView(getDate());
        mMainView.showCityInView(getCity());
        mMainView.showTemperatureDayInView(getTemperatureDay());
        mMainView.showTemperatureNightInView(getTemperatureNight());
        mMainView.showConditionInView(getCondition());
        mMainView.showImagesInView(getImagesId());
    }

    @Override
    public String getDate() {
        long dateLong = mWeather.getList().get(mMainView.getCurrentDay()).getDt();
        Date date = convertUnixTimestampToDate(dateLong);
        String dateStr = date.toString();
        String dayOfWeek = "";
        switch (dateStr.substring(BEGINNING_OF_DAY_NAME_FROM_API, END_OF_DAY_NAME_FROM_API)) {
            case MONDAY:
                dayOfWeek = "Monday";
                break;
            case TUESDAY:
                dayOfWeek = "Tuesday";
                break;
            case WEDNESDAY:
                dayOfWeek = "Wednesday";
                break;
            case THURSDAY:
                dayOfWeek = "Thursday";
                break;
            case FRIDAY:
                dayOfWeek = "Friday";
                break;
            case SATURDAY:
                dayOfWeek = "Saturday";
                break;
            case SUNDAY:
                dayOfWeek = "Sunday";
                break;
        }
        return dateStr.substring(BEGINNING_OF_DAY_NAME, END_OF_DAY_NAME) + " " + dayOfWeek;
    }

    @Override
    public String getCity() {
        String city = mWeather.getCity().getName() + " " + mWeather.getCity().getCountry();
        return city;
    }

    @Override
    public String getTemperatureDay() {
        double tempDouble = mWeather.getList().get(mMainView.getCurrentDay()).getTemp().getDay();
        int temp = (int) ((((tempDouble * CONST_FOR_TRANSLATION_TEMPERATURE_1 - CONST_FOR_TRANSLATION_TEMPERATURE_2))
                - CONST_FOR_TRANSLATION_TEMPERATURE_3) * CONST_FOR_TRANSLATION_TEMPERATURE_4);
        if (temp > 0) {
            return String.valueOf("Day +" + temp + "°C");
        }
        else{
            return String.valueOf(String.valueOf("Day " + temp + "°C"));
        }
    }

    @Override
    public String getTemperatureNight() {
        double tempDouble = mWeather.getList().get(mMainView.getCurrentDay()).getTemp().getNight();
        int temp = (int) ((((tempDouble * CONST_FOR_TRANSLATION_TEMPERATURE_1 - CONST_FOR_TRANSLATION_TEMPERATURE_2))
                - CONST_FOR_TRANSLATION_TEMPERATURE_3) * CONST_FOR_TRANSLATION_TEMPERATURE_4);
        if (temp > 0) {
            return String.valueOf("Night +" + temp + "°C");
        }
        else {
            return String.valueOf("Night " + temp + "°C");
        }
    }

    @Override
    public String getCondition() {
        String condition = mWeather.getList().get(mMainView.getCurrentDay()).getWeather().get(0).getDescription();
        return condition;
    }

    @Override
    public int getImagesId() {
        int id = mWeather.getList().get(mMainView.getCurrentDay()).getWeather().get(0).getId();
        return id;
    }

    @Override
    public void fetchErrors(Throwable e) {
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

    public static Date convertUnixTimestampToDate(long timestamp) {
        Date date = new Date(timestamp * 1000L);
        return date;
    }

    @Override
    public void stop() {
        mDisposables.clear();
    }
}
