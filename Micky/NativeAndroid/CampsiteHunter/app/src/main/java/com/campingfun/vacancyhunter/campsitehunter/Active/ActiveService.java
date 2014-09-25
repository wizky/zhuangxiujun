package com.campingfun.vacancyhunter.campsitehunter.Active;

import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.campingfun.vacancyhunter.campsitehunter.CommonUtils;
import com.campingfun.vacancyhunter.campsitehunter.Entity.Campground;
import com.campingfun.vacancyhunter.campsitehunter.Event_ShowMyCurrentSearchLocation;
import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by wayliu on 9/1/2014.
 */
public class ActiveService {
    private static final String API_BASE = "http://api.amp.active.com/camping";
    private static final String TYPE_CAMPGROUNDS = "/campgrounds";
    private static final String TYPE_CAMPGROUND_DETAILS = "/campground/details";
    private static final String ns = null;

    public static ArrayList<Campground> GetCampgroundNearLocation(LatLng location) {
        StringBuilder sb = new StringBuilder(API_BASE + TYPE_CAMPGROUNDS);
        sb.append("?api_key=" + CommonUtils.ActiveApiKey());
        sb.append("&landmarkName=true&xml=true");
        sb.append("&landmarkLat=" + location.latitude);
        sb.append("&landmarkLong=" + location.longitude);
        String url = sb.toString();
//        String url = "http://api.amp.active.com/camping/campgrounds?landmarkName=true&landmarkLat=37.84035&landmarkLong=-122.4888889&xml=true&api_key=2chxq68efd4azrpygt5hh2qu";
        ArrayList<Campground> campgrounds = null;
        Campground currentCampground = null;
        try {
            Log.i("GetCampgroundNearLocation", url);

            InputStream responseStream = CommonUtils.HttpGetResponse(url);

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(responseStream, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        campgrounds = new ArrayList<Campground>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("result")) {
                            currentCampground = new Campground();
                            parser.require(XmlPullParser.START_TAG, ns, "result");
                            currentCampground.setId(parser.getAttributeValue(null, "facilityID"));
                            currentCampground.setName(parser.getAttributeValue(null, "facilityName"));
                            currentCampground.setContractType(parser.getAttributeValue(null, "contractType"));
                            currentCampground.setContractId(parser.getAttributeValue(null, "contractID"));
                            currentCampground.setPhotoUrl(parser.getAttributeValue(null, "faciltyPhoto"));
                            currentCampground.setAvailable(parser.getAttributeValue(null, "availabilityStatus") == "Y");
                            currentCampground.setState(parser.getAttributeValue(null, "state"));
                            currentCampground.setLocation(new LatLng(
                                    Double.parseDouble(parser.getAttributeValue(null, "latitude")),
                                    Double.parseDouble(parser.getAttributeValue(null, "longitude"))));

                            campgrounds.add(currentCampground);

//                            parser.nextTag();
//                            parser.require(XmlPullParser.END_TAG, ns, "result");
                        }
                        break;
                }

                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("GetCampgroundNearLocation", "Found " + campgrounds.size() + " campgrounds");

        return campgrounds;
    }

    public static Campground GetCampgroundDetails(Campground campground) {
        StringBuilder sb = new StringBuilder(API_BASE + TYPE_CAMPGROUND_DETAILS);
        sb.append("?api_key=" + CommonUtils.ActiveApiKey());
        sb.append("&contractCode=" + campground.getContractId());
        sb.append("&parkId=" + campground.getId());
        String url = sb.toString();
//        String url = "http://api.amp.active.com/camping/campgrounds?landmarkName=true&landmarkLat=37.84035&landmarkLong=-122.4888889&xml=true&api_key=2chxq68efd4azrpygt5hh2qu";
        try {
            Log.i("GetCampgroundDetails", url);

            InputStream responseStream = CommonUtils.HttpGetResponse(url);

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(responseStream, null);

            ArrayList<String> imageUrls = new ArrayList<String>();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name;
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("detailDescription")) {
                            parser.require(XmlPullParser.START_TAG, ns, "detailDescription");
                            campground.setDescription(parser.getAttributeValue(null, "description"));
                            campground.setDrivingDirection(parser.getAttributeValue(null, "drivingDirection"));
                            campground.setFullReserveUrl(parser.getAttributeValue(null, "fullReservationUrl"));
                            campground.setImportantInfo(parser.getAttributeValue(null, "importantInformation"));
                        }
                        else if (name.equalsIgnoreCase("address")) {
                            campground.setAddress(
                                    parser.getAttributeValue(null, "streetAddress")
                                    + "\n" + parser.getAttributeValue(null, "city") + ", " + parser.getAttributeValue(null, "state")
                                    + "\n" + parser.getAttributeValue(null, "country") + ", " + parser.getAttributeValue(null, "zip")
                            );
                        }
                        else if (name.equalsIgnoreCase("photo")) {
                            imageUrls.add(ParseImageUrl(parser.getAttributeValue(null, "url")));
                        }
                        break;
                }

                eventType = parser.next();
            }

            campground.setImgUrls_PhotoArray(imageUrls.toArray(new String[0]));
            campground.setImgUrls_Photo(TextUtils.join(",", imageUrls));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("GetCampgroundDetails", "Get details of " + campground);

        return campground;
    }

        private static String ParseImageUrl(String html) {
        InputStream responseStream = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));;
        XmlPullParser parser = Xml.newPullParser();

        String imageUrl = null;
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(responseStream, null);

            int eventType = parser.getEventType();
            while (imageUrl == null && eventType != XmlPullParser.END_DOCUMENT) {
                String name;
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("img")) {
                            parser.require(XmlPullParser.START_TAG, ns, "img");
                            imageUrl = parser.getAttributeValue(null, "pbsrc");
                        }
                        break;
                }

                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "http://www.reserveamerica.com/" + imageUrl;
    }
}
