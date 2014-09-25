package com.campingfun.vacancyhunter.campsitehunter.maps;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.MapFragment;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.campingfun.vacancyhunter.campsitehunter.Active.Async_GetCampgroundNearLocation;
import com.campingfun.vacancyhunter.campsitehunter.CampsiteDetails;
import com.campingfun.vacancyhunter.campsitehunter.Entity.Campground;
import com.campingfun.vacancyhunter.campsitehunter.Entity.ShowCampgroundsEvent;
import com.campingfun.vacancyhunter.campsitehunter.Event_ShowMyCurrentSearchLocation;
import com.campingfun.vacancyhunter.campsitehunter.R;
import com.campingfun.vacancyhunter.campsitehunter.Utils.ToastHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BaseMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BaseMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class BaseMapFragment extends Fragment {
    private static final String TAG = BaseMapFragment.class.getSimpleName();
    ProgressDialog progressDialog = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final double[] CLUSTER_SIZES = new double[]{180, 160, 144, 120, 90};
    private MutableData[] dataArray = {new MutableData(6, new LatLng(-50, 0)), new MutableData(28, new LatLng(-52, 1)),
            new MutableData(496, new LatLng(-51, -2)),};

    private MapFragment mapFragment;
    protected GoogleMap map;
    private HashMap<LatLng, Campground> visibleCampgroundsMap = new HashMap<LatLng, Campground>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FrameLayout frameLayout;
    private LatLng lastKnownLocation;

    private boolean initialPositionAnimated = false;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BaseMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BaseMapFragment newInstance(String param1, String param2) {
        BaseMapFragment fragment = new BaseMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public BaseMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void onEventMainThread(ShowCampgroundsEvent event) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (event.getCampgrounds() == null || event.getCampgrounds().size() == 0) {
            ToastHelper.showToast(getActivity(), getString(R.string.NoCampgroundsFound));
        } else {
            ToastHelper.showToast(getActivity(), event.getCampgrounds().size() + " campgrounds found near " + event.getPlaceSearched_Name());
            ShowCampgroundsAndAnimateCamera(event);
        }
    }

    public void onEventMainThread(Event_ShowMyCurrentSearchLocation event) {
        MarkerOptions options = new MarkerOptions();
        map.addMarker(options.position(event.getCoordinate()).title(event.getName()));
    }

    private void ShowCampgroundsAndAnimateCamera(ShowCampgroundsEvent event)
    {
        List<Campground> campgrounds = event.getCampgrounds();
        map.clear();
        MarkerOptions options = new MarkerOptions();
        LatLngBounds.Builder builder = LatLngBounds.builder();
        visibleCampgroundsMap.clear();

        for (Campground c : campgrounds) {
            visibleCampgroundsMap.put(c.getLocation(), c);
            map.addMarker(options.position(c.getLocation()).title(c.getName()).snippet("Click for details"));
            builder.include(c.getLocation());
        }

        Marker targetLocation = map.addMarker(options.icon(
                BitmapDescriptorFactory.fromResource(R.drawable.mapmarker)).position(event.getPlaceSearched_Coordinate()).snippet("").title(event.getPlaceSearched_Name()));
        targetLocation.showInfoWindow();
        LatLngBounds bounds = builder.build();
//        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, getResources().getDimensionPixelSize(R.dimen.padding)));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(event.getPlaceSearched_Coordinate(), 7));
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = mapFragment.getExtendedMap();
            if (map != null) {
                setUpMap();
            }
        }
    }

    protected void setUpMap() {

        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        map.setMyLocationEnabled(true);
        ViewGroup v1 = (ViewGroup) getView();

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.isCluster()) {
                    float zoomLevel = Float.MIN_VALUE;
                    float currentZoomLevel;
                    List<Marker> markers = marker.getMarkers();
                    LatLngBounds.Builder builder = LatLngBounds.builder();
                    for (Marker m : markers) {
                        builder.include(m.getPosition());
//                        currentZoomLevel = map.getMinZoomLevelNotClustered(m);
//                        zoomLevel = Math.max(zoomLevel, currentZoomLevel != Float.POSITIVE_INFINITY ? currentZoomLevel : zoomLevel);
                    }

                    LatLngBounds bounds = builder.build();

//                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), zoomLevel));
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, getResources().getDimensionPixelSize(R.dimen.padding)));
                    return true;
                }
                return false;
            }
        });
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(!marker.isCluster()) {
                    Campground campground = visibleCampgroundsMap.get(marker.getPosition());
                    if(campground != null) {
                        Intent intent = new Intent(getActivity(), CampsiteDetails.class);
                        intent.putExtra(CampsiteDetails.PARAM_CAMPGROUND, campground);
                        startActivity(intent);
                    }
                }

                if (marker.isCluster()) {
                    float zoomLevel = map.getMinZoomLevelNotClustered(marker);
//                    List<Marker> markers = marker.getMarkers();
//                    LatLngBounds.Builder builder = LatLngBounds.builder();
//                    for (Marker m : markers) {
//                        builder.include(m.getPosition());
//                    }
//                    LatLngBounds bounds = builder.build();

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), zoomLevel));
//                    newLatLngBounds(bounds, getResources().getDimensionPixelSize(R.dimen.padding)));
                }
            }
        });

        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
