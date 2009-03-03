/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mapunity.tracker.view;

import com.mapunity.net.Downloader;
import com.mapunity.tracker.controller.Controller;
import com.mapunity.tracker.model.CityData;
import com.mapunity.tracker.model.RecorderSettings;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

/**
 *
 * @author raghul
 */
public class CitySelection extends List implements CommandListener {
    
    private Controller controller;
    
    private Command cancel;
    private Command ok;
    private Command updateCityList;
    private RecorderSettings settings;
    private Downloader dwn;
    private Hashtable cityMat;
    
    
    public CitySelection(Controller controler){
        
        super("Select City",Choice.EXCLUSIVE);
        this.controller = controler;
        settings = controller.getSettings();
        initList();
        
        ok = new Command("OK", Command.OK, 1);
        updateCityList = new Command("Update list", Command.SCREEN, 2);
        cancel = new Command("Cancel",Command.CANCEL,3);
        
        this.addCommand(ok);
        this.addCommand(updateCityList);
        this.addCommand(cancel);
        
        this.setCommandListener(this);
                
    }

    public void commandAction(Command command,Displayable displayable){
        
        if(command == cancel){
             
             System.out.println(settings.getCity());
             controller.MainMenu();
        }
        if(command == ok){
             
             settings.setCity(this.getString(getSelectedIndex()));
             controller.selectedCity = (CityData) controller.cityHash.get(this.getString(getSelectedIndex()));
             settings.setCity(controller.selectedCity.Name);
             
             controller.showAlert("selected city saved", 0, AlertType.INFO);
             controller.MainMenu();
        }
        if(command == updateCityList){
           cityMat = new Hashtable();
           new Thread(){
               public void run(){
                   dwn = new Downloader(controller);
                   String cityData = dwn.requestForData(controller.cityListURL);
                   System.out.println("The downloaded value "+cityData);
                   settings.setCityMatrix(cityData);
                   System.out.println(" from local "+settings.getCityMatrix());
                   controller.loadCityMatrix();
                   initList();
               }
           }.start();
           
           
        }
    }
    
    public void initList(){
        
        this.deleteAll();
        controller.loadCityMatrix();
        try{
            Enumeration e = controller.cityHash.keys();
            while(e.hasMoreElements()){
             this.append(e.nextElement().toString(), null);       
            }
            
        }catch(Exception e){
            System.out.println("Error in parsing the hash "+e);
            this.append(controller.BLR, null);
            this.append(controller.HYD,null);
        }
        
    }
}
