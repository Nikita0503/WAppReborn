
package com.example.nikita.wappreborn.data.model;

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

    public double getMessage() {
        return message;
    }

    public int getCnt() {
        return cnt;
    }

    public java.util.List<List> getList() {
        return list;
    }



}
