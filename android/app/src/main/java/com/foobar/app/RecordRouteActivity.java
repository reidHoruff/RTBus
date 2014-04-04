package com.foobar.app;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.view.View;

import java.util.ArrayList;

public class RecordRouteActivity extends Activity implements LocationListener, OnServerTaskComplete {
    private TextView routeIdField;
    private TextView latituteField;
    private TextView longitudeField;
    private TextView latArrayField;
    private TextView longArrayField;
    private LocationManager locationManager;

    private StringBuilder builder1 = new StringBuilder();
    private StringBuilder builder2 = new StringBuilder();
    int routeCords = 0;
    int trackCords = 0;
    double currentLat;
    double currentLong;
    long id = 0;
    private ServerCommunicator comm = new ServerCommunicator();

    public void createRouteResponse(long route_id){
        id = route_id;
        routeIdField.setText("Route ID: " + id);
    }

    public void addCoordinateResponse(boolean success) {
        if (!success) {
            routeCords = 0;
        }
    }

    public void setCurrentBusPositionResponse(boolean success){
        if (!success) {
            trackCords = 0;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        routeIdField = (TextView) findViewById(R.id.TextView00);
        latituteField = (TextView) findViewById(R.id.TextView02);
        longitudeField = (TextView) findViewById(R.id.TextView04);
        latArrayField = (TextView) findViewById(R.id.TextView06);
        longArrayField = (TextView) findViewById(R.id.TextView07);

        final Button button1 = (Button) findViewById(R.id.Button01);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (routeCords == 0) {
                    routeCords = 1;
                    button1.setText("Stop Sending");
                    makeRoute();
                } else {
                    routeCords = 0;
                    button1.setText("Create Route");
                }
            }
        });

        final Button button2 = (Button) findViewById(R.id.Button02);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                button2.setText("Create Stop");
                makeStop();
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Criteria criteria = new Criteria();

        if (location != null) {
            System.out.println("Provider " + LocationManager.GPS_PROVIDER + " has been selected.");
            onLocationChanged(location);
        } else {
            latituteField.setText("Location not available");
            longitudeField.setText("Location not available");
        }
    }

    public void makeRoute() {
        comm.createRoute(this, "Default Route");
        builder1.delete(0,builder1.length());
        builder2.delete(0,builder2.length());
    }

    public void makeStop() {
        Log.v("REST", "makeStop");
        comm.addStop(this, (int)id, currentLat, currentLong, "Default Stop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
        latituteField.setText("Location not available");
        longitudeField.setText("Location not available");
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLat = location.getLatitude();
        currentLong = location.getLongitude();
        String lat = Double.toString(currentLat);
        String lng = Double.toString (currentLong);

        if (routeCords == 1) {
            comm.addCoordiante(this,(int)(long) id,location.getLatitude(),location.getLongitude());
            builder1.append(String.valueOf(lat) + "\n");
            builder2.append(String.valueOf(lng) + "\n");
            latituteField.setText(String.valueOf(lat));
            longitudeField.setText(String.valueOf(lng));
            latArrayField.setText(builder1.toString());
            longArrayField.setText(builder2.toString());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }

    public void getRouteResponse(Route route){ }
    public void getRouteListResponse(ArrayList<Route> routes){ }
    public void getCurrentBusPositionResponse(BusPosition position){ }
    public void addStopResponse(boolean success) { }
}