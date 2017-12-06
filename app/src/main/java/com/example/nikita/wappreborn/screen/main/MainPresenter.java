package com.example.nikita.wappreborn.screen.main;

import com.example.nikita.wappreborn.R;
import com.example.nikita.wappreborn.data.network.ApiUtils;
import com.example.nikita.wappreborn.data.model.OpenWeatherMap;
import com.example.nikita.wappreborn.data.model.SearchLocation;

import java.util.Date;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by Nikita on 26.11.2017.
 */

public class MainPresenter implements MainContract.IMainPresenter{
    private static final int LAT = 0;
    private static final int LON = 1;
    private static final int ID_CLEARSKY = 800;
    private static final int ID_SUNNYCLOUD = 8;
    private static final int ID_CLOUDS = 7;
    private static final int ID_SNOW = 6;
    private static final int ID_RAIN = 5;
    private static final int ID_DRIZZLE = 3;
    private static final int ID_THUNDER = 2;
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
    private static final String CONDITION_FROM_API_LIGHT_SNOW = "light snow";
    private static final String CONDITION_FROM_API_LIGHT_RAIN_AND_SNOW = "light rain and snow";
    private static final String CONDITION_FROM_API_LIGHT_SHOWER_SNOW = "light shower snow";
    private static final String CONDITION_FROM_API_SNOW = "snow";
    private static final String CONDITION_FROM_API_RAIN_AND_SNOW = "rain and snow";
    private static final String CONDITION_FROM_API_SHOWER_SNOW = "shower snow";
    private static final String CONDITION_FROM_API_HEAVY_SNOW = "heavy snow";
    private static final String CONDITION_FROM_API_HEAVY_SHOWER_SNOW = "heavy shower snow";
    private static final String CONDITION_FROM_API_LIGHT_RAIN = "light rain";
    private static final String CONDITION_FROM_API_MODERATE_RAIN = "moderate rain";
    private String mCondition;
    private int mIconImageId;
    private int mBackgroundImageId;
    private ApiUtils mApiUtils;
    private MainContract.IMainView mMainView;
    private OpenWeatherMap mWeather;
    private CompositeDisposable mDisposables;
    public MainPresenter(MainContract.IMainView mainView){
        mApiUtils = new ApiUtils();
        this.mMainView = mainView;
        start();
    }

