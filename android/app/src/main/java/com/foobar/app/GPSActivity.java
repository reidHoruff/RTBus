package com.foobar.app;

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

public class GPSActivity extends Activity implements LocationListener {

    private TextView latituteField;
    private TextView longitudeField;
    private TextView latArrayField;
    private TextView longArrayField;
    private LocationManager locationManager;
    StringBuilder builder1 = new StringBuilder();
    StringBuilder builder2 = new StringBuilder();
    int index = 0;

    //    private String provider;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        latituteField = (TextView) findViewById(R.id.TextView02);
        longitudeField = (TextView) findViewById(R.id.TextView04);
        latArrayField = (TextView) findViewById(R.id.TextView06);
        longArrayField = (TextView) findViewById(R.id.TextView07);
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
//        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + LocationManager.GPS_PROVIDER + " has been selected.");
            onLocationChanged(location);
        } else {
            latituteField.setText("Location not available");
            longitudeField.setText("Location not available");
        }
    }
    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
        latituteField.setText("Location not available");
        longitudeField.setText("Location not available");
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        String lat = Double.toString(location.getLatitude());
        String lng = Double.toString (location.getLongitude());
        builder1.append(String.valueOf(lat) + "\n");
        builder2.append(String.valueOf(lng) + "\n");
        latituteField.setText(String.valueOf(lat));
        longitudeField.setText(String.valueOf(lng));
        latArrayField.setText(builder1.toString());
        longArrayField.setText(builder2.toString());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

}