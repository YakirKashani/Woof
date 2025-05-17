package com.example.woof.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.woof.R;
import com.example.woof.Singleton.CurrentDogManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MapBottomSheet extends BottomSheetDialogFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView no_gps_text;
    private TextView timestamp_text;
    private LatLng location;
    private String timestamp;

    public MapBottomSheet(LatLng location, String timestamp) {
        this.location = location;
        this.timestamp = timestamp;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_bottom_sheet, container, false);
        no_gps_text = view.findViewById(R.id.no_gps_text);
        timestamp_text = view.findViewById(R.id.timestamp_text);

        // Find the map fragment inside the bottom sheet
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Example: Add a marker in Tel Aviv and move the camera
        if(CurrentDogManager.getInstance().getDog().getCollarGpsId() == null){
            no_gps_text.setVisibility(View.VISIBLE);
        } else {
            no_gps_text.setVisibility(View.GONE);
     //       LatLng telAviv = new LatLng(32.0853, 34.7818);
            mMap.addMarker(new MarkerOptions().position(location).title(CurrentDogManager.getInstance().getDog().getName()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
            timestamp_text.setText("Last updated: " + timestamp);
        }
    }

}

