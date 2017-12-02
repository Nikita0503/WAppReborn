package com.example.nikita.wappreborn.screen.main;

import android.content.Context;

/**
 * Created by Nikita on 26.11.2017.
 */

public interface IMainView {
    int getCurrentDay();
    String getCity();

    void setLocation(String place);
    void setDate(String date);
    void setCity(String city);
    void setTemperatureDay(String temp);
    void setTemperatureNight(String temp);
    void setCondition(String condition);
    void setImages(int[] id);
    void setFullScreen();
    void setLatLon(double[] coord);

    boolean checkInternet();
    void isOnline(int id);
    void saveText();
    void loadText();

    void defineViews();
    void setListeners();
    Context getWApplicationContext();


}
