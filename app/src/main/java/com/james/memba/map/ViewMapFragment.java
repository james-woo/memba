package com.james.memba.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.james.memba.R;

public class ViewMapFragment extends Fragment implements OnMapReadyCallback {

    private OnViewMapLoadedListener mViewMapLoadedListener;

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

        try {
            mGoogleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            // ignore error
        }

        mapLoaded();
    }

    private void mapLoaded() {
        if (mViewMapLoadedListener != null) {
            mViewMapLoadedListener.onViewMapLoaded();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnViewMapLoadedListener) {
            mViewMapLoadedListener = (OnViewMapLoadedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnViewMapLoadedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mViewMapLoadedListener = null;
    }

    public void updateLocation(Location location) {
        mLocation = location;

        // Zoom map to my location
        if (mGoogleMap.isMyLocationEnabled()) {
            System.out.println(mLocation);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 15);
            mGoogleMap.moveCamera(update);
        }
    }

    public interface OnViewMapLoadedListener {
        void onViewMapLoaded();
    }
}
