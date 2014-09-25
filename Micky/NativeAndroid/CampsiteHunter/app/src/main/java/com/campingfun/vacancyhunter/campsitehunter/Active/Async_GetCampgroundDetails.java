package com.campingfun.vacancyhunter.campsitehunter.Active;

import android.os.AsyncTask;

import com.campingfun.vacancyhunter.campsitehunter.Entity.Campground;
import com.campingfun.vacancyhunter.campsitehunter.Entity.ShowCampgroundsEvent;
import com.campingfun.vacancyhunter.campsitehunter.Event_ShowCampgroundDetails;

import de.greenrobot.event.EventBus;

/**
 * Created by wayliu on 9/1/2014.
 */
public class Async_GetCampgroundDetails extends AsyncTask<Campground, String, String>{

    @Override
    protected String doInBackground(Campground... params) {
        Campground campground = ActiveService.GetCampgroundDetails(params[0]);
        Event_ShowCampgroundDetails showCampgroundsEvent = new Event_ShowCampgroundDetails(campground);
        EventBus.getDefault().post(showCampgroundsEvent);

        return "Completed";
    }
}
