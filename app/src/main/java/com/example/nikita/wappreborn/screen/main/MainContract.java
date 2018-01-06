package com.example.nikita.wappreborn.screen.main;

import com.example.nikita.wappreborn.data.model.Coordinates;
import com.example.nikita.wappreborn.data.model.OpenWeatherMap;
import com.example.nikita.wappreborn.screen.BaseContract;

/**
 * Created by Nikita on 06.12.2017.
 */

public interface MainContract extends BaseContract {
    interface View extends BaseView{
        void setEnabledButtonsInView();
        void showLocationInView(String city);
        void showCoordinatesInView(Coordinates coord);
        void showWeatherInView(OpenWeatherMap weather);
        void showNetworkConnectionError();
        void showEmptyLineError();
        void showEnteredCityError();
    }

    interface Presenter extends BasePresenter{
        void fetchWeather(String city);
        void fetchCoordinatesForMapActivity(String city);
        void fetchCityWithCoordinates(Coordinates coord);
    }
}
