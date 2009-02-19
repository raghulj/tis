/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mapunity.tracker.view;



import javax.microedition.lcdui.*;

import com.mapunity.tracker.controller.Controller;
import com.mapunity.tracker.model.*;
import java.io.*;
import javax.microedition.io.*;
import com.mapunity.util.*;
/**
 *
 * @author raghul
 */

public class Locations extends List implements CommandListener {

    
       private TextField SearchQueryField;
       private Controller controller;
       private Command searchCommand;
       private Command cancelCommand;
       private Command selectOption;
       private Command backToOptions;
       private Form MapunityLocId;
       private Form SearchWindow;
       private TextField MapunityIdNo;
       private Command ViewId;
       private Locations loc;
       
       
       public  Locations(Controller controller) 
       {
       
        super("Locations ",List.EXCLUSIVE);
        this.controller = controller;
        loc = this;
        initList();
        selectOption = new Command("OK", Command.SCREEN, 1);
        cancelCommand = new Command("back", Command.BACK,2);
        backToOptions = new Command("back", Command.BACK,2);
          
        this.addCommand(selectOption);
        this.addCommand(cancelCommand);
        this.setCommandListener(this);
          

       //    this.initialize(controller); 
       
        }
       
     
    private void SearchWindow(){
         SearchWindow = new Form("Search Locations");
       this.SearchQueryField = new TextField("", "kora", 64, TextField.ANY);
       SearchQueryField.setString("");
       SearchWindow.addCommand(searchCommand = new Command("suggest", Command.SCREEN, 1));
       SearchWindow.addCommand(backToOptions);
       SearchWindow.append("Search for");
       SearchWindow.append(SearchQueryField);  
       SearchQueryField.setString("");
       SearchWindow.append("Enter first few characters of any locality and click on suggest. For example type \"kora\" for koramangala and select from list. ");
       SearchWindow.setCommandListener(this);
       controller.setCurrentScreen(SearchWindow);

    }
    
    private void MapunityLocID(){
        
        MapunityLocId = new Form("Enter MapID");
        MapunityLocId.append("");
        MapunityIdNo = new TextField("","",64,TextField.NUMERIC);
        MapunityLocId.append(MapunityIdNo);
         MapunityLocId.append("");
        MapunityLocId.append("You can get a BTIS MapID for your office, home, or other location at http://btis.in/mapid/");
        ViewId  = new Command("OK", Command.SCREEN, 1);
        MapunityLocId.addCommand(ViewId);
        MapunityLocId.addCommand(backToOptions);
         MapunityLocId.setCommandListener(this);
         controller.setCurrentScreen(MapunityLocId);
   
    }

            
      public void commandAction(Command command, Displayable displayable) {
        if (command == searchCommand) {
            
            if (SearchQueryField.getString().toString().length() > 1){
            controller.showsearchResults(SearchQueryField.getString().toString());
            }else{
                controller.showAlert("Enter the data for atleast 2 Letters", Alert.FOREVER, AlertType.INFO);
                controller.setCurrentScreen(this);
            }
                
        }
        if (command == cancelCommand) {
            this.goBack();
       }
        if ( command == selectOption)
        {
            if (this.isSelected(1) == true)
            {
                SearchWindow();
            }
            else if (this.isSelected(0) == true){
                MapunityLocID();
            }
        }
        if ( command == backToOptions){
            System.out.println("I am back");
            controller.setCurrentScreen(this);
            
        }
        if (command == ViewId){
            new Thread(){
                public void run()
                {
                   
                      try{
                           String [] data=null;
                    data = getLocationData(MapunityIdNo.getString());
                  
                        double aDoubleLat = Double.parseDouble(data[1]);
                        
                         if(data.length != 0 && data.length ==3){
                        System.out.println(data.length);
                    controller.showIdMarker(data[0],data[1], data[2]);
                    }
                    else{
                        controller.showAlert("Network Error", 0, AlertType.ERROR);
                    }
                        
                    }catch(Exception e){
                        controller.setCurrentScreen(loc);
                         new Thread(){public void run(){  
             controller.showAlert("Entered data not found", Alert.FOREVER, AlertType.INFO);
             
              }}.start();
                    }
                   
                    
                } 
            }.start();
            
        }
    }

  private void goBack() {
    
            controller.MainMenu();
        
    }
    public void clearQryField()
    {
        SearchQueryField.setString("");
    }
    
    public String getQuery()
    {
       return  SearchQueryField.getString();
    }
    
    
        private String [] getLocationData(String Str)
    {
         String uploadWebsite =  "http://"+controller.selectedCity.URL+"/php/getMapDtls.php?id=";
         String [] ArrayOfData=null;
         StreamConnection c = null;
         InputStream s = null;
         StringBuffer b = new StringBuffer();
        
         String url = uploadWebsite +Str;
         System.out.print(url);
         try {
          c = (StreamConnection)Connector.open(url);
          s = c.openDataInputStream();
          int ch;
          int k =0;
          while((ch = s.read()) != -1) {
               // System.out.print((char) ch);
              b.append((char) ch);   }
          
          String result = b.toString();
          if(!result.equals("")){
             
             ArrayOfData =StringUtil.split(result.toString().trim(),"~~");
          }
          }catch (Exception e){
              System.out.print("Pblm her e"+e);
              
              //controller.ShowPointingCanvas();
            //  controller.showAlert("Network Error", 3, AlertType.ERROR);
              
               new Thread(){public void run(){  
             controller.showAlert("Entered data not found", Alert.FOREVER, AlertType.INFO);
             
              }}.start();
          }
        
        return ArrayOfData;
        
        
    }
        
     public void initList(){
         this.deleteAll();
         if(controller.selectedCity.MAPID == 1){
             this.append("BTIS MapID", null);    
         }
                   
          this.append("Search Locations", null);
     }
       
}
