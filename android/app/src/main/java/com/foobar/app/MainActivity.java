package com.foobar.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends Activity implements OnServerTaskComplete {

    ListView listView;

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    /**
     * 'Software Engineering' project number in Google API Console.
     */
    String SENDER_ID = "215267657068";

    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    String regid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide icon
        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        context = this.getApplicationContext();

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);

        // Defined Array values to show in ListView
        String[] values = new String[] {
                "Track a Bus",
                "Record a Route",
                "Tracking Mode",
                "Manage Subscriptions"
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

        Log.v("GCMfoo", "checking play services compatibility...");
        if (this.checkPlayServices()) {
            Log.v("GCMfoo", "This device is supported. [ok]");
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);
            boolean always = true;
            if (regid.isEmpty() || always) {
                this.registerInBackground();
            }
        } else {
            Log.v("GCMfoo", "This device is not supported.");
        }
    }


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, 9000).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("GCMfoo", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.v("GCMfoo", "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        Log.v("GCMfoo", "registering in background...");
        new RegisterAsyncTask(this).execute();
    }

    public void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i("GCMfoo", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
        Log.v("GCMfoo", "Registering device to our server...");
        new ServerCommunicator(this).subscribeGCM(regId);
    }

    //not used
    public void createRouteResponse(long route_id) {}
    public void getRouteResponse(Route route) {}
    public void getRouteListResponse(ArrayList<Route> routes) {}
    public void addCoordinateResponse(boolean success) {}
    public void setCurrentBusPositionResponse(boolean success) {}
    public void getCurrentBusPositionResponse(BusPosition position) {}
    public void addStopResponse(boolean success) {}
    public void deleteStopSubscriptionResponse(boolean success) {}
    public void addStopSubscriptionResponse(boolean success) {}
    public void getStopSubscriptionsResponse(ArrayList<StopSubscription> subs) {}
}


class RegisterAsyncTask extends AsyncTask<String, String, String> {
    MainActivity client;
    public RegisterAsyncTask(MainActivity client) {
        this.client = client;
    }

    @Override
    protected String doInBackground(String... params) {
        String msg = "";
        try {
            if (client.gcm == null) {
                client.gcm = GoogleCloudMessaging.getInstance(client.context);
            }
            client.regid = client.gcm.register(client.SENDER_ID);
            msg = "Device registered, registration ID=" + client.regid;

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            //sendRegistrationIdToBackend();

            // For this demo: we don't need to send it because the device
            // will send upstream messages to a server that echo back the
            // message using the 'from' address in the message.

            // Persist the regID - no need to register again.
            client.storeRegistrationId(client.context, client.regid);
        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        Log.v("GCMfoo", "ASYNC: " + msg);
    }
}
