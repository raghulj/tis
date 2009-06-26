/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mapunity.tracker.view;

import com.mapunity.net.Downloader;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;
import java.io.*;
import org.json.me.*;
import java.util.*;

import com.mapunity.util.*;
import com.mapunity.tracker.controller.*;

/**
 *
 * @author raghul
 */
public class TrafficCams extends List implements CommandListener{
   
     private Controller controller;
   
     public String [] ArrayOfResults;
     private Form fmViewPng;
     private Command cmBack;
     private Command bckMenu;
     private Command TrafficStat;
     private Command TrafOption;
     private Command RefreshImage;


     private List LiveCameras;
     private Form TrafficStatus;


    private Command viewImage;
    private Command camList;
    private Command refreshList;
    private Command Vehicleback;
    private Command VehicleOk;
    private TrafficCams traf;
    TextField vehNo;
    Form VehicleNoForm;
    
    public TrafficCams(Controller controller)
    {
        super("Traffic Status",Choice.EXCLUSIVE);
       
        this.controller = controller;
        traf =this;

        TrafOption = new Command("OK", Command.SCREEN, 1);
        bckMenu = new Command("back",Command.BACK,2);
        
       VehicleOk = new Command("OK", Command.SCREEN, 1);
       Vehicleback = new Command("Back",Command.BACK, 2);
                  
                  
        this.addCommand(TrafOption);
        this.addCommand(bckMenu);
        setCommandListener(this);
            
         if(fmViewPng == null){
          fmViewPng = new Form("");
          cmBack = new Command("Back", Command.BACK, 1);
          RefreshImage = new Command("refresh", Command.OK, 2);
          fmViewPng.addCommand(cmBack);
          fmViewPng.addCommand(RefreshImage);
          fmViewPng.setCommandListener(this);
         }
           
   
         
     
       
    }
    
    private void LiveCameras(){
        LiveCameras = new List ("Live Cameras",Choice.IMPLICIT);
        viewImage = new Command("view", Command.OK, 1);     
        camList =  new Command("back", Command.BACK, 2); 
        refreshList = new Command("refresh List", Command.OK, 0); 
        
        LiveCameras.addCommand(viewImage);
        LiveCameras.addCommand(camList);
        LiveCameras.addCommand(refreshList);
        LiveCameras.setCommandListener(this);
        
        new Thread(){public void run(){
         controller.showProgressBar(); }}.start();
          refreshCamList();
        
        
    }


   
     public void commandAction(Command command, Displayable displayable)
     {
         if(command == camList)
         {
           controller.setCurrentScreen(this);
           controller.setCurrentScreen(this);
         }
         if(command ==refreshList)
         {
              controller.showProgressBar();
              refreshCamList();
         }
         if(command ==cmBack)
         {
              controller.getDisp().setCurrent(LiveCameras);
         }
         if(command ==RefreshImage)
         {
              refreshImage();
         }
         if(command ==viewImage)
         {
                  
          refreshImage();
         }
         if(command == TrafOption){
             if(this.getString(this.getSelectedIndex()).equals("Cameras")){
                 LiveCameras();
                 
             }
             else if(this.getString(this.getSelectedIndex()).equals("Traffic Fines")){
                 
                 if(VehicleNoForm == null){
                     
                  VehicleNoForm = new Form("Traffic Fines");
                  vehNo = new TextField("Vehicle Number", "", 100, TextField.ANY);

                 
                  VehicleNoForm.append(vehNo);
                  VehicleNoForm.addCommand(Vehicleback);
                  VehicleNoForm.addCommand(VehicleOk);
                 
                 VehicleNoForm.setCommandListener(this);
                 }
                 
                 controller.setCurrentScreen(VehicleNoForm);
                 
                 
             }
             else{
                 new Thread(){
                     public void run(){
                         getTrafficSpots();
                     }
                 }.start();
                 
             }
         }
         if(command == bckMenu){
             controller.MainMenu();
         }
         
         if(command == Vehicleback){
              controller.setCurrentScreen(this);
         }
         if(command == VehicleOk){
             if(vehNo.getString().equals("")){
                 
                 controller.showAlert("Enter the vehicle number", 0, AlertType.ERROR);
             }else{
                 
                  controller.showProgressBar();
                 new Thread(){
                    public void run(){
                             
                             Downloader dwn = new Downloader(controller);
                             String message = dwn.requestForData("http://125.17.140.50/notices/vehiclefinedetails.aspx?veh_no="+vehNo.getString());
                             vehNo.setString("");
                             controller.showAlert(message, 0, AlertType.INFO);
                             
                        }
                   }.start();
                   
                   new Thread(){
                    public void run(){
                 
                  controller.setCurrentScreen(VehicleNoForm);
                    }
                 }.start();
                   
             }
             

             
           
         }
         
     }
     
