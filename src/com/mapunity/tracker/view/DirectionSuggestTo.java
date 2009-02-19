/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mapunity.tracker.view;

import javax.microedition.lcdui.*;
import com.mapunity.tracker.controller.Controller;
import java.io.*;
import javax.microedition.io.*;
import com.mapunity.util.*;



/**
 *
 * @author raghul
 */
public class DirectionSuggestTo extends List implements CommandListener {
    
    private Controller controller;
    
        /** Commands */
    private Command refreshCommand;
    private Command selectCommand;
    private Command cancelCommand;
    
    public String searchQuery;
    public String [] ArrayOfResults;
    public DirectionSuggestTo directionSuggest;
    private String resultLatitude;
    private String resultLongitude;
    private String resultPlace;
     public String [] DirectionArray;
 
     
    public DirectionSuggestTo(Controller controller,String qry)
    {
         super("Suggestions "+qry,List.IMPLICIT);
         this.searchQuery= qry;
        this.initializeCommands();
        this.controller = controller;
        directionSuggest = this;
        //refreshSearchList();
    }
    
    private void initializeCommands() {
        refreshCommand = new Command("Refresh query", Command.ITEM, 2);
        addCommand(refreshCommand);
        selectCommand = new Command("Direct Me", Command.ITEM, 1);
        addCommand(selectCommand);
        setSelectCommand(selectCommand);
        cancelCommand = new Command("back", Command.SCREEN, 3);
        addCommand(cancelCommand);
        
        setCommandListener(this);
    }
   
    public void commandAction(Command command, Displayable displayable) {
        if (command == refreshCommand) {
            // Refresh devices
          //  refresh();
            this.deleteAll();
            this.refreshSearchList();
        }
        else if(command == cancelCommand) {
           
            this.controller.ShowDirectionsFormForTo();
        }
        else if(command == selectCommand) {
           
       //  System.out.println( "-------"+ SearchResult.getSelectedIndex());
            //resultPlace = SearchResult.
                    
        String[] chunk =     StringUtil.split(ArrayOfResults[directionSuggest.getSelectedIndex()].toString(),"~~");
        String temp = chunk[0].toString().toUpperCase();
            System.out.println("From field"+temp);
            String toF="";
            String [] tt = StringUtil.split(temp, " ");
             System.out.println("From field"+tt);
            for(int y =0;y<tt.length;y++)
            {
                if(y !=tt.length-1)
                {
                toF = toF+""+tt[y]+"%20";
                System.out.println("From field"+toF);
                }
                else{
                    toF = toF+tt[y];
                }
            }
            
            this.controller.ToField = toF;
            GetDiretions();
            //this.controller.showPointinTrail("test",resultLatitude, resultLongitude);
        }
    }
    

    
    
        public void refreshSearchList()
    {
        //PointFriend.deleteAll();
//            SearchResult.setTitle("Search results for "+searchQuery);
             System.out.println("qry "+searchQuery);
             directionSuggest.setTitle("Suggestions for " +searchQuery);
          directionSuggest.deleteAll();
             
                 new Thread(){      public void run() { 
      controller.showProgressBar();
          String [] chunk;
         ArrayOfResults =GetDataFromSite(searchQuery);
         if(ArrayOfResults != null)
         {
           System.out.println("ArrayOfResults "+ArrayOfResults);
           try{
             for (int i=0;i< ArrayOfResults.length-1;i++){
                 chunk =StringUtil.split(ArrayOfResults[i].toString(),"~~");
                directionSuggest.append(chunk[0], null);
               //                         this.append(chunk[0],null);
                 System.out.println("!!!!!!"+chunk[0]);
               //  BuddyLat[i]=chunk[1].toString();// parseLatLongValue(chunk[1], false);
               //  BuddyLng[i] = chunk[2].toString();
                 System.out.println("lat lat"+chunk[1].toString());
                 System.out.println("++"+ chunk[2].toString());
                 System.out.println("-------------------------");
               //   System.out.println("555++"+ ArrayOfResults[i].toString());
                 controller.setCurrentScreen(directionSuggest);
                 
             }
           }catch( Exception r ){
           System.out.println("In refreshSearchList :"+r);
           }
         }else{
            // new Thread(){public void run(){  
          //   controller.showAlert("Entered data not found", Alert.FOREVER, AlertType.INFO);}}.start();
             controller.setCurrentScreen(controller.directionsToField);
             
              new Thread(){public void run(){  
             controller.showAlert("Entered data not found", Alert.FOREVER, AlertType.INFO);
             
              }}.start();
         }
        
        }
          
         
         }.start();
               
    }
    
