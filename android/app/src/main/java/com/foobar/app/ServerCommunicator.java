package com.foobar.app;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.provider.Settings.Secure;

/**
 * Created by reidhoruff on 4/2/14.
 */


/*
HOW TO USE
make your activity implement 'OnServerTaskComplete'
You must then make your activity implement all of the methods in OnServerTaskComplete.java

(there is 6 of them I think)

Create a ServerCommunicator object and call one of the
listed methods below depending on what server operation you
want to peform.

Once the server request is completed the appropriate corresponding method (from OnServerTaskComplete.java)
will be called.

You will then have to parse that string 'it will be json'
see: https://github.com/reidHoruff/RTBus/tree/master/server

Give me some more time an you wont have to parse the string, the method will insted
provide you with a object containing all of the relevent data.


(Also I haven't tested all of these so there might be some typoes in the query strings)
 */

public class ServerCommunicator {
    HttpClient httpclient = null;
    private OnServerTaskComplete client;
    final String ADDRESS = "reidhoruff.webfactional.com";
    private String devID = null;

    public ServerCommunicator(Activity client) {
        this.client = (OnServerTaskComplete) client;
        this.httpclient = new DefaultHttpClient();
        this.devID = "sfsdfs";
    }

    public void createRoute(String name) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("create_route").appendQueryParameter("name", name);
        new CreateRouteRequestTask(this.client).execute(builder.build().toString());
    }

    public void getRoute(int id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("get_route")
            .appendQueryParameter("id", Integer.toString(id));
        new GetRouteRequestTask(this.client).execute(builder.build().toString());
    }

    public void getRouteList() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("get_route_list");
        new GetRouteListRequestTask(this.client).execute(builder.build().toString());
    }

    public void addCoordiante(int id, double lat, double lng) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("add_coordinate")
            .appendQueryParameter("id", Integer.toString(id))
            .appendQueryParameter("lat", Double.toString(lat))
            .appendQueryParameter("lng", Double.toString(lng));
        new AddCoordinateRequestTask(this.client).execute(builder.build().toString());
    }

    public void addStop(int id, double lat, double lng, String name) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("add_stop")
            .appendQueryParameter("id", Integer.toString(id))
            .appendQueryParameter("lat", Double.toString(lat))
            .appendQueryParameter("lng", Double.toString(lng))
            .appendQueryParameter("name", name);
        new AddStopRequestTask(this.client).execute(builder.build().toString());
    }

    public void setCurrentPosition(int id, double lat, double lng) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("set_cur_pos")
            .appendQueryParameter("id", Integer.toString(id))
            .appendQueryParameter("lat", Double.toString(lat))
            .appendQueryParameter("lng", Double.toString(lng));
        new SetCurrentBusPositionRequestTask(this.client).execute(builder.build().toString());
    }

    public void getCurrentPosition(int id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("get_cur_pos")
                .appendQueryParameter("id", Integer.toString(id));
        new GetCurrentBusPositionRequestTask(this.client).execute(builder.build().toString());
    }

    public void deleteStopSubscription(long id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("remove_stop_sub")
                .appendQueryParameter("id", Long.toString(id))
                .appendQueryParameter("device", this.devID);
        new DeleteStopSubscriptionRequestTask(this.client).execute(builder.build().toString());
    }

    public void addStopSub(long stopID, int h, int m) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("add_stop_sub")
                .appendQueryParameter("device", this.devID)
                .appendQueryParameter("stop_id", Long.toString(stopID))
                .appendQueryParameter("m", Long.toString(m))
                .appendQueryParameter("h", Long.toString(h));
        new AddStopSubscriptionRequestTask(this.client).execute(builder.build().toString());
    }

    public void getStopSubscriptions() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("get_stop_subs")
                .appendQueryParameter("device", this.devID);
        new GetStopSubscriptionRequestTask(this.client).execute(builder.build().toString());
    }
}

abstract class RequestTask extends AsyncTask<String, String, String>{
    OnServerTaskComplete activity;
    protected boolean isSuccess;

    public RequestTask(OnServerTaskComplete activity) {
        this.activity = activity;
        this.isSuccess = false;
    }

    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        Log.v("REST", uri[0]);

        try {
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
                Log.i("REST", responseString);
            } else{
                Log.i("REST", "error----");
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            return null;
        } catch (IOException e) {
            return null;
        }

        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        JSONParser parser = new JSONParser();
        JSONObject jsonobj = null;

        if (result != null) {
            try {
                jsonobj = (JSONObject)(parser.parse(result));
            } catch (ParseException e) {
            }
        }

