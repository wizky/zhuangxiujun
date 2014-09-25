package com.campingfun.vacancyhunter.campsitehunter.Entity;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by wayliu on 9/1/2014.
 */
public class GooglePlace {
    private String description;
    private String placeId;

    public GooglePlace(String description, String placeId) {
        this.description = description;
        this.placeId = placeId;
    }

    @Override
    public String toString() {
        return this.description;
    }
    public String getDescription() {
        return description;
    }

    public String getPlaceId() {
        return placeId;
    }
}
