package com.campingfun.vacancyhunter.campsitehunter;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.campingfun.vacancyhunter.campsitehunter.Active.ActiveService;
import com.campingfun.vacancyhunter.campsitehunter.Entity.GooglePlace;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wayliu on 8/31/2014.
 */
public class LocationAutoCompleteAdapter extends ArrayAdapter<GooglePlace> implements Filterable{
    private ArrayList<GooglePlace> resultList;
    private Map<String, GooglePlace> resultDictionary;

    public LocationAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public GooglePlace findEntryByName(String name) {
        return resultDictionary.get(name);
    }
    @Override
    public int getCount() {
        return resultDictionary.size();
    }

    @Override
    public GooglePlace getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultDictionary = GooglePlaceApi.GetAutoSuggestPlaces(constraint.toString());
                    resultList = new ArrayList<GooglePlace>(resultDictionary.values());
                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }



//
//    private static final String LOG_TAG = "ExampleApp";
//
//    private static final String PLACES_API_BASE = "http://www.recreation.gov/ajax/";
//    private static final String TYPE_AUTOCOMPLETE = "/AutoCompleteRecreation";
//    private static final String OUT_JSON = "/json";
//
//    public static ArrayList<CharSequence> Autocomplete(String input) {
//        ArrayList<CharSequence> resultList = null;
//
//        HttpURLConnection conn = null;
//        StringBuilder jsonResults = new StringBuilder();
//        try {
//            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE);
//            sb.append("?location=" + URLEncoder.encode(input, "utf8"));
//
//            URL url = new URL(sb.toString());
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            InputStreamReader in = new InputStreamReader(conn.getInputStream());
//
//            // Load the results into a StringBuilder
//            int read;
//            char[] buff = new char[1024];
//            while ((read = in.read(buff)) != -1) {
//                jsonResults.append(buff, 0, read);
//            }
//        } catch (MalformedURLException e) {
//            Log.e(LOG_TAG, "Error processing Places API URL", e);
//            return resultList;
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "Error connecting to Places API", e);
//            return resultList;
//        } finally {
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
//
//        try {
//            // Create a JSON object hierarchy from the results
//            JSONArray predsJsonArray = new JSONArray(jsonResults.toString());
//
//            // Extract the Place descriptions from the results
//            resultList = new ArrayList<CharSequence>(predsJsonArray.length());
//            for (int i = 0; i < predsJsonArray.length(); i++) {
//                resultList.add(Html.fromHtml(predsJsonArray.getJSONObject(i).getString("parkName")));
//            }
//        } catch (JSONException e) {
//            Log.e(LOG_TAG, "Cannot process JSON results", e);
//        }
//
//        return resultList;
//    }
}
