package com.campingfun.vacancyhunter.campsitehunter.Entity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by wayliu on 8/31/2014.
 */
public class ShowCampgroundsEvent {
    private ArrayList<Campground> campgrounds;
    private LatLng placeSearched_Coordinate;
    private String placeSearched_Name;

    public ShowCampgroundsEvent(ArrayList<Campground> campgrounds, LatLng coordinate, String name) {
        this.campgrounds = campgrounds;
        this.placeSearched_Coordinate = coordinate;
        this.placeSearched_Name = name;
    }

    public ArrayList<Campground> getCampgrounds() {
        return campgrounds;
    }

    public void setCampgrounds(ArrayList<Campground> campgrounds) {
        this.campgrounds = campgrounds;
    }

    public LatLng getPlaceSearched_Coordinate() {
        return placeSearched_Coordinate;
    }

    public void setPlaceSearched_Coordinate(LatLng placeSearched_Coordinate) {
        this.placeSearched_Coordinate = placeSearched_Coordinate;
    }

    public String getPlaceSearched_Name() {
        return placeSearched_Name;
    }

    public void setPlaceSearched_Name(String placeSearched_Name) {
        this.placeSearched_Name = placeSearched_Name;
    }
}
