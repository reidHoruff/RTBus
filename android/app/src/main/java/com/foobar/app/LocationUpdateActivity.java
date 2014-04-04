package com.foobar.app;

import android.app.Activity;
import android.util.Log;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import java.util.ArrayList;

public class LocationUpdateActivity extends Activity implements LocationListener, OnServerTaskComplete {
    double currentLat;
    double currentLong;
    long id = 0;
    LocationManager locationManager;

    ServerCommunicator comm = new ServerCommunicator();

    public void createRouteResponse(long route_id){ }
    public void getRouteResponse(Route route){ }
    public void getRouteListResponse(ArrayList<Route> routes){ }
    public void addCoordinateResponse(boolean success){ }
    public void setCurrentBusPositionResponse(boolean success){ }
    public void getCurrentBusPositionResponse(BusPosition position){ }
    public void addStopResponse(boolean success) { }

    //    private String provider;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_realtime_tracking);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Criteria criteria = new Criteria();

        if (location != null) {
            onLocationChanged(location);
        }
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLat = location.getLatitude();
        currentLong = location.getLongitude();
        this.comm.setCurrentPosition(this, 50, currentLat, currentLong);
        Log.v("REST", Double.toString(currentLat) + ": " + Double.toString(currentLong));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}

