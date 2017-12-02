
package com.example.nikita.wappreborn.data.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class List {
    private int dt;
    private Temp temp;
    private java.util.List<Weather> weather = null;

    public Integer getDt() {
        return dt;
    }

    public Temp getTemp() {
        return temp;
    }

    public java.util.List<Weather> getWeather() {
        return weather;
    }

}
