package com.foobar.app;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class LocationUpdateActivity extends Activity implements LocationListener, OnServerTaskComplete {
    private ListView routeListView;
    private ArrayList<Route> routeList;
    private ArrayAdapter<Route> routeListAdapter;
    private Route route;

    double currentLat;
    double currentLong;
    private long id = 0;

    private LocationManager locationManager;
    private ServerCommunicator comm;

    public void getStopSubsResponse(String device, int h, int m, long id) { }

    public void createRouteResponse(long route_id){ }
    public void getRouteResponse(Route route){ }

    public void setRoute(Route route) {
        this.route = route;

        if (route != null) {
            TextView message = (TextView)this.findViewById(R.id.messageTextView);
            message.setText("Now Tracking " + route.getName());
        }
    }

    public void getRouteListResponse(ArrayList<Route> routes) {
        this.routeList.clear();
        for (Route route: routes) {
            this.routeList.add(route);
        }
        this.routeListAdapter.notifyDataSetChanged();
    }

    public void addCoordinateResponse(boolean success){ }
    public void setCurrentBusPositionResponse(boolean success){ }
    public void getCurrentBusPositionResponse(BusPosition position){ }
    public void addStopResponse(boolean success) { }

    //    private String provider;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_tracking);

        //keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //hide icon
        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        this.comm = new ServerCommunicator(this);

        this.routeList = new ArrayList<Route>();
        this.routeListView = (ListView) this.findViewById(R.id.left_drawer);
        this.routeListAdapter = new ArrayAdapter<Route>(this, android.R.layout.simple_list_item_1, this.routeList);
        this.routeListView.setAdapter(this.routeListAdapter);
        this.routeListView.setOnItemClickListener(new LocationUpdateRouteListClickListener(this, this.routeList));
        this.comm.getRouteList();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    /* Request updates at startup */
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

        TextView coordsDisplay = (TextView)this.findViewById(R.id.coordTextView);
        coordsDisplay.setText("(" + currentLat + ", " + currentLong + ")");

        if (this.route != null) {
            this.comm.setCurrentPosition((int)this.route.getID(), currentLat, currentLong);
            Log.v("REST", Double.toString(currentLat) + ": " + Double.toString(currentLong));
        }
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

class LocationUpdateRouteListClickListener implements AdapterView.OnItemClickListener {
    private ArrayList<Route> routeList;
    private LocationUpdateActivity client;

    public LocationUpdateRouteListClickListener(LocationUpdateActivity client, ArrayList<Route> routeList) {
        this.client = client;
        this.routeList = routeList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.client.setRoute(this.routeList.get(position));
    }
}
