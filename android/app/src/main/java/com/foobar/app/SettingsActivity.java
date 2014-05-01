package com.foobar.app;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

import java.sql.Time;
import java.util.ArrayList;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.provider.Settings.Secure;
import android.widget.TimePicker;
import android.widget.Toast;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import android.content.Context;

public class SettingsActivity extends Activity implements OnServerTaskComplete {
    private Spinner RouteMenu;
    private Spinner StopMenu;
    private Spinner SubMenu;
    private Button buttonSubscribe;
    private Button buttonDelete;
    private Button buttonAPM;
    private EditText hourInput;
    private EditText minuteInput;
    private TimePicker timePicker;

    private Route route;                // Where route is saved
    private BusStop stop;
    private ArrayList<Route> routeList;
    private ArrayList<BusStop> stopList;
    private ArrayList<StopSubscription> subList;
    private ArrayAdapter<Route> routeListAdapter;
    private ArrayAdapter<BusStop> stopListAdapter;
    private ArrayAdapter<StopSubscription> subListAdapter;
    private ArrayList<StopSubscription> stopSubs;
    private ServerCommunicator comm;

    private int hour, minute, routeID = 0;           // Where the hour and minute are saved and apm = am or pm

    public void deleteStopSubscriptionResponse(boolean success) { }
    public void addStopSubscriptionResponse(boolean success) { }
    public void getStopSubscriptionsResponse(ArrayList<StopSubscription> subs) {
        this.stopSubs = subs;
        this.subList.clear();
        Log.v("REST","Subs from server: " + subs.toString());
        for (StopSubscription sub : subs)   {
            this.subList.add(sub);
        }
        this.subListAdapter.notifyDataSetChanged();
    }

    public void getCurrentBusPositionResponse(BusPosition position){ }
    public void addStopResponse(boolean success) { }
    public void addCoordinateResponse(boolean success) {    }
    public void setCurrentBusPositionResponse(boolean success){ }
    public void createRouteResponse(long route_id){ }

    public void getRouteResponse(Route route){      // Populates sto plist to populate StopMenu
        this.route = route;
        Log.v("REST", "Getting route: " + route.getID());
        this.stopList.clear();
        for (BusStop stop : route.getStops())
            this.stopList.add(stop);
        this.stopListAdapter.notifyDataSetChanged();
    }

    public void getRouteListResponse(ArrayList<Route> routes) { // Populates routelist to populate RouteMenu
        this.routeList.clear();
        for (Route route: routes) {
            this.routeList.add(route);
        }
        this.routeListAdapter.notifyDataSetChanged();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //hide icon
        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        this.comm = new ServerCommunicator(this);
        this.timePicker = (TimePicker) findViewById(R.id.timepicker);
        RouteMenu = (Spinner) findViewById(R.id.spinner1);
        StopMenu = (Spinner) findViewById(R.id.spinner2);
        buttonSubscribe = (Button) findViewById(R.id.buttonSubscribe);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);

        addItemsOnRouteMenu();
        addItemsOnSubMenu();
        RouteMenu.setOnItemSelectedListener(new CustomOnItemSelectedListener(this.routeList,this));
        addItemsOnStopMenu();

        buttonSubscribe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Route route = routeList.get(routeList.indexOf(RouteMenu.getSelectedItem()));   // Sets the route to current selected route
                comm.getStopSubscriptions();
                BusStop stop = stopList.get(stopList.indexOf(StopMenu.getSelectedItem()));
                comm.addStopSub(stop.getID(),timePicker.getCurrentHour(),timePicker.getCurrentMinute());
                Toast.makeText(SettingsActivity.this,
                        "On Subscribe : " +
                                "\nRoute : "+ String.valueOf(RouteMenu.getSelectedItem()) +
                                "\nBus Stop : "+ String.valueOf(StopMenu.getSelectedItem()) +
                                "\nSubscribed to: RouteID: " + route.getID() + " Time: " + timePicker.getCurrentHour() + ":" +
                                timePicker.getCurrentMinute(),
                        Toast.LENGTH_LONG).show();
                comm.getStopSubscriptions();
            }
         });
        buttonDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                StopSubscription sub = subList.get(subList.indexOf(SubMenu.getSelectedItem()));
                comm.deleteStopSubscription(sub.getID());
                comm.getStopSubscriptions();
            }
        });
    }

    public void addStops(Route route){
        this.comm = new ServerCommunicator(this);
        comm.getRoute((int) route.getID());
    }


    public void addItemsOnRouteMenu() {
        this.comm = new ServerCommunicator(this);
        RouteMenu = (Spinner) findViewById(R.id.spinner1);
        this.routeList = new ArrayList<Route>();
        this.routeListAdapter = new ArrayAdapter<Route>(this,android.R.layout.simple_spinner_item, this.routeList);
        routeListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        RouteMenu.setAdapter(routeListAdapter);
        this.comm.getRouteList();
    }

    public void addItemsOnStopMenu()    {
        this.comm = new ServerCommunicator(this);
        StopMenu = (Spinner) findViewById(R.id.spinner2);
        this.stopList = new ArrayList<BusStop>();
        this.stopListAdapter = new ArrayAdapter<BusStop>(this,android.R.layout.simple_spinner_item, this.stopList);
        stopListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        StopMenu.setAdapter(stopListAdapter);
        if (RouteMenu.getSelectedItem() != null) {
            route = routeList.get(routeList.indexOf(RouteMenu.getSelectedItem()));
            this.comm.getRoute((int) route.getID());
        }
    }

    public void addItemsOnSubMenu() {
        this.comm = new ServerCommunicator(this);
        SubMenu = (Spinner) findViewById(R.id.spinner3);
        this.subList = new ArrayList<StopSubscription>();
        this.subListAdapter = new ArrayAdapter<StopSubscription>(this,android.R.layout.simple_spinner_item, this.subList);
        subListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SubMenu.setAdapter(subListAdapter);
        this.comm.getStopSubscriptions();
    }

}

class CustomOnItemSelectedListener implements OnItemSelectedListener {
    private ArrayList<Route> routeList;
    private SettingsActivity client;

    public CustomOnItemSelectedListener (ArrayList<Route> Routelist, SettingsActivity Client) {
        this.client = Client;
        routeList = Routelist;
    }
    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        Route route = this.routeList.get(routeList.indexOf(parent.getItemAtPosition(pos)));
        client.addStops(route);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}
