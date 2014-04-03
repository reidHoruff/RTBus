package com.foobar.app;

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
import java.net.URI;

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
    final String ADDRESS = "reidhoruff.webfactional.com";

    public ServerCommunicator() {
       this.httpclient = new DefaultHttpClient();
    }

    public void createRoute(OnServerTaskComplete activity, String name) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("create_route").appendQueryParameter("name", name);
        new CreateRouteRequestTask(activity).execute(builder.build().toString());
    }

    public void getRoute(OnServerTaskComplete activity, int id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("get_route")
            .appendQueryParameter("id", Integer.toString(id));
        new GetRouteRequestTask(activity).execute(builder.build().toString());
    }

    public void getRouteList(OnServerTaskComplete activity) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("get_route_list");
        new GetRouteListRequestTask(activity).execute(builder.build().toString());
    }

    public void addCoordiante(OnServerTaskComplete activity, int id, double lat, double lng) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("add_coordinate")
            .appendQueryParameter("id", Integer.toString(id))
            .appendQueryParameter("lat", Double.toString(lat))
            .appendQueryParameter("lng", Double.toString(lng));
        new AddCoordinateRequestTask(activity).execute(builder.build().toString());
    }

    public void setCurrentPosition(OnServerTaskComplete activity, int id, double lat, double lng) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("set_cur_position")
            .appendQueryParameter("id", Integer.toString(id))
            .appendQueryParameter("lat", Double.toString(lat))
            .appendQueryParameter("lng", Double.toString(lng));
        new SetCurrentBusPositionRequestTask(activity).execute(builder.build().toString());
    }

    public void getCurrentPosition(OnServerTaskComplete activity, int id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("get_cur_position")
                .appendQueryParameter("id", Integer.toString(id));
        new GetCurrentBusPositionRequestTask(activity).execute(builder.build().toString());
    }
}


abstract class RequestTask extends AsyncTask<String, String, String>{
    OnServerTaskComplete activity;

    public RequestTask(OnServerTaskComplete activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;

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
        this.notify(result);
    }

    protected void notify(String result) {
    }
}

class CreateRouteRequestTask extends RequestTask {
    public CreateRouteRequestTask(OnServerTaskComplete activity) {
        super(activity);
    }

    @Override
    protected void notify(String response) {
       this.activity.createRouteResponse(response);
    }
}

class GetRouteRequestTask extends RequestTask {
    public GetRouteRequestTask(OnServerTaskComplete activity) {
        super(activity);
    }

    @Override
    protected void notify(String response) {
        this.activity.getRouteResponse(response);
    }
}

class GetRouteListRequestTask extends RequestTask {
    public GetRouteListRequestTask(OnServerTaskComplete activity) {
        super(activity);
    }

    @Override
    protected void notify(String response) {
        this.activity.getRouteListResponse(response);
    }
}

class AddCoordinateRequestTask extends RequestTask {
    public AddCoordinateRequestTask(OnServerTaskComplete activity) {
        super(activity);
    }

    @Override
    protected void notify(String response) {
        this.activity.addCoordinateResponse(response);
    }
}

class SetCurrentBusPositionRequestTask extends RequestTask {
    public SetCurrentBusPositionRequestTask(OnServerTaskComplete activity) {
        super(activity);
    }

    @Override
    protected void notify(String response) {
        this.activity.setCurrentBusPositionResponse(response);
    }
}

class GetCurrentBusPositionRequestTask extends RequestTask {
    public GetCurrentBusPositionRequestTask(OnServerTaskComplete activity) {
        super(activity);
    }

    @Override
    protected void notify(String response) {
        this.activity.getCurrentBusPositionResponse(response);
    }
}
