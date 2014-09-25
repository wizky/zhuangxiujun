package com.campingfun.vacancyhunter.campsitehunter;

import com.campingfun.vacancyhunter.campsitehunter.Entity.Campground;

/**
 * Created by wayliu on 9/1/2014.
 */
public class Event_ShowCampgroundDetails {
    private Campground campground;

    public Event_ShowCampgroundDetails(Campground campground) {
        this.campground = campground;
    }

    public Campground getCampground() {
        return campground;
    }
}
