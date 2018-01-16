
package com.example.nikita.wappreborn.data.model;

public class City {
    private int id;
    private int population;
    private String name;
    private String country;
    private Coord coord;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public Coord getCoord() {return coord; }
}