        if (jsonobj != null) {
            this.isSuccess = (Boolean)jsonobj.get("success");
        }
        this.notify(jsonobj);
    }


    protected boolean isSuccess() {
        return this.isSuccess;
    }

    abstract protected void notify(JSONObject json);
}

class CreateRouteRequestTask extends RequestTask {
    public CreateRouteRequestTask(OnServerTaskComplete activity) {
        super(activity);
    }

    @Override
    protected void notify(JSONObject json) {
        if (!this.isSuccess()) {
            this.activity.createRouteResponse(-1);
        } else {
            long id = (Long)((JSONObject)(json.get("load"))).get("id");
            this.activity.createRouteResponse(id);
        }
    }
}

class GetRouteRequestTask extends RequestTask {
    public GetRouteRequestTask(OnServerTaskComplete activity) {
        super(activity);
    }

    @Override
    protected void notify(JSONObject json) {
        if (!this.isSuccess()) {
            this.activity.getRouteResponse(null);
        } else {
            JSONObject load = (JSONObject)json.get("load");
            this.activity.getRouteResponse(new Route(load));
        }
    }
}

class GetRouteListRequestTask extends RequestTask {
    public GetRouteListRequestTask(OnServerTaskComplete activity) {
        super(activity);
    }

    @Override
    protected void notify(JSONObject json) {
        if (!this.isSuccess()) {
            this.activity.getRouteListResponse(null);
        } else {
            ArrayList<Route> routes = new ArrayList<Route>();
            JSONArray arr = (JSONArray)((JSONObject)(json.get("dump"))).get("routes");
            Iterator<JSONObject> iterator = arr.iterator();

            while (iterator.hasNext()) {
                JSONObject item = iterator.next();
                routes.add(new Route((String)item.get("name"), (Long)item.get("id")));
            }

            this.activity.getRouteListResponse(routes);
        }
    }
}

class AddCoordinateRequestTask extends RequestTask {
    public AddCoordinateRequestTask(OnServerTaskComplete activity) {
        super(activity);
    }

    @Override
    protected void notify(JSONObject json) {
        this.activity.addCoordinateResponse(this.isSuccess());
    }
}

class AddStopRequestTask extends RequestTask {
    public AddStopRequestTask(OnServerTaskComplete activity) {
        super(activity);
    }

    @Override
    protected void notify(JSONObject json) {
        this.activity.addStopResponse(this.isSuccess());
    }
}

class SetCurrentBusPositionRequestTask extends RequestTask {
    public SetCurrentBusPositionRequestTask(OnServerTaskComplete activity) {
        super(activity);
    }

    @Override
    protected void notify(JSONObject json) {
        this.activity.setCurrentBusPositionResponse(this.isSuccess());
    }
}

class GetCurrentBusPositionRequestTask extends RequestTask {
    public GetCurrentBusPositionRequestTask(OnServerTaskComplete activity) {
        super(activity);
    }

    @Override
    protected void notify(JSONObject json) {
        if (!this.isSuccess()) {
            this.activity.getCurrentBusPositionResponse(null);
        } else {
            JSONObject load = (JSONObject)json.get("load");
            double lat = Double.parseDouble((String)load.get("lat"));
            double lng = Double.parseDouble((String) load.get("lng"));
            long diff = (Long)load.get("diff");
            this.activity.getCurrentBusPositionResponse(new BusPosition(new Coordinate(lat, lng), diff));
        }
    }
}

class DeleteStopSubscriptionRequestTask extends RequestTask {
    public DeleteStopSubscriptionRequestTask(OnServerTaskComplete activity) {
        super(activity);
    }

    @Override
    protected void notify(JSONObject json) {
        this.activity.deleteStopSubscriptionResponse(this.isSuccess());
    }
}

class AddStopSubscriptionRequestTask extends RequestTask {
    public AddStopSubscriptionRequestTask(OnServerTaskComplete activity) {
        super(activity);
    }

    @Override
    protected void notify(JSONObject json) {
        this.activity.addStopSubscriptionResponse(this.isSuccess());
    }
}

class GetStopSubscriptionRequestTask extends RequestTask {
    public GetStopSubscriptionRequestTask(OnServerTaskComplete activity) {
        super(activity);
    }

    @Override
    protected void notify(JSONObject json) {
        if (this.isSuccess()) {
            JSONArray arr = (JSONArray) json.get("dump");
            ArrayList<StopSubscription> stopSubs = new ArrayList<StopSubscription>();
            Iterator<JSONObject> it = arr.iterator();
            while (it.hasNext()) {
                stopSubs.add(new StopSubscription(it.next()));
            }
            this.activity.getStopSubscriptionsResponse(stopSubs);
        } else {
            this.activity.getStopSubscriptionsResponse(null);
        }
    }
}
