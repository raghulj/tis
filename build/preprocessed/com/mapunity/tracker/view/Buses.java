/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mapunity.tracker.view;

import javax.microedition.lcdui.*;
import com.mapunity.tracker.controller.*;
import org.json.me.*;
import java.io.*;
import javax.microedition.io.*;
import com.mapunity.util.*;
/**
 *
 * @author raghul
 */
public class Buses extends Form implements CommandListener{
        
        private Command viewStages;
        private Command viewBusPosition;
        private Command back;
        private Command refreshStages;
        private Command backToNo;
        private Command backToPlaces;
        private Command showBuses;
        private Command showStages;
        private List Stages;
        private List busesNearMe;
        private Command  MapStages;
        private String [] PostionAddress=null;
        private String [] Location;
        private Form FormData;
        Buses bus;
        String busNos;
        
        Controller controller;
    	 String uploadWebsite ;
         String BusNowebsite;
         String result="";
        private TextField busNo;
        public Buses(Controller controller)
        {
            super("Buses");
            FormData = this;
            this.controller = controller;
            uploadWebsite =  "http://"+controller.selectedCity.URL+"/php/getStagePoints.php?type=route&routeno=";
            BusNowebsite = "http://"+controller.selectedCity.URL+"/php/getStagePoints.php?type=stop&stage=";
         
            back = new Command("back", Command.BACK, 1);
            viewStages = new Command("view stages", Command.OK, 2);
            viewBusPosition = new Command("view bus location", Command.OK, 3);
            busNo = new TextField("","", 16, TextField.ANY);
            this.append("Bus no :");
            append(busNo);
            this.append("Enter the bus number and click \"view stages\". ");
            this.addCommand(viewStages);
            this.addCommand(viewBusPosition);
            this.addCommand(back);
            initialize();
            this.setCommandListener(this);
        }
        
        public void initialize()
        {
           Stages = new List("Stages ",List.IMPLICIT);
           // Stages.append(arg0, append());append()       
             backToNo = new Command("back", Command.BACK, 1);
              MapStages = new Command("view in Map", Command.OK, 3);
                showBuses = new Command("Buses through this stage", Command.OK, 2);
            Stages.addCommand(backToNo);

            Stages.setCommandListener(this);
            
            busesNearMe = new List("Buses ",List.IMPLICIT);
            backToPlaces = new Command("back", Command.BACK, 1);
            showStages =new Command("view stages ", Command.BACK, 1);
            busesNearMe.addCommand(showStages);
            refreshData();
            busesNearMe.addCommand(backToPlaces);       
            busesNearMe.setCommandListener(this);
                    
                    
        }
        
         public void commandAction(Command command, Displayable displayable) {
             
             if(command == back)
             {
                 controller.MainMenu();
             }
             if(command == viewStages)
             {
                 busNos = busNo.getString().toUpperCase();
                fetchStagesData(busNos);
                
             }
            if(command == backToNo)
             {
                
                 controller.getDisp().setCurrent(this);
             }
              if(command == MapStages)
             {
                  controller.pointresultCanvas.busStagesPoints.removeAllElements();
                  for (int mapStages=0;mapStages<PostionAddress.length-1;mapStages++)
                  {
                      String pos = PostionAddress[mapStages];
                        String [] point = StringUtil.split(pos, "||");
                      System.out.println(point[0] + "== "+point[1]);
                        controller.pointBusStages(point[0], point[1]);
                  }
                  
                  String pos = PostionAddress[Stages.getSelectedIndex()];
                        String [] point = StringUtil.split(pos, "||");
                       controller.gotoSpot(point[0], point[1]) ;
                controller.ShowPointingCanvas();
             }
              if(command == backToPlaces)
             {
                
                 controller.getDisp().setCurrent(Stages);
             }
              if(command == showBuses)
             {
                  controller.showProgressBar();
                  fetchBusNoData();
                 
             }
             if(command == showStages)
             {
                 busNos = busesNearMe.getString(busesNearMe.getSelectedIndex()).toUpperCase();
                  fetchStagesData(busNos);
             }
             if(command == viewBusPosition){
              
                 String [] data = GetBusPosition("Tt");
                 String Message = ""+data[0]+"\n"+data[1]+"\n"+data[2];
                 controller.showBusPosition(Message , data[3], data[4]);
             }
    }
         
        
      
