
package com.example.nikita.wappreborn.data.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchLocation {
    private int cnt;
    private double message;
    private String cod;
    private City city;

    private java.util.List<List> list = null;
    public City getCity() {
        return city;
    }

    public String getCod() {
        return cod;
    }

    public Double getMessage() {
        return message;
    }

    public Integer getCnt() {
        return cnt;
    }

    public java.util.List<List> getList() {
        return list;
    }



}