    @Override
    public void fetchWeather() {
        Flowable<OpenWeatherMap> weather = mApiUtils.getAnswers(mMainView.getCityFromView());
        mDisposables.add(weather.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<OpenWeatherMap>() {
                    @Override
                    public void onNext(OpenWeatherMap weather) {
                        mWeather = weather;
                        updateData();
                    }
                    @Override
                    public void onError(Throwable e) {
                        if((e.toString()).equals("com.jakewharton.retrofit2.adapter.rxjava2.HttpException: HTTP 404 Not Found")) {
                            mMainView.showDataError("Unknown city");
                        }else if(e.toString().equals("com.jakewharton.retrofit2.adapter.rxjava2.HttpException: HTTP 400 Bad Request")){
                            mMainView.showDataError("Enter correct city");
                        }else{
                            mMainView.showNetworkConnectionError();
                        }
                    }
                    @Override
                    public void onComplete() {
                        mMainView.setEnabledButtonsInView();
                    }}));
    }

    @Override
    public void fetchLocation() {
        double[] coord = mMainView.getCoordinates();
        double lat = coord[LAT];
        double lon = coord[LON];
        Flowable<SearchLocation> weather = mApiUtils.getAnswers(lat, lon);
        mDisposables.add(weather.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<SearchLocation>() {
                    @Override
                    public void onNext(SearchLocation location) {
                        String city = location.getCity().getName() + ", " + location.getCity().getCountry();
                        mMainView.showLocationInView(city);
                    }
                    @Override
                    public void onError(Throwable e) {
                        if((e.toString()).equals("com.jakewharton.retrofit2.adapter.rxjava2.HttpException: HTTP 404 Not Found")){
                            mMainView.showDataError("Unknown geolocation");
                        }else{
                            mMainView.showNetworkConnectionError();
                        }
                    }
                    @Override
                    public void onComplete() { }}));
    }

    @Override
    public void fetchCoordinates() {
        Flowable<OpenWeatherMap> weather = mApiUtils.getAnswers(mMainView.getCityFromView());
        mDisposables.add(weather.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<OpenWeatherMap>() {
                    @Override
                    public void onNext(OpenWeatherMap weather) {
                        double coord[] = new double[2];
                        mWeather = weather;
                        coord[LAT] = mWeather.getCity().getCoord().getLat();
                        coord[LON] = mWeather.getCity().getCoord().getLon();
                        mMainView.setCoordinates(coord);
                        mMainView.startMapActivity();
                    }
                    @Override
                    public void onError(Throwable e) {
                        if((e.toString()).equals("com.jakewharton.retrofit2.adapter.rxjava2.HttpException: HTTP 404 Not Found")) {
                            mMainView.showDataError("Unknown city");
                        }else if(e.toString().equals("com.jakewharton.retrofit2.adapter.rxjava2.HttpException: HTTP 400 Bad Request")){
                            mMainView.showDataError("Enter correct city");
                        }else{
                            mMainView.showNetworkConnectionError();
                        }
                    }
                    @Override
                    public void onComplete() { }}));
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
        mCondition = condition;
        return condition;
    }

    @Override
    public void fetchImages() {
        int id = mWeather.getList().get(mMainView.getCurrentDay()).getWeather().get(0).getId();
        if (id == ID_CLEARSKY) {
            mIconImageId = R.drawable.sunny;
            mBackgroundImageId = R.drawable.sunnybackground;
        } else {
            switch (id / 100) {
                case ID_SUNNYCLOUD:;
                    mIconImageId = R.drawable.sunnycloud;
                    mBackgroundImageId = R.drawable.sunnycloudbackground;
                    break;
                case ID_CLOUDS:
                    mIconImageId = R.drawable.cloud;
                    mBackgroundImageId = R.drawable.cloudsbackground;
                    break;
                case ID_SNOW:
                    mIconImageId = R.drawable.snow;
                    mBackgroundImageId = R.drawable.snowbackground;
                    break;
                case ID_RAIN:
                    mIconImageId = R.drawable.rain;
                    mBackgroundImageId = R.drawable.rainbackground;
                    break;
                case ID_DRIZZLE:
                    mIconImageId = R.drawable.drizzle;
                    mBackgroundImageId = R.drawable.drizzlebackground;
                    break;
                case ID_THUNDER:
                    mIconImageId = R.drawable.thunder;
                    mBackgroundImageId = R.drawable.thunderbackground;
                    break;
            }
        }
        if (mCondition.equals(CONDITION_FROM_API_LIGHT_SNOW) || mCondition.equals(CONDITION_FROM_API_LIGHT_RAIN_AND_SNOW) || mCondition.equals(CONDITION_FROM_API_LIGHT_SHOWER_SNOW)) {
            mIconImageId = R.drawable.smallsnow;
            mBackgroundImageId = R.drawable.snowbackground;
        }
        else if (mCondition.equals(CONDITION_FROM_API_SNOW) || mCondition.equals(CONDITION_FROM_API_RAIN_AND_SNOW) || mCondition.equals(CONDITION_FROM_API_SHOWER_SNOW)) {
            mIconImageId = R.drawable.snow;
            mBackgroundImageId = R.drawable.snowbackground;
        }
        else if (mCondition.equals(CONDITION_FROM_API_HEAVY_SNOW) || mCondition.equals(CONDITION_FROM_API_HEAVY_SHOWER_SNOW)) {
            mIconImageId = R.drawable.heavysnow;
            mBackgroundImageId = R.drawable.snowbackground;
        }
        else if (mCondition.equals(CONDITION_FROM_API_LIGHT_RAIN)) {
            mIconImageId = R.drawable.rain;
            mBackgroundImageId = R.drawable.drizzlebackground;
        }
        else if (mCondition.equals(CONDITION_FROM_API_MODERATE_RAIN)) {
            mIconImageId = R.drawable.drizzle;
            mBackgroundImageId = R.drawable.rainbackground;
        }
    }

    @Override
    public void updateData(){
        mMainView.showDateInView(getDate());
        mMainView.showCityInView(getCity());
        mMainView.showTemperatureDayInView(getTemperatureDay());
        mMainView.showTemperatureNightInView(getTemperatureNight());
        mMainView.showConditionInView(getCondition());
        fetchImages();
        mMainView.showIconImageInView(mIconImageId);
        mMainView.showBackgroundImageInView(mBackgroundImageId);
    }

    public static Date convertUnixTimestampToDate(long timestamp) {
        Date date = new Date(timestamp * 1000L);
        return date;
    }

    @Override
    public void start() {
        mDisposables = new CompositeDisposable();
    }

    @Override
    public void stop() {
        mDisposables.dispose();
        mDisposables.clear();
    }
}
