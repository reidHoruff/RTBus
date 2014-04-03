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
public class ServerCommunicator {
    HttpClient httpclient = null;
    final String ADDRESS = "reidhoruff.webfactional.com";

    public ServerCommunicator() {
       this.httpclient = new DefaultHttpClient();
    }

    public void createRoute(OnServerTaskComplete activity, String name) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("create_route").appendQueryParameter("name", name);
        new RequestTask(activity).execute(builder.build().toString());
    }

    public void getRoute(OnServerTaskComplete activity, int id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("get_route")
            .appendQueryParameter("id", Integer.toString(id));
        new RequestTask(activity).execute(builder.build().toString());
    }

    public void getRouteList(OnServerTaskComplete activity) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("get_route_list");
        new RequestTask(activity).execute(builder.build().toString());
    }

    public void addCoordiante(OnServerTaskComplete activity, int id, double lat, double lng) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("add_coordinate")
            .appendQueryParameter("id", Integer.toString(id))
            .appendQueryParameter("lat", Double.toString(lat))
            .appendQueryParameter("lng", Double.toString(lng));
        new RequestTask(activity).execute(builder.build().toString());
    }

    public void setCurrentPosition(OnServerTaskComplete activity, int id, double lat, double lng) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("set_cur_position")
            .appendQueryParameter("id", Integer.toString(id))
            .appendQueryParameter("lat", Double.toString(lat))
            .appendQueryParameter("lng", Double.toString(lng));
        new RequestTask(activity).execute(builder.build().toString());
    }

    public void getCurrentPosition(OnServerTaskComplete activity, int id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("get_cur_position")
                .appendQueryParameter("id", Integer.toString(id));
        new RequestTask(activity).execute(builder.build().toString());
    }
}


class RequestTask extends AsyncTask<String, String, String>{
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
        this.activity.onServerTaskComplete(result);
    }
}

