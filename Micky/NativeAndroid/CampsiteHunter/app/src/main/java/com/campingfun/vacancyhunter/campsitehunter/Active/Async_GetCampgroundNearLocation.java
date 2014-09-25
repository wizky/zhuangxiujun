package com.campingfun.vacancyhunter.campsitehunter.Active;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.campingfun.vacancyhunter.campsitehunter.Entity.Campground;
import com.campingfun.vacancyhunter.campsitehunter.Entity.GooglePlace;
import com.campingfun.vacancyhunter.campsitehunter.Entity.ShowCampgroundsEvent;
import com.campingfun.vacancyhunter.campsitehunter.Event_ShowMyCurrentSearchLocation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by wayliu on 9/1/2014.
 */
public class Async_GetCampgroundNearLocation extends AsyncTask<LatLng, String, String> {
    private final String LOG_TAG = getClass().getSimpleName();
    private Context mContext;

    public Async_GetCampgroundNearLocation(Context context) {
        this.mContext = context;
    }

    @Override
    protected String doInBackground(LatLng... params) {
        EventBus.getDefault().post(new Event_ShowMyCurrentSearchLocation("Search Location", params[0]));
        ArrayList<Campground> results = ActiveService.GetCampgroundNearLocation(params[0]);
        ShowCampgroundsEvent event = new ShowCampgroundsEvent(results, params[0], "Target Location");
        EventBus.getDefault().post(event);
        return "Finished";
    }
}
