package com.foobar.app;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.charset.CoderMalfunctionError;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by reidhoruff on 4/3/14.
 */
public class Route {
    private String name;
    private long id;
    public boolean isActive;
    public BusPosition position;
    public PolylineOptions polyline;
    public Polyline polylineUpdate;
    private MarkerOptions marker;
    private Marker markerUpdate;
    private Coordinate center = null;
    private LatLngBounds.Builder latLngBoundsBuilder;

    ArrayList<Coordinate> coordinates;
    ArrayList<BusStop> stops;

    public Route() {
        this.isActive = false;
        this.coordinates = new ArrayList<Coordinate>();
        this.stops = new ArrayList<BusStop>();
        this.polyline = new PolylineOptions();
        this.marker = new MarkerOptions();
        this.marker.position(new LatLng(100.0, 100.0));
        this.latLngBoundsBuilder = new LatLngBounds.Builder();
    }

    public Route(String name, long id) {
        this();
        this.name = name;
        this.id = id;
    }

    public Route(JSONObject load) {
        this();

        this.name = (String) load.get("name");
        this.id = (Long) load.get("id");

        JSONArray coordinates = (JSONArray) load.get("coordinates");
        JSONArray stops = (JSONArray) load.get("stops");

        Iterator<JSONObject> iterator = coordinates.iterator();
        while (iterator.hasNext()) {
            JSONObject coord = iterator.next();
            this.addCoordinate(new Coordinate(coord));
        }

        iterator = stops.iterator();
        while (iterator.hasNext()) {
            JSONObject stop = iterator.next();
            this.addStop(new BusStop(stop));
        }
    }

    public void setCenter(Coordinate center) {
        this.center = center;
    }

    public Coordinate getCenter() {
        return this.center;
    }

    public long getID() {
        return this.id;
    }

    public void centerMap(GoogleMap map) {
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(this.latLngBoundsBuilder.build(), 20));
    }

    public void addCoordinate(Coordinate coordinate) {
        this.coordinates.add(coordinate);
        this.latLngBoundsBuilder.include(coordinate.toLatLng());
    }

    public void addCoordinate(double lat, double lng) {
        this.addCoordinate(new Coordinate(lat, lng));
    }

    public void addStop(BusStop stop) {
        this.stops.add(stop);
    }

    public void setBusPosition(BusPosition pos) {
        this.position = pos;

        if (pos != null) {
            Log.v("REST", "updating marker");
            this.markerUpdate.setPosition(pos.toLatLng());
        }

        if (this.position == null || this.position.diff >= 30) {
          this.isActive = false;
          this.polylineUpdate.setColor(Color.GRAY);
        } else {
            this.isActive = true;
            this.polylineUpdate.setColor(Color.argb(0xFF, 0x1E, 0x90, 0xFF));
        }
    }

    protected PolylineOptions toPolyline() {
        this.polyline.geodesic(true).width(8);

        this.polyline.color(Color.GRAY);

        for (Coordinate coord: this.coordinates) {
            this.polyline.add(coord.toLatLng());
        }

        //complete loop
        if (this.coordinates.size() > 0) {
            this.polyline.add(this.coordinates.get(0).toLatLng());
        }

        return this.polyline;
    }

    public void drawRouteAndStops(GoogleMap map) {
        this.polylineUpdate = map.addPolyline(this.toPolyline());

        for (BusStop stop: this.stops) {
            map.addMarker(stop.toMarker());
        }

        this.markerUpdate = map.addMarker(this.marker);
    }

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }
}
