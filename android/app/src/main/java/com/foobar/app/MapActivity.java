package com.foobar.app;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MapActivity extends Activity implements OnServerTaskComplete {

    private GoogleMap map = null;
    private Route route = null;
    public ServerCommunicator communicator = null;
    private Handler mHandler = null;
    private Runnable busUpdater = null;
    private ListView routeListView;
    private ArrayList<Route> routeList;
    private ArrayAdapter<Route> routeListAdapter;

    public void createRouteResponse(long id){
        Log.v("REST", "here");
        Log.v("REST", Long.toString(id));
    }

    public void getRouteResponse(Route route) {
        this.route = route;
        Log.v("REST", "getRoute");

        if (route != null) {
            Log.v("REST", route.toString());
            route.drawRouteAndStops(this.map);
            route.centerMap(this.map);
        }
    }

    public void getCurrentBusPositionResponse(BusPosition busPosition) {
        if (this.route != null) {
            this.route.setBusPosition(busPosition);
            Log.v("REST", "current pos:");

            if (busPosition != null) {
                Log.v("REST", busPosition.toString());
            }
        }
    }

    public void addCoordinateResponse(boolean response){}

    public void getRouteListResponse(ArrayList<Route> routes) {
        this.routeList.clear();
        for (Route route: routes) {
            this.routeList.add(route);

        }
        this.routeListAdapter.notifyDataSetChanged();
    }

    public void setCurrentBusPositionResponse(boolean response){}

    public void addStopResponse(boolean success) {
        Log.v("REST", "stop has been added");
        Log.v("REST", Boolean.toString(success));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        this.routeList = new ArrayList<Route>();
        this.routeListView = (ListView) this.findViewById(R.id.left_drawer);
        //this.routeListAdapter = new ArrayAdapter<Route>(this, R.layout.route_list_item, R.id.textView, this.routeList);
        this.routeListAdapter = new ArrayAdapter<Route>(this, android.R.layout.simple_list_item_1, this.routeList);
        this.routeListView.setAdapter(this.routeListAdapter);

        communicator = new ServerCommunicator(this);
        communicator.getRouteList();

        this.routeListView.setOnItemClickListener(new RouteListClickListener(this.communicator, this.routeList));

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.585543, -101.865126), 15));
        new BusRunnable(this.communicator, 57).run();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.route != null && this.map != null) {
            this.route.drawRouteAndStops(this.map);
        }
    }
}

class RouteListClickListener implements AdapterView.OnItemClickListener {
    private ServerCommunicator communicator;
    private ArrayList<Route> routeList;

    public RouteListClickListener(ServerCommunicator comm, ArrayList<Route> routeList) {
        this.communicator = comm;
        this.routeList = routeList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Route route = this.routeList.get(position);
        this.communicator.getRoute((int)route.id);
    }
}

class BusRunnable implements Runnable {
    private Handler mHandler;
    private ServerCommunicator communicator;
    private int route_id;

    public BusRunnable(ServerCommunicator communicator, int route_id) {
        this.mHandler = new Handler();
        this.communicator = communicator;
        this.route_id = route_id;
    }

    public void run() {
        Log.v("REST", "updating bus");
        this.communicator.getCurrentPosition(this.route_id);
        mHandler.postDelayed(this, 3000);
    }
}
