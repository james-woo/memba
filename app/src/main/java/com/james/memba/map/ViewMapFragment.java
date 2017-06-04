package com.james.memba.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        GoogleMap.OnMarkerClickListener {

    private OnViewMapLoadedListener mViewMapLoadedListener;
    private OnMarkerClickedListener mMarkerClickedListener;

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

        mGoogleMap.setOnMarkerClickListener(this);

        mapLoaded();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        markerClicked(marker.getTitle());
        return true;
    }

    public void showBerries(ArrayList<BerryLocation> locations) {
        for (BerryLocation l : locations) {
            LatLng pos = new LatLng(l.getLocation().lat, l.getLocation().lng);
            MarkerOptions m = new MarkerOptions();
            m.icon(BitmapDescriptorFactory.fromResource(R.drawable.point));
            m.title(l.getId());
            m.position(pos);
            mGoogleMap.addMarker(m);
        }
    }

    public void updateLocation(Location location) {
        mLocation = location;

        // Zoom map to my location
        if (mGoogleMap.isMyLocationEnabled()) {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 15);
            mGoogleMap.moveCamera(update);
        }
    }

    private void mapLoaded() {
        if (mViewMapLoadedListener != null) {
            mViewMapLoadedListener.onViewMapLoaded();
        }
    }

    private void markerClicked(String berryId) {
        if (mMarkerClickedListener != null) {
            mMarkerClickedListener.onMarkerClicked(berryId);
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

        if (context instanceof OnMarkerClickedListener) {
            mMarkerClickedListener = (OnMarkerClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMarkerClickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mViewMapLoadedListener = null;
        mMarkerClickedListener = null;
    }

    public interface OnViewMapLoadedListener {
        void onViewMapLoaded();
    }

    public interface OnMarkerClickedListener {
        void onMarkerClicked(String berryId);
    }
}
