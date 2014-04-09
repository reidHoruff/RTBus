package com.foobar.app;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by reidhoruff on 4/3/14.
 */
public class Coordinate {
    public double lat;
    public double lng;

    public Coordinate(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public LatLng toLatLng() {
        return new LatLng(this.lat, this.lng);
    }

    public String toString() {
        return "(" + Double.toString(this.lat) + ", " + Double.toString(this.lng) + ")";
    }
}
