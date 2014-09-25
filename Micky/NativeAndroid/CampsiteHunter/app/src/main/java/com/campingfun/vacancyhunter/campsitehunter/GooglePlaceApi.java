package com.campingfun.vacancyhunter.campsitehunter;

import android.util.Log;

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
 * Created by wayliu on 9/1/2014.
 */
public class GooglePlaceApi {
    private static final String LOG_TAG = GooglePlaceApi.class.getSimpleName();
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_PLACEDETAILS = "/details";
    private static final String OUT_JSON = "/json";

    public static LatLng GetPlaceCoordinateById(String placeId) {
        HttpURLConnection conn = null;
        LatLng coordinate = null;

        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_PLACEDETAILS + OUT_JSON);
            sb.append("?key=" + CommonUtils.GetGoogleApiKey());
            sb.append("&placeid=" + placeId);

            URL url = new URL(sb.toString());
            Log.i(LOG_TAG, url.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return coordinate;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return coordinate;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString()).getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
            coordinate = new LatLng(jsonObj.getDouble("lat"), jsonObj.getDouble("lng"));

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return coordinate;
    }

    public static Map<String, GooglePlace> GetAutoSuggestPlaces(String input) {
            Map<String, GooglePlace> autoCompletePlacesDict = new HashMap<String, GooglePlace>();

            HttpURLConnection conn = null;
            StringBuilder jsonResults = new StringBuilder();
            try {
                StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
                sb.append("?key=" + CommonUtils.GetGoogleApiKey());
                sb.append("&types=geocode");
                sb.append("&components=country:us");
                sb.append("&input=" + URLEncoder.encode(input, "utf8"));

                URL url = new URL(sb.toString());
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                // Load the results into a StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error processing Places API URL", e);
                return autoCompletePlacesDict;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error connecting to Places API", e);
                return autoCompletePlacesDict;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            try {
                // Create a JSON object hierarchy from the results
                JSONObject jsonObj = new JSONObject(jsonResults.toString());
                JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

                // Extract the Place descriptions from the results
                for (int i = 0; i < predsJsonArray.length(); i++) {
                    JSONObject jsonObject = predsJsonArray.getJSONObject(i);
                    GooglePlace newPlace = new GooglePlace(jsonObject.getString("description"), jsonObject.getString("place_id"));
                    autoCompletePlacesDict.put(newPlace.getDescription(), newPlace);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Cannot process JSON results", e);
            }

            return autoCompletePlacesDict;
        }
}
