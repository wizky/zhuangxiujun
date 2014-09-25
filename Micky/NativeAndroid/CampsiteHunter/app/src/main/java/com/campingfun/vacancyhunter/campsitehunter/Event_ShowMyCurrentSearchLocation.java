package com.campingfun.vacancyhunter.campsitehunter;

import com.campingfun.vacancyhunter.campsitehunter.Entity.GooglePlace;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by wayliu on 9/1/2014.
 */
public class Event_ShowMyCurrentSearchLocation {

    private String name;
    private LatLng coordinate;
    public Event_ShowMyCurrentSearchLocation(String name, LatLng coordinate) {
        this.name = name;
        this.coordinate = coordinate;
    }

    public String getName() {
        return name;
    }

    public LatLng getCoordinate() {
        return coordinate;
    }
}
