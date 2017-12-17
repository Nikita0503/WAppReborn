package com.example.nikita.wappreborn.screen.main;

import com.example.nikita.wappreborn.data.model.Coordinates;
import com.example.nikita.wappreborn.screen.BaseContract;

/**
 * Created by Nikita on 06.12.2017.
 */

public interface MainContract extends BaseContract {
    interface IMainView extends BaseView{
        int getCurrentDay();
        String getCityFromView();
        Coordinates getCoordinates();
        void showDateInView(String date);
        void showCityInView(String city);
        void showTemperatureDayInView(String temp);
        void showTemperatureNightInView(String temp);
        void showConditionInView(String condition);
        void showBackgroundImageInView(int id);
        void showIconImageInView(int id);
        void showLocationInView(String place);
        void showNetworkConnectionError();
        void showEmptyLineError();
        void showEnteredCityError();
        void showImagesInView(int id);
        void setFullScreen();
        void setCoordinates(Coordinates coord);
        void setListeners();
        void saveCityFromView();
        void loadCityToView();
        void startMapActivity();
        void setEnabledButtonsInView();
        void setNotEnabledButtonsInView();
    }

    interface IMainPresenter extends BasePresenter{
        void fetchWeather(String city);
        void fetchCoordinatesForMapActivity(String city);
        void fetchCityWithCoordinates(Coordinates coord);
        void fetchErrors(Throwable e);
        public int getImagesId();
        String getDate();
        String getCity();
        String getTemperatureDay();
        String getTemperatureNight();
        String getCondition();
        void updateData();
    }
}
