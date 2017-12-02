package com.example.nikita.wappreborn.presenter;

/**
 * Created by Nikita on 26.11.2017.
 */

public interface IMainPresenter {
    void getWeather();
    void getLocation();
    String getDate();
    String getCity();
    String getTemperatureDay();
    String getTemperatureNight();
    String getCondition();
    int[] getImages();
    int[] getImages(String condition);

    void getLatLon();

    void updateData();

}
