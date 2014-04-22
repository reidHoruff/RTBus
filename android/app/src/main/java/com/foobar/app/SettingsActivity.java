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

public class SettingsActivity extends Activity implements OnServerTaskComplete {
    private Spinner spinner1;
    private Button buttonSubscribe;
    private EditText hourInput;
    private EditText minuteInput;
    private Route route;                // Where route is saved
    private ArrayList<Route> routeList;
    private ArrayAdapter<Route> routeListAdapter;
    private ServerCommunicator comm;
    private int hour, minute = 0;           // Where the hour and minute are saved


    public void getCurrentBusPositionResponse(BusPosition position){ }
    public void addStopResponse(boolean success) { }
    public void addCoordinateResponse(boolean success) {    }
    public void setCurrentBusPositionResponse(boolean success){ }
    public void createRouteResponse(long route_id){ }
    public void getRouteResponse(Route route){ }


    public void getRouteListResponse(ArrayList<Route> routes) {
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
        addItemsOnSpinner();
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        buttonSubscribe = (Button) findViewById(R.id.buttonSubscribe);
        buttonSubscribe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hour = Integer.parseInt(hourInput.getText().toString());                // Sets hour to currently typed in hour
                minute = Integer.parseInt(minuteInput.getText().toString());              // Sets minute to currently typed in minute
                route = routeList.get(routeList.indexOf(spinner1.getSelectedItem()));   // Sets the route to current selected route
            }

        });
    }

    public void addItemsOnSpinner() {
        this.comm = new ServerCommunicator(this);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        this.routeList = new ArrayList<Route>();
        this.routeListAdapter = new ArrayAdapter<Route>(this,android.R.layout.simple_spinner_item, this.routeList);
        routeListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(routeListAdapter);
        this.comm.getRouteList();
    }

}
