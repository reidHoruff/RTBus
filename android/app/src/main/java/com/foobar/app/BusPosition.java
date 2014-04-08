package com.foobar.app;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by reidhoruff on 4/3/14.
 */
public class BusPosition {
    public Coordinate coordinate;

    //time since last update
    public long diff;

    public BusPosition(Coordinate coordinate, long diff) {
        this.coordinate = coordinate;
        this.diff = diff;
    }

    public LatLng toLatLng() {
        return this.coordinate.toLatLng();
    }

    public String toString() {
        return this.coordinate.toString() + ": " + this.diff;
    }
}
