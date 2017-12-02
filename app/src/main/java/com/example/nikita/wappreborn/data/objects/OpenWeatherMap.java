
package com.example.nikita.wappreborn.data.objects;

import com.example.nikita.wappreborn.data.objects.*;
import com.example.nikita.wappreborn.data.objects.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpenWeatherMap {
    private com.example.nikita.wappreborn.data.objects.City city;
    private java.util.List<com.example.nikita.wappreborn.data.objects.List> list = null;

    public com.example.nikita.wappreborn.data.objects.City getCity() {
        return city;
    }

    public java.util.List<com.example.nikita.wappreborn.data.objects.List> getList() {
        return list;
    }
}