      public String  fetchStagesData(String busno)
     {
          controller.showProgressBar();
             Stages.deleteAll();
      
        Stages.setTitle("Stages of "+busno);
        new Thread() { public void run(){
            
            String addloc="";
        
         StreamConnection c = null;
         InputStream s = null;
         StringBuffer b = new StringBuffer();
        
         try {
             System.out.println("The Bus URL is "+uploadWebsite+busNos);
          c = (StreamConnection)Connector.open(uploadWebsite+busNos);
          s = c.openDataInputStream();
          int ch;
          int k =0;
          while((ch = s.read()) != -1) {
               // System.out.print((char) ch);
              b.append((char) ch);   }
          
          result =  b.toString();
          System.out.print(result);
          //String stagesString = fetchStagesData();
                JSONObject ff = new JSONObject(result);
                String data = ff.getString("stagepts");
                JSONArray jsonArray = new JSONArray(data);
                    for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonUser = jsonArray.getJSONObject(i);
                            String lat = jsonUser.getString ("lat");
                            String lon = jsonUser.getString("lon");
                            String stagename = jsonUser.getString("stagename");
                            Stages.append(stagename, null);
                            System.out.println(stagename);
                         //   Location[i] = stagename;
                           addloc = lat+"||"+lon+"~~"+addloc;
                     }
                  if(Stages.size() <1)
                  {
                       
                       
                      //controller.showAlert("Entered Bus number not found", 3, AlertType.ERROR);
                      Stages.append("Entered Bus number not found", null);
                                 
                        Stages.removeCommand(MapStages);
                        Stages.removeCommand(showBuses);
                        
                  //   controller.setCurrentScreen(FormData);
                     
                  }
                  else{
                PostionAddress = StringUtil.split(addloc, "~~");
                 Stages.addCommand(MapStages);
                 Stages.addCommand(showBuses);
               //  Stages.setCommandListener(FormData);
                
                  }
                 controller.getDisp().setCurrent(Stages);
          }catch (Exception e){System.out.print(e);
          
          
       /*   new Thread(){
              public void run(){
                    controller.showAlert("Network Error", 3, AlertType.ERROR);
              }
          }.start();*/
          
             controller.setCurrentScreen(FormData);
           new Thread(){public void run(){  
             controller.showAlert("Error in network connection.", Alert.FOREVER, AlertType.INFO);
             
              }}.start();
        
          
          
          }
        }}.start();
        
      return result;
     }
         
      public String  fetchBusNoData()
      {  
          
          busesNearMe.deleteAll();
                   
        busesNearMe.setTitle("Buses for  "+Stages.getString(Stages.getSelectedIndex()));
        new Thread() { public void run(){
           
             String toF="";
            try{
           
            String [] tt = StringUtil.split(Stages.getString(Stages.getSelectedIndex()), " ");
             System.out.println("From field"+tt[0]);
            for(int y =0;y<tt.length;y++)
            {
                if(y !=tt.length-1)
                {
                toF = toF+""+tt[y]+"%20";
                System.out.println("From field"+toF);
                }
                else{
                    toF = toF+tt[y];
                     System.out.print("in else ");
                }
                 System.out.print("Out of loop");
            }
             System.out.print(BusNowebsite+toF);
            }catch(Exception e){
                controller.showAlert("Network Error", 3, AlertType.ERROR);
             displayList();
            }
             
            String addloc="";
        
         StreamConnection c = null;
         InputStream s = null;
         StringBuffer b = new StringBuffer();
         System.out.print(BusNowebsite+toF);
         try {
          c = (StreamConnection)Connector.open(BusNowebsite+toF);
          s = c.openDataInputStream();
          int ch;
          int k =0;
          while((ch = s.read()) != -1) {
               // System.out.print((char) ch);
              b.append((char) ch);   }
          
          result =  b.toString();
          System.out.print(result);
          //String stagesString = fetchStagesData();
             JSONObject ff1 = new JSONObject(result);
            String data1 = ff1.getString("routes");
            JSONArray jsonArray1 = new JSONArray(data1);

                for (int i = 0; i < jsonArray1.length(); i++) {
                    //   JSONObject jsonUser = jsonArray1.getJSONObject(i);
                    //    String lat = jsonUser.getString ("lat");
                    busesNearMe.append(jsonArray1.optString(i), null);
                        System.out.println(jsonArray1.optString(i));

                }    
               
              controller.getDisp().setCurrent(busesNearMe); 
          }catch (Exception e){
              System.out.print("Error "+e);
           //   controller.getDisp().setCurrent(Stages);
          // controller.showAlert("Network Error", 3, AlertType.ERROR);
          //
        //  controller.getDisp().setCurrent(controller.showAlert("Network Error", 3, AlertType.ERROR), Stages);
          
          }
        }}.start();
        controller.getDisp().setCurrent(Stages);
      return result;
      }

      public void displayList()
      {
           controller.getDisp().setCurrent(this);
      }
      
         private String [] GetBusPosition(String Str)
    {
         String uploadWebsite =  "http://"+controller.selectedCity.URL+"/php/busposition?q=";
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

          if(!result.equals("")){
             
             ArrayOfData =StringUtil.split(result.toString().trim(),"~~");
          }
          }catch (Exception e){
              System.out.print(e);
              controller.ShowPointingCanvas();
              controller.showAlert("Network Error", 3, AlertType.ERROR);
          }
        
        return ArrayOfData;
        
        
    }
         
    public void refreshData(){
        System.out.println("IN refresh data");
        if(controller.selectedCity.BUS_POSITION == 1){
        System.out.println("pblm in ");    
            busesNearMe.addCommand(viewBusPosition);
        }else{
            busesNearMe.removeCommand(viewBusPosition);
        }
                    
    }
    
}
