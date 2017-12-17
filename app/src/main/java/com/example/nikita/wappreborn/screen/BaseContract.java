package com.example.nikita.wappreborn.screen;

/**
 * Created by Nikita on 06.12.2017.
 */

public interface BaseContract {
    interface BaseView{
        void defineViews();
    }

    interface BasePresenter{
        void start();
        void stop();
    }
}
