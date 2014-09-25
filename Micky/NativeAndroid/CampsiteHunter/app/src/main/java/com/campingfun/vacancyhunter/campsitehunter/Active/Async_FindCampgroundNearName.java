package com.campingfun.vacancyhunter.campsitehunter.Active;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.campingfun.vacancyhunter.campsitehunter.Entity.Campground;
import com.campingfun.vacancyhunter.campsitehunter.Entity.GooglePlace;
import com.campingfun.vacancyhunter.campsitehunter.Entity.ShowCampgroundsEvent;
import com.campingfun.vacancyhunter.campsitehunter.Event_ShowMyCurrentSearchLocation;
import com.campingfun.vacancyhunter.campsitehunter.Event_ShowToast;
import com.campingfun.vacancyhunter.campsitehunter.GooglePlaceApi;
import com.campingfun.vacancyhunter.campsitehunter.Utils.ToastHelper;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;

/**
 * Created by wayliu on 9/1/2014.
 */
public class Async_FindCampgroundNearName extends AsyncTask<GooglePlace, String, String>{

    private Context mContext;
    public Async_FindCampgroundNearName(Context context) {
        super();
        this.mContext = context;
    }
    @Override
    protected String doInBackground(GooglePlace... params) {
        GooglePlace place = params[0];
        LatLng location = null;
        if (place.getPlaceId() != null) {
            location = GooglePlaceApi.GetPlaceCoordinateById(place.getPlaceId());
        }
        else
        {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocationName(place.getDescription(), 1);
                if (addressList != null && addressList.size() > 0) {
                    location = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ArrayList<Campground> results = null;
        if (location == null) {
            results = new ArrayList<Campground>();
            EventBus.getDefault().post(new Event_ShowToast("Can't find " + place.getDescription() + ", please change name and try again"));
        } else {
            EventBus.getDefault().post(new Event_ShowMyCurrentSearchLocation(place.getDescription(), location));

            results = ActiveService.GetCampgroundNearLocation(location);
        }

        ShowCampgroundsEvent event = new ShowCampgroundsEvent(results, location, place.toString());
        EventBus.getDefault().post(event);
        return "Finished";
    }
}