          private String [] GetDataFromSite()
    {
          
         String uploadWebsite =  "http://"+controller.selectedCity.URL+"/cameras/mobile_cam_list.php";
         String [] ArrayOfData=null;
         StreamConnection c = null;
         InputStream s = null;
         StringBuffer b = new StringBuffer();
        
         String url = uploadWebsite;
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
             if( ArrayOfData.length ==0)
         {
              controller.MainMenu();
          new Thread(){public void run(){
              controller.showAlert("Network Error", 0, AlertType.ERROR);
          }}.start();
         }
          }
          }catch (Exception e){
              System.out.print(e);
              new Thread(){
               public void run(){
                controller.showProgressBar();
              }
           }.start();
          //controller.getDisp().setCurrent(this);
             
          }
        
        return ArrayOfData;
        
        
    }
       
        public void refreshCamList()
    {
           
       
         
           LiveCameras.deleteAll();
                 new Thread(){      public void run() { 
      controller.showProgressBar();
          String [] chunk;
         ArrayOfResults =GetDataFromSite();
         
           System.out.println("ArrayOfResults "+ArrayOfResults);
           try{
             for (int i=0;i< ArrayOfResults.length-1;i++){
                 chunk =StringUtil.split(ArrayOfResults[i].toString(),"~");
               LiveCameras.append(chunk[1], null);
              
                 
             }
          controller.getDisp().setCurrent(LiveCameras);
            
           }catch( Exception r ){
           System.out.println("In refreshSearchList :"+r);
            
        //  new Thread(){public void run(){
        //      controller.setCurrentScreen(NetworkErrorAlert);
       //   }}.start();
          controller.setCurrentScreen(traf);
           new Thread(){public void run(){  
             controller.showAlert("No cameras available at this moment.", Alert.FOREVER, AlertType.INFO);
             
              }}.start();
          
         /*  
           //alert.setString("Network Error");*/
           
           
           }
        
        }
          
         
         }.start();
           
    }
        
 private void refreshImage()
     {
         controller.showProgressBar();
            new Thread(){
                  
               public void run()
               {
           
                         if (fmViewPng.size() > 0)
        for (int i = 0; i < fmViewPng.size(); i++)
          fmViewPng.delete(i);

      // Download image and set as the first (only) item on the form
      Image im;

      try
      {
          String [] chunk;
 
          String tt = ArrayOfResults[LiveCameras.getSelectedIndex()];

          chunk =StringUtil.split(tt,"~");
          String imagename = chunk[0].toString();

        if ((im = getImage(imagename)) != null)
        {
          ImageItem ii = new ImageItem(null, im, ImageItem.LAYOUT_DEFAULT, null);
         // If there is already an image, set (replace) it
          if (fmViewPng.size() != 0)
            fmViewPng.set(0, ii);
          else  // Append the image to the empty form
            fmViewPng.append(ii);
        }
        else
          fmViewPng.append("Unsuccessful download.");
  
        // Display the form with the image
      controller.getDisp().setCurrent(fmViewPng);
        
      }
      catch (Exception e)
      { 
        System.err.println("Msg: " + e.toString());
        //alert.setString(e.toString());
         controller.showAlert("Network Error", 0, AlertType.ERROR);
       // controller.getDisp().setCurrent(ListCams);
        //switchDisplayable(getAlert(), getCamList());
      }
               }
               }.start();
                                           
         }
     
     
  private Image getImage(String str) throws IOException
  {
      String url = "http://"+controller.selectedCity.URL+"/cameras/images/"+str+".jpg";
      System.out.println(url);
    InputStream iStrm = (InputStream) Connector.openInputStream(url);
    Image im = null;

    try
    {
      ByteArrayOutputStream bStrm = new ByteArrayOutputStream();
        
      int ch;
      while ((ch = iStrm.read()) != -1)
        bStrm.write(ch);
 
      // Place into image array
      byte imageData[] = bStrm.toByteArray();      
      
      // Create the image from the byte array
      im = Image.createImage(imageData, 0, imageData.length);  
      
    }
    finally
    {
      // Clean up
      if (iStrm != null)
        iStrm.close();
    }

    return (im == null ? null : im);

  }
  
 private void getTrafficSpots(){
     controller.showProgressBar();
      String uploadWebsite =  "http://"+controller.selectedCity.URL+"/php/trafficstatus.cache?dummy=ert43";
         String [] ArrayOfData=null;
         StreamConnection c = null;
         InputStream s = null;
         StringBuffer b = new StringBuffer();
        
         String url = uploadWebsite;
        System.out.print(url);
         try {
          c = (StreamConnection)Connector.open(url);
          s = c.openDataInputStream();
          int ch;
          int k =0;
          while((ch = s.read()) != -1) {
                System.out.print((char) ch);
              b.append((char) ch);   }
         // System.out.println("b"+b);
          try{
          JSONObject ff1 = new JSONObject(b.toString());
          String data1 = ff1.getString("locations");
          JSONArray jsonArray1 = new JSONArray(data1);
          Vector TrafficStatus = new Vector();
           for (int i = 0; i < jsonArray1.length(); i++) {
                      
                         System.out.println(jsonArray1.getJSONArray(i).getString(3));
                        double aDoubleLat = Double.parseDouble(jsonArray1.getJSONArray(i).getString(1));
                        double aDoubleLon = Double.parseDouble(jsonArray1.getJSONArray(i).getString(2));
                        System.out.println(aDoubleLat+" "+aDoubleLon);
                       TrafficStatus.addElement(new LocationPointer(jsonArray1.getJSONArray(i).getString(3), (float)aDoubleLon,(float)aDoubleLat, 1, true));
                        
                }    
         controller.setCurrentScreen(controller.TrafficSpots(TrafficStatus));
          }catch (Exception E){
               controller.setCurrentScreen(traf);
           new Thread(){public void run(){  
             controller.showAlert("Error in network connection.", Alert.FOREVER, AlertType.INFO);
             
              }}.start();
          }
          
         }
         catch(Exception e){
         
          controller.setCurrentScreen(traf);
           new Thread(){public void run(){  
             controller.showAlert("Error in network connection.", Alert.FOREVER, AlertType.INFO);
             
              }}.start();
         }
 }
 
 public void initList(){
        this.deleteAll();
        if(controller.selectedCity.CAMERAS ==1){
        this.append("Cameras",null);
        }
        if(controller.selectedCity.HOTSPOTS == 1){
        this.append("Hotspots",null);
      
        }
        if(controller.selectedCity.TRAFFIC_FINES == 1){
            this.append("Traffic Fines",null);
        }
        
 }
     
}
