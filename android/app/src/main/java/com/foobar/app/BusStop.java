package com.foobar.app;

import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by reidhoruff on 4/3/14.
 */
public class BusStop {
    public Coordinate coordinate;
    public String name;

    public BusStop(String name, double lat, double lng) {
        this(name, new Coordinate(lat, lng));
    }

    public BusStop(String name, Coordinate coordinate) {
        this.name = name;
        this.coordinate = coordinate;
    }

    public MarkerOptions toMarker() {
        return new MarkerOptions().position(this.coordinate.toLatLng());
    }

    public String toString() {
        return this.name + ": " + this.coordinate.toString();
    }
}
