package com.foobar.app;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by reidhoruff on 4/3/14.
 */
public class Route {
    public String name;
    public long id;
    public boolean isActive;
    public BusPosition position;
    public PolylineOptions polyline;

    ArrayList<Coordinate> coordinates;
    ArrayList<BusStop> stops;

    public Route(String name, long id) {
        this.isActive = false;
        this.name = name;
        this.id = id;
        this.coordinates = new ArrayList<Coordinate>();
        this.stops = new ArrayList<BusStop>();
        this.polyline = new PolylineOptions();
    }

    public void addCoordinate(Coordinate coordinate) {
        this.coordinates.add(coordinate);
    }

    public void addCoordinate(double lat, double lng) {
        this.addCoordinate(new Coordinate(lat, lng));
    }

    public void addStop(BusStop stop) {
        this.stops.add(stop);
    }

    public void setBusPosition(BusPosition pos) {
        this.position = pos;

        if (this.position == null || this.position.diff >= 60) {
          this.isActive = false;
          this.polyline.color(Color.RED);
        } else {
            this.isActive = true;
            this.polyline.color(Color.BLUE);
        }
    }

    protected PolylineOptions toPolyline() {
        this.polyline.geodesic(true).width(4);

        if (this.isActive) {
            this.polyline.color(Color.BLUE);
        } else {
            this.polyline.color(Color.RED);

        }

        for (Coordinate coord: this.coordinates) {
            this.polyline.add(coord.toLatLng());
        }

        return this.polyline;
    }


    public void draw(GoogleMap map) {
        map.addPolyline(this.toPolyline());

        for (BusStop stop: this.stops) {
            map.addMarker(stop.toMarker());
        }
    }

    public String toString() {
        return this.name + "(" + this.id + ")";
    }
}
