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
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

    public ServerCommunicator(OnServerTaskComplete client) {
        this.client = client;
       this.httpclient = new DefaultHttpClient();
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

    public void addStopSub(int id, String device, int h, int m)    {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("add_stop_sub")
                .appendQueryParameter("id", Integer.toString(id))
                .appendQueryParameter("device", device)
                .appendQueryParameter("h", Integer.toString(h))
                .appendQueryParameter("m", Integer.toString(m));
//        new AddStopSubRequestTask(this.client).execute(builder.build().toString());
    }

    public void getStopSubs(String device)    {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(ADDRESS).appendPath("get_stop_subs")
                .appendQueryParameter("device", device);
        new GetStopSubsRequestTask(this.client).execute(builder.build().toString());
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
                //Log.i("REST", responseString);
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
            double max_lat = 0.0;
            double min_lat = 0.0;
            double max_lng = 0.0;
            double min_lng = 0.0;
            int num_coords = 0;

            JSONObject load = (JSONObject)json.get("load");
            String name = (String) load.get("name");
            Long id = (Long) load.get("id");
            Route route = new Route(name, id);

            JSONArray coordinates = (JSONArray) load.get("coordinates");
            JSONArray stops = (JSONArray) load.get("stops");

            Iterator<JSONObject> iterator = coordinates.iterator();
            while (iterator.hasNext()) {
                JSONObject coord = iterator.next();
                double lat = Double.parseDouble((String)coord.get("lat"));
                double lng = Double.parseDouble((String)coord.get("lng"));
                if (num_coords == 0) {
                    max_lat = min_lat = lat;
                    max_lng = min_lng = lng;
                } else {
                    max_lat = Math.max(max_lat, lat);
                    min_lat = Math.min(min_lat, lat);
                    max_lng = Math.max(max_lng, lng);
                    min_lng = Math.min(min_lng, lng);
                }
                num_coords++;
                route.addCoordinate(new Coordinate(lat, lng));
            }

            iterator = stops.iterator();
            while (iterator.hasNext()) {
                JSONObject stop = iterator.next();
                double lat = Double.parseDouble((String)stop.get("lat"));
                double lng = Double.parseDouble((String)stop.get("lng"));
                String stopName = (String)stop.get("name");
                route.addStop(new BusStop(stopName, lat, lng));
            }

            this.activity.getRouteResponse(route);
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
/*
class AddStopSubRequestTask extends RequestTask {
    public AddStopSubRequestTask(OnServerTaskComplete activity) {
        super(activity);
    }

    @Override
    protected void notify(JSONObject json) {
        this.activity.addStopSubResponse(this.isSuccess());
    }
}
*/

class GetStopSubsRequestTask extends RequestTask {
    public GetStopSubsRequestTask(OnServerTaskComplete activity) {
        super(activity);
    }

    @Override
    protected void notify(JSONObject json) {
        if (!this.isSuccess()) {
            this.activity.getRouteResponse(null);
        } else {
            JSONObject load = (JSONObject)json.get("load");
            String device = (String) load.get("device");
            Long id = (Long) load.get("id");
            int h = Integer.parseInt((String)load.get("h"));
            int m = Integer.parseInt((String)load.get("m"));
            this.activity.getStopSubsResponse(device,h,m,id);
        }
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
