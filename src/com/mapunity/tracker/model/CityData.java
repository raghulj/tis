/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mapunity.tracker.model;

/**
 *
 * @author raghul
 */
public class CityData {
    
    public String Name;
    public String URL;
    public int LOCATION ;
    public int MAPID;
    public int DIRECTIONS ;
    public int BUS ;
    public int CAMERAS ;
    public int HOTSPOTS ;
    public int TRAFFIC_FINES;
    public int BUS_POSITION ;
    public String LATITUDE;
    public String LONGITUDE;
    
    
    public CityData(String name,String url,int loc,int mpid,int dir,int bus,int cam,int htpt,int traf,int buspt,String lat,String lng){
        Name = name;
        URL = url;
        LOCATION = loc;
        MAPID = mpid;
        DIRECTIONS = dir;
        BUS = bus;
        CAMERAS = cam;
        HOTSPOTS = htpt;
        TRAFFIC_FINES = traf;
        BUS_POSITION = buspt;
        LATITUDE = lat;
        LONGITUDE = lng;
     }

}
