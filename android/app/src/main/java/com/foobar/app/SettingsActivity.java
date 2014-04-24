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
import java.util.ArrayList;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.provider.Settings.Secure;
import android.widget.Toast;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import android.content.Context;

public class SettingsActivity extends Activity implements OnServerTaskComplete {
    private Spinner RouteMenu;
    private Spinner StopMenu;
    private Button buttonSubscribe;
    private Button buttonAPM;
    private EditText hourInput;
    private EditText minuteInput;

    private Route route;                // Where route is saved
    private ArrayList<Route> routeList;
    private ArrayList<BusStop> stopList;
    private ArrayAdapter<Route> routeListAdapter;
    private ArrayAdapter<BusStop> stopListAdapter;
    private ServerCommunicator comm;

    private int hour, minute, apm = 0, routeID = 0;           // Where the hour and minute are saved and apm = am or pm
    private int hour2, minute2, routeID2 = 0;           // Where the hour and minute are saved in getStopSubsResponse
    private String device;

    private String android_id = Secure.ANDROID_ID;

    public void getStopSubsResponse(String device, int h, int m, long id) {     // gets the stop subs
        this.hour2 = h;
        this.minute2 = m;
        this.routeID2 = (int) id;
        this.device = device;
    }

    public void getCurrentBusPositionResponse(BusPosition position){ }
    public void addStopResponse(boolean success) { }
    public void addCoordinateResponse(boolean success) {    }
    public void setCurrentBusPositionResponse(boolean success){ }
    public void createRouteResponse(long route_id){ }

    public void getRouteResponse(Route route){      // Populates stoplist to populate StopMenu
        this.route = route;
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
        this.hourInput = (EditText) findViewById(R.id.hourInput);
        this.minuteInput = (EditText) findViewById(R.id.minuteInput);
        RouteMenu = (Spinner) findViewById(R.id.spinner1);
        StopMenu = (Spinner) findViewById(R.id.spinner2);
        buttonSubscribe = (Button) findViewById(R.id.buttonSubscribe);
        buttonAPM = (Button) findViewById(R.id.buttonAPM);

        addItemsOnRouteMenu();
        RouteMenu.setOnItemSelectedListener(new CustomOnItemSelectedListener(this.routeList,this));
        addItemsOnStopMenu();

        buttonAPM.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(apm == 0)   {           // Toggle AM and PM for button
                     apm = 1;
                     buttonAPM.setText("PM");
                 } else {
                     apm = 0;
                     buttonAPM.setText("AM");
                 }
            }
        });

        buttonSubscribe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hourInput.getText().toString() == "")
                    hour = 0;
                else
                    hour = Integer.parseInt(hourInput.getText().toString());                // Sets hour to currently typed in hour
                if (minuteInput.getText().toString() == "")
                    minute = 0;
                else
                    minute = Integer.parseInt(minuteInput.getText().toString());              // Sets minute to currently typed in minute
                Route route = routeList.get(routeList.indexOf(RouteMenu.getSelectedItem()));   // Sets the route to current selected route
                comm.addStopSub((int) route.getID(),"1" ,hour, minute);

                Toast.makeText(SettingsActivity.this,
                        "On Subscribe : " +
                                "\nRoute : "+ String.valueOf(RouteMenu.getSelectedItem()) +
                                "\nBus Stop : "+ String.valueOf(StopMenu.getSelectedItem()) +
                                "\nSubscribed to: RouteID: " + route.getID() + " Hour: " + hour +
                                " Minute: " + minute  + "\n Android ID: " + android_id,
                        Toast.LENGTH_LONG).show();
                        Log.v("REST","from phone: " + android_id + " " + routeID + " " + hour + " " + minute);

                /*
                // For on device file saving
                hour = Integer.parseInt(hourInput.getText().toString());                // Sets hour to currently typed in hour
                minute = Integer.parseInt(minuteInput.getText().toString());              // Sets minute to currently typed in minute
                route = routeList.get(routeList.indexOf(spinner1.getSelectedItem()));   // Sets the route to current selected route
                FileOutputStream outputStream;
                String string = route.toString() + " " + hour + " " + minute;
                try {
                    outputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    outputStream.write(string.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
         });
    }

    public void addStops(Route route){
        this.comm = new ServerCommunicator(this);
        Log.v("REST", "Getting route: " + route.getID());
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
        Log.v("REST", route.toString() + " " + pos);
        client.addStops(route);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}