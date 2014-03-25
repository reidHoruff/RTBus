package com.foobar.app;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

public class MapActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        GoogleMap map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(33.585543, -101.865126), 15));

        // Polylines are useful for marking paths and routes on the map.
        map.addPolyline(new PolylineOptions().geodesic(true)
            .add(new LatLng(33.585543, -101.865126))
            .add(new LatLng(33.585449, -101.868361))
            .add(new LatLng(33.588345, -101.868565))
            .add(new LatLng(33.588193, -101.872331))
            .add(new LatLng(33.586977, -101.872706))
            .add(new LatLng(33.584305, -101.873200))
            .add(new LatLng(33.584448, -101.870625))
            .add(new LatLng(33.588228, -101.870560))
            .add(new LatLng(33.588219, -101.864098))
            .add(new LatLng(33.585458, -101.864120))
            .add(new LatLng(33.585543, -101.865126))
            .color(Color.BLUE)
            .width(3)
        );

        map.addMarker(new MarkerOptions().position(new LatLng(33.585543, -101.865126)));
        map.addMarker(new MarkerOptions().position(new LatLng(33.588219, -101.864098)));
        map.addMarker(new MarkerOptions().position(new LatLng(33.586950, -101.872735)));

        map.addPolyline(new PolylineOptions().geodesic(true)
                .add(new LatLng(33.583500, -101.868352))
                .add(new LatLng(33.581731, -101.868331))
                .add(new LatLng(33.581731, -101.868331))
                .add(new LatLng(33.583536, -101.864061))
                .add(new LatLng(33.583500, -101.868352))
                .color(Color.RED)
                .width(3)
        );

        map.addMarker(new MarkerOptions().position(new LatLng(33.583536, -101.864061)));
    }
}

