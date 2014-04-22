package com.foobar.app;

import org.json.simple.JSONObject;

/**
 * Created by reidhoruff on 4/22/14.
 */
public class StopSubscription {
    private long stopID, ID, h, m;
    private String stopName;

    public StopSubscription(long stopID, long ID, long h, long m, String stopName) {
        this.stopID = stopID;
        this.ID = ID;
        this.h = h;
        this.m = m;
        this.stopName = stopName;
    }

    public StopSubscription(JSONObject dump) {
        this.stopID = (Long) dump.get("stop_id");
        this.h = (Long) dump.get("h");
        this.m = (Long) dump.get("m");
        this.stopName = (String) dump.get("stop_name");
        this.ID = (Long) dump.get("id");
    }

    public String toString() {
        return Long.toString(this.stopID) + ":" + this.stopName;
    }
}
