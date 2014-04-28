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
    private LocationManager locationManager;
    private EditText routeNameInput;
    private EditText stopNameInput;
    private TextView GPSStatusText;
    int routeCords = 0;
    int trackCords = 0;
    double currentLat;
    double currentLong;
    long id = 0;
    private ServerCommunicator comm = new ServerCommunicator(this);

    public void createRouteResponse(long route_id){
        id = route_id;
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

        this.routeNameInput = (EditText) findViewById(R.id.routeNameInput);
        this.stopNameInput = (EditText) findViewById(R.id.stopNameInput);
        this.GPSStatusText = (TextView) findViewById(R.id.GPSStatusText);

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
                markStopButton.setText("Mark Stop");
                makeStop();
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    public void makeRoute() {
        comm.createRoute(this.routeNameInput.getText().toString());
    }

    public void makeStop() {
        Log.v("REST", "makeStop");
        comm.addStop((int)id, currentLat, currentLong, stopNameInput.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLat = location.getLatitude();
        currentLong = location.getLongitude();

        if (routeCords == 1) {
            comm.addCoordiante((int) id, location.getLatitude(), location.getLongitude());
        } 
        this.GPSStatusText.setText(new Coordinate(currentLat, currentLong).toString());
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
    public void deleteStopSubscriptionResponse(boolean success) {}
    public void addStopSubscriptionResponse(boolean success) {}
    public void getStopSubscriptionsResponse(ArrayList<StopSubscription> subs) {}
}