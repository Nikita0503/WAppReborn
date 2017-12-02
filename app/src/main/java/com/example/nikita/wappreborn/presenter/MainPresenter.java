package com.example.nikita.wappreborn.presenter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.nikita.wappreborn.R;
import com.example.nikita.wappreborn.data.network.ApiUtils;
import com.example.nikita.wappreborn.data.objects.OpenWeatherMap;
import com.example.nikita.wappreborn.data.objects.SearchLocation;
import com.example.nikita.wappreborn.screen.main.IMainView;

import java.util.Date;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by Nikita on 26.11.2017.
 */

public class MainPresenter implements IMainPresenter{
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

    private ApiUtils mApiUtils;
    private IMainView mMainView;
    private OpenWeatherMap mWeather;
    private Context mContext;
    public MainPresenter(IMainView mainView, Context context){
        mApiUtils = new ApiUtils();
        mContext = context;
        this.mMainView = mainView;
    }

    @Override
    public void getWeather() {
        Flowable<OpenWeatherMap> weather = mApiUtils.getAnswers(mMainView.getCity());
        weather.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<OpenWeatherMap>() {
                    @Override
                    public void onNext(OpenWeatherMap weather) {
                        mWeather = weather;
                        updateData();
                    }
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mMainView.getWApplicationContext(), "Unknown city", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onComplete() {

                    }});
    }

    @Override
    public void getLocation() {
        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext.getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        Flowable<SearchLocation> weather = mApiUtils.getAnswers(lat, lon);
        weather.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<SearchLocation>() {
                    @Override
                    public void onNext(SearchLocation location) {
                        String place = location.getCity().getName() + ", " + location.getCity().getCountry();
                        mMainView.setLocation(place);
                    }
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mMainView.getWApplicationContext(), "Unknown place", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onComplete() {

                    }});
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
        int[] images = getImages(condition);
        if(images != null){
            mMainView.setImages(images);
        }
        return condition;
    }

    @Override
    public int[] getImages() {
        int[] images = new int[2];
        int id = mWeather.getList().get(mMainView.getCurrentDay()).getWeather().get(0).getId();
        if (id == ID_CLEARSKY) {
            images[0] = R.drawable.sunny;
            images[1] = R.drawable.sunnybackground;
            return  images;
        } else {
            switch (id / 100) {
                case ID_SUNNYCLOUD:;
                    images[0] = R.drawable.sunnycloud;
                    images[1] = R.drawable.sunnycloudbackground;
                    break;
                case ID_CLOUDS:
                    images[0] = R.drawable.cloud;
                    images[1] = R.drawable.cloudsbackground;
                    break;
                case ID_SNOW:
                    images[0] = R.drawable.snow;
                    images[1] = R.drawable.snowbackground;
                    break;
                case ID_RAIN:
                    images[0] = R.drawable.rain;
                    images[1] = R.drawable.rainbackground;
                    break;
                case ID_DRIZZLE:
                    images[0] = R.drawable.drizzle;
                    images[1] = R.drawable.drizzlebackground;
                    break;
                case ID_THUNDER:
                    images[0] = R.drawable.thunder;
                    images[1] = R.drawable.thunderbackground;
                    break;
            }
            return images;
        }
    }

    @Override
    public int[] getImages(String condition) {
        int[] images = new int[2];
        if (condition.equals(CONDITION_FROM_API_LIGHT_SNOW) || condition.equals(CONDITION_FROM_API_LIGHT_RAIN_AND_SNOW) || condition.equals(CONDITION_FROM_API_LIGHT_SHOWER_SNOW)) { //перенести в Presenter
            images[0] = R.drawable.smallsnow;
            images[1] = R.drawable.snowbackground;
            return images;
        }
        else if (condition.equals(CONDITION_FROM_API_SNOW) || condition.equals(CONDITION_FROM_API_RAIN_AND_SNOW) || condition.equals(CONDITION_FROM_API_SHOWER_SNOW)) {
            images[0] = R.drawable.snow;
            images[1] = R.drawable.snowbackground;
            return images;
        }
        else if (condition.equals(CONDITION_FROM_API_HEAVY_SNOW) || condition.equals(CONDITION_FROM_API_HEAVY_SHOWER_SNOW)) {
            images[0] = R.drawable.heavysnow;
            images[1] = R.drawable.snowbackground;
            return images;
        }
        else if (condition.equals(CONDITION_FROM_API_LIGHT_RAIN)) {
            images[0] = R.drawable.rain;
            images[1] = R.drawable.drizzlebackground;
            return images;
        }
        else if (condition.equals(CONDITION_FROM_API_MODERATE_RAIN)) {
            images[0] = R.drawable.drizzle;
            images[1] = R.drawable.rainbackground;
            return images;
        }
        else return null;
    }

    @Override
    public void getLatLon() {
        Flowable<OpenWeatherMap> weather = mApiUtils.getAnswers(mMainView.getCity());
        weather.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<OpenWeatherMap>() {
                    @Override
                    public void onNext(OpenWeatherMap weather) {
                        double coord[] = new double[2];
                        mWeather = weather;
                        coord[0] = mWeather.getCity().getCoord().getLat();
                        coord[1] = mWeather.getCity().getCoord().getLon();
                        mMainView.setLatLon(coord);
                    }
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mMainView.getWApplicationContext(), "Unknown city", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onComplete() {

                    }});
    }

    @Override
    public void updateData(){
        mMainView.setDate(getDate());
        mMainView.setCity(getCity());
        mMainView.setTemperatureDay(getTemperatureDay());
        mMainView.setTemperatureNight(getTemperatureNight());
        mMainView.setImages(getImages());
        mMainView.setCondition(getCondition());
    }

    public static Date convertUnixTimestampToDate(long timestamp) {
        Date date = new Date(timestamp * 1000L);
        return date;
    }
}
