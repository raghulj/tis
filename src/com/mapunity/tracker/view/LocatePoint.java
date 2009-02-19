/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mapunity.tracker.view;

/**
 *
 * @author raghul
 */
public class LocatePoint {
    public double latitude;
    public double longitude;
    public String LocationName;
    
    public void setLocatePoint(String name,double lat,double lon)
    {
        this.latitude = lat;
        this.longitude = lon;
        this.LocationName = name;
    }

}
