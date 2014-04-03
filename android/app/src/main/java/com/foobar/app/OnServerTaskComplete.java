package com.foobar.app;

/**
 * Created by reidhoruff on 4/2/14.
 */
public interface OnServerTaskComplete {
    public void createRouteResponse(String response);
    public void getRouteResponse(String response);
    public void getRouteListResponse(String response);
    public void addCoordinateResponse(String response);
    public void setCurrentBusPositionResponse(String response);
    public void getCurrentBusPositionResponse(String response);
}
