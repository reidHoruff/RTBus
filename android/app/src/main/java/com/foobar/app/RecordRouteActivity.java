package com.foobar.app;

import android.graphics.drawable.ColorDrawable;
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
import android.view.WindowManager;
import android.widget.EditText;
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
    private EditText routeNameInput;

    private StringBuilder builder1 = new StringBuilder();
    private StringBuilder builder2 = new StringBuilder();
    int routeCords = 0;
    int trackCords = 0;
    double currentLat;
    double currentLong;
    long id = 0;
    private ServerCommunicator comm = new ServerCommunicator(this);

    public void createRouteResponse(long route_id){
        id = route_id;
        routeIdField.setText("Route ID: " + id);
    }

    public void addCoordinateResponse(boolean success) {
        if (!success) {
            this.routeCords = 0;
        }
    }

    public void setCurrentBusPositionResponse(boolean success){
        if (!success) {
            this.trackCords = 0;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        //keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //hide icon
        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        this.routeIdField = (TextView) findViewById(R.id.TextView00);
        this.latituteField = (TextView) findViewById(R.id.TextView02);
        this.longitudeField = (TextView) findViewById(R.id.TextView04);
        this.latArrayField = (TextView) findViewById(R.id.TextView06);
        this.longArrayField = (TextView) findViewById(R.id.TextView07);
        this.routeNameInput = (EditText) findViewById(R.id.editText);

        final Button recordButton = (Button) findViewById(R.id.Button01);

        recordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (routeCords == 0) {
                    routeCords = 1;
                    recordButton.setText("Stop Sending");
                    makeRoute();
                } else {
                    routeCords = 0;
                    recordButton.setText("Create Route");
                }
            }
        });

        final Button markStopButton = (Button) findViewById(R.id.Button02);
        markStopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                markStopButton.setText("Create Stop");
                makeStop();
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    public void makeRoute() {
        comm.createRoute("Default Route");
        builder1.delete(0,builder1.length());
        builder2.delete(0,builder2.length());
    }

    public void makeStop() {
        Log.v("REST", "makeStop");
        comm.addStop((int)id, currentLat, currentLong, this.routeNameInput.getText().toString());
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
            comm.addCoordiante((int)(long) id, location.getLatitude(), location.getLongitude());
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