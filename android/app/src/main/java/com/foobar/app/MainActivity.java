package com.foobar.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

    ListView listView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide icon
        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);

        // Defined Array values to show in ListView
        String[] values = new String[] {
                "Track a Bus",
                "Record a Route",
                "Tracking Mode",
                "Settings"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent myIntent = new Intent(MainActivity.this, MapActivity.class);
                    MainActivity.this.startActivity(myIntent);
                } else if (position == 1) {
                    Intent myIntent = new Intent(MainActivity.this, RecordRouteActivity.class);
                    MainActivity.this.startActivity(myIntent);
                } else if (position == 2) {
                    Intent myIntent = new Intent(MainActivity.this, LocationUpdateActivity.class);
                    MainActivity.this.startActivity(myIntent);
                } else if (position == 3) {
                    Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    MainActivity.this.startActivity(myIntent);
                }
            }

        });
    }
}