//                map.animateCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocation, 15));
                progressDialog.show();
                new Async_GetCampgroundNearLocation(getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, lastKnownLocation);
                return true;
            }
        });

        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                lastKnownLocation = new LatLng(location.getLatitude(), location.getLongitude());
                if(!initialPositionAnimated)
                {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocation, 10));
                    initialPositionAnimated = true;
                }
            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                progressDialog.show();
                new Async_GetCampgroundNearLocation(getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, latLng);
            }
        });

        updateClustering();
        //MarkerGenerator.addMarkersInWorld(map);

//        MarkerOptions options = new MarkerOptions();
//        LatLng point =new LatLng(46.985832214400, -121.836181641000);
//        map.addMarker(options.position(point).title("National Park Service").snippet("WA"));
//
//        LatLng point2 = new LatLng(46.9858322100, -121.83628000);
//        map.addMarker(options.position(point2).title("National Park Service Double").snippet("WA"));
//        map.setMyLocationEnabled(true);
//        Location loc = map.getMyLocation();
//        LatLng myPoint = new LatLng(loc.getLatitude(), loc.getLongitude());
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(myPoint, 15));

//        BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
//        for (MutableData data : dataArray) {
//            map.addMarker(new MarkerOptions().position(data.position).icon(icon).data(data));
//        }
    }

    private void updateClustering() {
        if (map == null) {
            Log.e(TAG, "No map found");
            return;
        }

        ClusteringSettings clusteringSettings = new ClusteringSettings();
        clusteringSettings.addMarkersDynamically(true);
        clusteringSettings.clusterOptionsProvider(new BaseClusterOptionsProvider(getResources()));

        double clusterSize = CLUSTER_SIZES[4];
        clusteringSettings.clusterSize(800);
        clusteringSettings.enabled(false);
        map.setClustering(clusteringSettings);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_map, container, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        createMapFragmentIfNeeded(view);
        // Inflate the layout for this fragment
        return view;
    }

    private void createMapFragmentIfNeeded(View view) {
        this.frameLayout = (FrameLayout) view.findViewById(R.id.map_container);

        if (this.mapFragment == null) {
            this.mapFragment = createMapFragment();
            FragmentTransaction tx = getFragmentManager().beginTransaction();
            tx.add(R.id.map_container, this.mapFragment);
            tx.commit();
        }
    }

    protected MapFragment createMapFragment(){
        return  MapFragment.newInstance();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
//    }
    private static class MutableData {

        private int value;

        private LatLng position;

        public MutableData(int value, LatLng position) {
            this.value = value;
            this.position = position;
        }
    }
}
