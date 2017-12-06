package com.example.nikita.wappreborn.screen.main;

import android.content.Context;
import android.location.Location;

import com.example.nikita.wappreborn.BaseContract;

/**
 * Created by Nikita on 06.12.2017.
 */

public interface MainContract extends BaseContract {
    interface IMainView extends BaseView{
        int getCurrentDay();
        String getCityFromView();
        double[] getCoordinates();
        void showDateInView(String date);
        void showCityInView(String city);
        void showTemperatureDayInView(String temp);
        void showTemperatureNightInView(String temp);
        void showConditionInView(String condition);
        void showBackgroundImageInView(int id);
        void showIconImageInView(int id);
        void showLocationInView(String place);
        void showNetworkConnectionError();
        void showDataError(String error);
        void setFullScreen();
        void setCoordinates(double[] coord);
        void setListeners();
        void saveCity();
        void loadCity();
        void startMapActivity();
        void setEnabledButtonsInView();
        void setNotEnabledButtonsInView();
    }

    interface IMainPresenter extends BasePresenter{
        void fetchWeather();
        void fetchCoordinates();
        void fetchLocation();
        String getDate();
        String getCity();
        String getTemperatureDay();
        String getTemperatureNight();
        String getCondition();
        void fetchImages();
        void updateData();
    }
}
