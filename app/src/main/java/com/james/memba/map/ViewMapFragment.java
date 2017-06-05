package com.james.memba.map;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.james.memba.R;
import com.james.memba.model.BerryLocation;

import java.util.ArrayList;

public class ViewMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMyLocationButtonClickListener{

    private ViewMapListener mViewMapListener;

    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private Location mLocation;

    public ViewMapFragment() {
        // Required empty public constructor
    }

    public static ViewMapFragment newInstance() {
        ViewMapFragment fragment = new ViewMapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.view_map_fragment, container, false);

        try {
            MapsInitializer.initialize(getActivity());
            mMapView = (MapView) root.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        // Allow set my location, a button on the top right to bring map to current location
        try {
            mGoogleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            // ignore error
        }

        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnMyLocationButtonClickListener(this);

        mapLoaded();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Berry id was stored as a title
        markerClicked(marker.getTitle());
        return true;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        myLocationClicked();
        return true;
    }

    public void showBerries(ArrayList<BerryLocation> locations) {
        for (BerryLocation l : locations) {
            LatLng pos = new LatLng(l.getLocation().lat, l.getLocation().lng);
            MarkerOptions m = new MarkerOptions();
            m.icon(BitmapDescriptorFactory.fromResource(R.drawable.point));
            // Store the id of the berry as a title so that it can be referenced
            m.title(l.getId());
            m.position(pos);
            mGoogleMap.addMarker(m);
        }
    }

    // Main activity calls this when the users location is updated, moves view to the location
    public void updateLocation(Location location) {
        mLocation = location;

        // Zoom map to my location
        if (mGoogleMap.isMyLocationEnabled()) {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 15);
            mGoogleMap.moveCamera(update);
        }
    }

    // Callback for when the fragment is finished loading
    private void mapLoaded() {
        if (mViewMapListener != null) {
            mViewMapListener.onViewMapLoaded();
        }
    }

    // Callback when a marker is clicked, the berryId is the marker title
    private void markerClicked(String berryId) {
        if (mViewMapListener != null) {
            mViewMapListener.onMarkerClicked(berryId);
        }
    }

    // Callback when the "My location" button is pressed
    private void myLocationClicked() {
        if (mViewMapListener != null) {
            mViewMapListener.onMyLocationClicked();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ViewMapListener) {
            mViewMapListener = (ViewMapListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ViewMapListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mViewMapListener = null;
    }

    public interface ViewMapListener {
        void onViewMapLoaded();
        void onMarkerClicked(String berryId);
        void onMyLocationClicked();
    }
}
