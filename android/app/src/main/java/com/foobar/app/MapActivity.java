package com.foobar.app;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import org.json.simple.JSONObject;

import java.util.ArrayList;

public class MapActivity extends Activity implements OnServerTaskComplete {

    private GoogleMap map = null;
    private Route route = null;

    public void createRouteResponse(long id){
        Log.v("REST", "here");
        Log.v("REST", Long.toString(id));
    }

    public void getRouteResponse(Route route) {
        Log.v("REST", "getRoute");
        Log.v("REST", route.toString());
        this.route = route;
        route.draw(this.map);
    }

    public void getRouteListResponse(ArrayList<Route> routes){
        for (Route r : routes) {
            Log.v("REST", r.toString());
        }
    }

    public void getCurrentBusPositionResponse(BusPosition busPosition) {
        if (this.route != null) {
            this.route.setBusPosition(busPosition);
        }
    }

    public void addCoordinateResponse(boolean response){}
    public void setCurrentBusPositionResponse(boolean response){}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ServerCommunicator comm = new ServerCommunicator();
        //comm.createRoute(this, "this is a route.");
        //comm.getRouteList(this);
        Log.v("REST", "this is happending now");
        comm.getRoute(this, 1);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.585543, -101.865126), 15));
    }
}

