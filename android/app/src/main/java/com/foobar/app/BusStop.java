package com.foobar.app;

import com.google.android.gms.maps.model.MarkerOptions;

import org.json.simple.JSONObject;

/**
 * Created by reidhoruff on 4/3/14.
 */
public class BusStop {
    public Coordinate coordinate;
    public String name;
    public long ID;

    public BusStop(String name, double lat, double lng) {
        this(name, new Coordinate(lat, lng));
    }

    public BusStop(String name, Coordinate coordinate) {
        this.name = name;
        this.coordinate = coordinate;
    }

    public BusStop(JSONObject stop) {
        double lat = Double.parseDouble((String)stop.get("lat"));
        double lng = Double.parseDouble((String)stop.get("lng"));
        String stopName = (String)stop.get("name");
        this.name = stopName;
        this.coordinate = new Coordinate(lat, lng);
        this.ID = (Long) stop.get("id");
    }

    public long getID() {
        return this.ID;
    }

    public MarkerOptions toMarker() {
        return new MarkerOptions().position(this.coordinate.toLatLng());
    }

    public String toString() {
        return this.name;
    }
}
