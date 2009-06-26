/*
 * Mapunity.java
 *
 * Copyright (C) 2005-2006 Tommi Laukkanen
 * http://www.substanceofcode.com
 *
 * Created on August 14th 2006
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package com.mapunity.tracker;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import com.mapunity.tracker.controller.*;
import com.mapunity.tracker.view.Logger;
import com.mapunity.util.DateTimeUtil;
import com.mapunity.util.Version;
//import com.mapunity.tracker.*;

/**
 * TODO
 * @author  Tommi Laukkanen
 */
public class Mapunity extends MIDlet {
    
    /** 
     * The current Version of this Mobile Trail Explorer. (Major, Minor, Build) 
     */
    public static final Version VERSION = new Version(0, 5, 0);
    
    /** Beta flag */
    public static final boolean BETA = false;
    
    /**
     * Local Controller object
     */
    private static Controller controller;
    
    private List MenuList;

    
    
    
    /**
     * Constructor:
     * <ul>
     * <li> Get a Display
     * <li> Instanciate the Controlle
     * <li> Display the splash
     * </ul>
     */
    public Mapunity() {

        MenuList = new List("BTIS",List.IMPLICIT);
        MenuList.append("Directions",null );
        MenuList.append("Search", null);
        MenuList.append("Traffic Cams", null);
        MenuList.append("Bus routes", null);
        MenuList.append("User Guide", null);
        MenuList.append("Acknowledgment", null);
        MenuList.append("Exit ", null);
        
        

    }
    
    /**
     * MIDlet state change -> Active state
     */
    public void startApp() {
       /* try {
            if (controller.checkIfPaused()==true) {
                    controller.unpause();    		
            }    	                                      
            Logger.debug("TrailExplorerMidlet.startApp() called @ " + DateTimeUtil.convertToTimeStamp(System.currentTimeMillis(), true));
        } catch(Exception ex) {
            ex.printStackTrace();
        }*/
            	try{
	        Display disp = Display.getDisplay(this);
               // MainMenu mm = new MainMenu();
              //  disp.setCurrent(mm);
                
	        controller = new Controller(this, disp);
             //  controller.initLat = getAppProperty("user-Latitude");
              //  controller.initLon = getAppProperty("user-Longitude"); 
	       controller.showSplash();
    	}catch(Exception any){
    		any.printStackTrace();
    	}
    }
    
    /**
     * MIDlet state change -> Paused state
     */
    public void pauseApp() {
        if(controller.getStatusCode() == Controller.STATUS_RECORDING){
            controller.pause();
            Logger.debug("TrailExplorerMidlet.pauseApp() called @ " + DateTimeUtil.convertToTimeStamp(System.currentTimeMillis(), true));
        }
    }
    
    /**
     * MIDlet state change -> Destroyed state - we must terminate ourselves
     */
    public void destroyApp(boolean unconditional) {
        Logger.debug("TrailExplorerMidlet.destroyApp() called @ " + DateTimeUtil.convertToTimeStamp(System.currentTimeMillis(), true));
    }
    
    
      
}