    private String [] GetDataFromSite(String Str)
    {
        
         String uploadWebsite =  "http://"+controller.selectedCity.URL+"/php/mobile_search.php?q=";
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
                System.out.print((char) ch);
              b.append((char) ch);   }
          
          String result = b.toString();
       //   System.out.print("in thread---------");
         // System.out.print(result);
          if(!result.equals("")){
             
             ArrayOfData =StringUtil.split(result.toString().trim(),"||");
          }
          }catch (Exception e){
              System.out.print(e);
              controller.ShowPointingCanvas();
              controller.showAlert("Network Error", 3, AlertType.ERROR);
          }
        
        return ArrayOfData;
        
        
    }
    
    public void setQueryWord(String qry)
    {
        searchQuery  = qry;
    }
    
     public void GetDiretions()
    {
         controller.showProgressBar();
           
                 new Thread(){      public void run() { 
      
          String [] chunk;
         DirectionArray =fetchDirectionsData(controller.FromField+"&dst="+controller.ToField+"&city=Bangalore");
        // DirectionArray =GetDataFromSite("KORAMANGALA&dst=INDIRANAGAR&city=Bangalore");
           try{
            
            if(DirectionArray !=null)
            {
             
             controller.DirectionsArray = DirectionArray;
             controller.drawDirections();
             //.showAlert("General Information", Alert.FOREVER, AlertType.INFO);
             controller.ShowPointingCanvas();

            }
             
            
             //controller.showPointinTrail("",initialLat,initialLon);
           }catch( Exception r ){
           System.out.println("In refreshSearchList :"+r);
            
            //alert.setCommandListener(this);
            controller.showAlert("Network Error", Alert.FOREVER, AlertType.ERROR);
             controller.setCurrentScreen(controller.pointresultCanvas);
           
           
           
          
           }
        
        }
          
         
         }.start();
      
             
        //controller.showDirectCanvas(initialLat,initialLon,DirectionArray);
               
    }
    
    public String [] fetchDirectionsData(String str)
     {
        String uploadWebsite =  "http://"+controller.selectedCity.URL+"/godown/scripts/php/cityroutebackend_ol_mobile.php?org="+str;
         String [] ArrayOfData=null;
         StreamConnection c = null;
         InputStream s = null;
         StringBuffer b = new StringBuffer();
        
         //String url = uploadWebsite +Str;
        System.out.print(uploadWebsite);
         try {
          c = (StreamConnection)Connector.open(uploadWebsite);
          s = c.openDataInputStream();
          int ch;
          int k =0;
          while((ch = s.read()) != -1) {
               // System.out.print((char) ch);
              b.append((char) ch);   }
          
          String result = b.toString();
          //test for km
      //    result = "12.935||77.627~~12.9347||77.6267~~12.9347||77.6267~~12.9344||77.6263~~12.9344||77.6263~~12.9345||77.6261~~12.9345||77.6261~~12.935||77.6253~~12.935||77.6253~~12.9352||77.6249~~12.9352||77.6249~~12.9353||77.6247~~12.9353||77.6247~~12.9353||77.6246~~12.9351||77.6244~~12.9351||77.6244~~12.9348||77.6241~~12.9348||77.6241~~12.9348||77.6241~~12.9347||77.624~~12.9343||77.6239~~12.9336||77.6236~~12.9336||77.6236~~12.933||77.6234~~12.933||77.6234~~12.9318||77.6229~~12.9318||77.6229~~12.9308||77.6224~~12.9305||77.6223~~12.9305||77.6223~~12.93||77.622~~12.9298||77.622~~12.9298||77.622~~12.9294||77.6218~~12.9294||77.6218~~12.9289||77.6216~~12.9289||77.6216~~12.9284||77.6215~~12.9284||77.6215~~12.9279||77.6212~~12.9279||77.6212~~12.9277||77.6212~~12.9277||77.6212~~12.9276||77.6211~~12.9276||77.6211~~12.9256||77.6202~~12.9256||77.6202~~12.9256||77.6202~~12.9254||77.62~~12.9254||77.62~~12.9248||77.6189~~12.9248||77.6189~~12.9246||77.6185~~12.9246||77.6185~~12.924||77.6189~~12.924||77.6189~~12.9237||77.6191~~12.9237||77.6191~~12.9232||77.6193~~12.9232||77.6193~~12.9228||77.6196~~12.9228||77.6196~~12.9227||77.6197~~12.9227||77.6197~~12.9224||77.6194~~12.9224||77.6194~~12.9218||77.6189~~12.9218||77.6189~~12.9217||77.6188~~12.9216||77.6187~~12.9216||77.6187~~12.9214||77.6186~~12.9214||77.6186~~12.9214||77.6185~~12.9214||77.6185~~12.9212||77.6183~~12.9212||77.6183~~12.9209||77.6181~~12.9209||77.6181~~12.9208||77.618~~12.9208||77.618~~12.9207||77.6179~~12.9207||77.6179~~12.9205||77.6178~~12.92||77.6176~~12.92||77.6176~~12.9199||77.6177~~12.9199||77.6177~~12.9198||77.6176~~12.9197||77.6175~~12.9196||77.6174~~12.9195||77.6174~~12.9193||77.6173~~12.9191||77.6173~~12.9188||77.6173~~12.9187||77.6173~~12.9187||77.6173~~12.9186||77.6173~~12.9186||77.6173~~12.9183||77.6173~~12.9183||77.6175~~12.9183||77.6175~~12.9181||77.6174~~12.9181||77.6174~~12.918||77.6174~~12.918||77.6174~~12.9179||77.6174~~12.9177||77.6175~~12.9176||77.6174~~12.9176||77.6174~~12.9172||77.6174~~12.9172||77.6174~~12.9169||77.6174~~12.9169||77.6174~~12.9169||77.6174~~12.9166||77.6172~~12.9166||77.6172~~12.9161||77.6169~~12.9159||77.6169~~12.9159||77.6169~~12.916||77.6167~~12.916||77.6167~~12.916||77.6162~~12.916||77.6162~~12.916||77.616~~12.916||77.616~~12.9161||77.6153~~12.9161||77.6153~~12.9161||77.6146~~12.9161||77.6146~~12.9162||77.614~~12.9162||77.614~~12.9162||77.6138~~12.9162||77.6138~~12.9162||77.6133~~12.9163||77.6122~~12.9164||77.6118~~12.9164||77.611~~12.9164||77.611~~12.9165||77.6097~~12.9165||77.6093~~12.9166||77.6089~~12.9166||77.6089~~12.9166||77.6082~~12.9166||77.6082~~12.9166||77.6079~~12.9166||77.6079~~12.9167||77.6076~~12.9167||77.6076~~12.9167||77.6073~~12.9167||77.6073~~12.9167||77.6069~~12.9167||77.6069~~12.9167||77.6064~~12.9168||77.6061~~12.9168||77.6059~~12.9168||77.6059~~12.9168||77.6057~~12.9168||77.6053~~12.9169||77.6047~~12.9169||77.6047~~12.9169||77.6043~~12.9169||77.6034~~12.9169||77.6034~~12.9169||77.6026~~12.9169||77.6026~~12.9169||77.6024~~12.9169||77.6024~~12.9169||77.602~~12.9169||77.602~~12.9169||77.6018~~12.9169||77.6018~~12.917||77.6014~~12.917||77.6014~~12.917||77.6012~~12.917||77.6012~~12.917||77.6007~~12.917||77.6007~~12.917||77.6004~~12.917||77.6004~~12.917||77.6004~~12.9167||77.6003~~12.9165||77.6003~~12.9165||77.6003~~12.916||77.6003~~12.916||77.6003~~12.9145||77.6002~~12.9145||77.6002~~12.9138||77.6003~~12.9138||77.6003~~12.9131||77.6003~~12.913||77.6003~~12.913||77.6003~~12.912||77.6004~~12.912||77.6004~~12.9117||77.6005~~12.911||77.6005~~12.911||77.6005~~12.911||77.6002~~12.911||77.6002~~12.9109||77.5995~~12.9109||77.5995~~12.9109||77.5991~~ 5.7";
       //   System.out.print("in thread---------");
         // System.out.print(result);
          if(!result.equals("")){
             
             ArrayOfData =StringUtil.split(result.toString().trim(),"~~");
          }
          }catch (Exception e){System.out.print(e);
           controller.showAlert("Network Error", 3, AlertType.ERROR);
          
          
          }
        
        return ArrayOfData;
     }
}
