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
public class searchResults extends List implements CommandListener {
    
    private Controller controller;
    
        /** Commands */
    private Command refreshCommand;
    private Command selectCommand;
    private Command cancelCommand;
    
    public String searchQuery;
    public String [] ArrayOfResults;
    public searchResults SearchResult;
    private String resultLatitude;
    private String resultLongitude;
    private String resultPlace;
 
     
    public searchResults(Controller controller,String qry)
    {
         super("Search results for "+qry,List.IMPLICIT);
         this.searchQuery= qry;
        this.initializeCommands();
        this.controller = controller;
        SearchResult = this;
        //refreshSearchList();
    }
    
    private void initializeCommands() {
        refreshCommand = new Command("Refresh query", Command.ITEM, 2);
        addCommand(refreshCommand);
        selectCommand = new Command("view on Map", Command.ITEM, 1);
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
           
            this.controller.ShowSearchForm();
        }
        else if(command == selectCommand) {
           
         System.out.println( "-------"+ SearchResult.getSelectedIndex());
            //resultPlace = SearchResult.
                    
        String[] chunk =     StringUtil.split(ArrayOfResults[SearchResult.getSelectedIndex()].toString(),"~~");
         //BuddyCanvas.nme = chunk[0].toString();
        this.resultLatitude =chunk[1].toString();
        this.resultLongitude =chunk[2].toString();
        System.out.println("Lat----"+this.resultLatitude);
        System.out.println(this.resultLongitude);

            
            this.controller.showPointinTrail("test",resultLatitude, resultLongitude);
             
        }
    }
    

    
    
   public void refreshSearchList()
    {
        //PointFriend.deleteAll();
            SearchResult.setTitle("Search results for "+searchQuery);
             System.out.println("qry "+searchQuery);
             SearchResult.deleteAll();
                 new Thread(){      public void run() { 
      controller.showProgressBar();
          String [] chunk;
         ArrayOfResults =GetDataFromSite(searchQuery);
         if (ArrayOfResults != null){
           System.out.println("ArrayOfResults "+ArrayOfResults);
           try{
             for (int i=0;i< ArrayOfResults.length-1;i++)
             {
                 chunk =StringUtil.split(ArrayOfResults[i].toString(),"~~");
                 SearchResult.append(chunk[0], null);
               //                         this.append(chunk[0],null);
                 System.out.println("!!!!!!"+chunk[0]);
               //  BuddyLat[i]=chunk[1].toString();// parseLatLongValue(chunk[1], false);
               //  BuddyLng[i] = chunk[2].toString();
                 System.out.println("lat lat"+chunk[1].toString());
                 System.out.println("++"+ chunk[2].toString());
                 System.out.println("-------------------------");
               //   System.out.println("555++"+ ArrayOfResults[i].toString());
                 
             }
             controller.setCurrentScreen(SearchResult);
           }catch( Exception r ){
           System.out.println("In refreshSearchList :"+r);
           }
         }else{
             controller.setCurrentScreen(controller.searchFormScreen);
             new Thread(){public void run(){  
             controller.showAlert("Entered data not found", Alert.FOREVER, AlertType.INFO);}}.start();
             
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
}
