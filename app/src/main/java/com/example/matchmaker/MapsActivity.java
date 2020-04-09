package com.example.matchmaker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements
        OnMarkerClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LatLng [] latLngs_array;
    private String [] idArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        try {
            getAllMatches(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getAllMatches(Context context) throws JSONException {
        SharedPreferences sharedIdList = context.getSharedPreferences(getString(R.string.id_list), Context.MODE_PRIVATE);
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.match_shared_data),Context.MODE_PRIVATE);

        Set<String> id_matches = sharedIdList.getStringSet(getIntent().getStringExtra("sport"), Collections.<String>emptySet());

        idArrayList = id_matches.toArray(new String [id_matches.size()]);

        latLngs_array = new LatLng[idArrayList.length];

        for(int i = 0; i < idArrayList.length; i++) {
            String json = sharedPref.getString(idArrayList[i], "");
            if (!json.equals("")) {
                JSONArray jsonArray = new JSONArray(json);
                List<String> list = new ArrayList<String>();
                for (int x = 0; x < jsonArray.length(); x++) {
                    list.add(jsonArray.getString(x));
                }

                String [] position = list.get(7).split(",");
                LatLng latLng = new LatLng(Double.parseDouble(position[0]), Double.parseDouble(position[1]));

                latLngs_array[i] = latLng;

            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
        putMarkers(mMap);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    private void putMarkers(GoogleMap mMap) {
        MarkerOptions markerOptions = new MarkerOptions();

        for(int i= 0; i< latLngs_array.length; i++){
            markerOptions.position(latLngs_array[i]);
            markerOptions.title(idArrayList[i]);
            mMap.addMarker(markerOptions);
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(MapsActivity.this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        LatLng position = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(position));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String id_match_marker = marker.getTitle();
        Intent intent = new Intent(MapsActivity.this, MatchInfoActivity.class);
        intent.putExtra("sport", getIntent().getStringExtra("sport"));
        intent.putExtra("id_match",id_match_marker);
        startActivity(intent);
        return false;
    }
}
