/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mapunity.tracker;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import com.mapunity.tracker.controller.*;
import com.mapunity.tracker.model.RecorderSettings;
import java.io.*;
import javax.microedition.io.*;


/**
 *
 * @author raghul
 */
public class MainMenu extends List implements CommandListener{
   
    private Controller controller;
    private Command exit;
    private Command ok;
    private Command selectCity;
    private MIDlet midi;
    private MainMenu mm;
    private Displayable displa;
    private Form frm;
    private Form BaseForm;
    private Command ok1;
    private Command backtoMenu;
    private String text = "A new version of this application is available would you like to download the app.?";
    private String Ver;
    

    
    public  MainMenu(Controller controll,MIDlet mid)
    {
       
        super("TIS",List.IMPLICIT);
        this.controller = controll;
       
        mm = this;
        midi = mid;
        Ver =  midi.getAppProperty("MIDlet-Version");
         
        exit = new Command("Exit", Command.EXIT, 1);
        ok = new Command("ok", Command.OK, 1);
        selectCity = new Command("Select city",Command.SCREEN,2);
         
        BaseForm = new Form ("New Version Available");
        BaseForm.append(text);
        ok1 = new Command("Yes",Command.OK,1);
        backtoMenu = new Command("No",Command.EXIT,2);
        BaseForm.addCommand(ok1);
        BaseForm.addCommand(backtoMenu);
        BaseForm.setCommandListener(this);
        
//        if (controller.selectedCity == null){
//                    controller.ShowCitySelectionList();      
//        }
       
        
        
        
       
       // Controller.getController().getPointingCanvas("", "12.969802", "77.60879");
      // Controller.getController().getPointingCanvas("", Controller.getController().initLat, Controller.getController().initLon);
        
        RecorderSettings settings = controller.getSettings();
        String ex = settings.getExportFolder();
                if(ex == null)
                {
                   new Thread()
                   {
                    public void run(){
                        frm = new Form("Information");
                        frm.append( "Please select a folder for application to proceed");
                        frm.addCommand(ok);
                        frm.setCommandListener(mm);
                        controller.setCurrentScreen(frm);
                    }
                 }.start(); 
                   
                new Thread(){
                 public void run(){

                // updateDownloadCounter();
                    }
                 
                    }.start();
                    
                }
             /*   else{
             new Thread(){
                 public void run(){
                     updateVersion();
                 }
             }.start();
                        
                }*/
         

    }
    
    private void Initialize(){
                            
        initMenuList();
    }
     public void commandAction(Command command, Displayable disp) {
         displa = disp;

         if(command == List.SELECT_COMMAND){
            
             if(getString(getSelectedIndex()).equals("Directions")){
                 controller.ShowDirectionsFormForFrom();
             }
             if(getString(getSelectedIndex()).equals("Locations")){
                controller.ShowSearchForm();
             }
             if(getString(getSelectedIndex()).equals("Traffic")){
                controller.showTrafficCams();
             }
             if(getString(getSelectedIndex()).equals("Bus routes")){
                controller.showBus();
             }
             if(getString(getSelectedIndex()).equals("Help")){
                controller.showAboutScreen();
             }
             if(getString(getSelectedIndex()).equals("Acknowledgment")){
                controller.showAcknowledeScreen();
             }
             if(getString(getSelectedIndex()).equals("Show Map")){
                System.out.println("In show map");
                 
                if (Runtime.getRuntime().totalMemory() < 614500)
                {
                    
                    controller.disableMaps = true;
                    controller.showAlert("Sorry We are forced to disable drawing Maps due to low memory", 0, AlertType.ERROR);
                }
                        controller.ShowPointingCanvas();
               
               
             }
             
         }
         if(command == exit){
               midi.notifyDestroyed();
   
               controller.exit();
         }
         if(command == ok){
               controller.getDisp().setCurrent(Controller.getController().getFileChooser(displa));
         }
         if(command == ok1){
              getNewVersion();
         }
         if(command == backtoMenu){
              controller.setCurrentScreen(this);
         }
         if(command == selectCity){
             
             controller.ShowCitySelectionList();
         }
         
         
     }
     
      String getVersionData() throws IOException {
         String url = "http://"+controller.URL_BASE+"/mobile/version.txt";
        StreamConnection c = null;
        InputStream s = null;
        StringBuffer b = new StringBuffer();
        TextBox t = null;
        try {
          c = (StreamConnection)Connector.open(url);
          s = c.openInputStream();
          int ch;
          while((ch = s.read()) != -1) {
             b.append((char) ch);
          }
          
          return b.toString().trim();

        } finally {
           if(s != null) {
              s.close();
           }
           if(c != null) {
              c.close();
           }
        }
        
    }
          
          void getNewVersion(){
              String url = "http://www.btis.in/mobile/BTIS.jar";
        StreamConnection c = null;
       
        try {

          midi.platformRequest(url);
          System.out.println("Downloading");
        }catch(Exception e){}
        
          }
          
         public void updateVersion(){
              String version="0.20";
              try{
               version = getVersionData();
              }catch(Exception e){}
              System.out.println(version);
              if(!Ver.equals(version) && !version.equals("0")){
                 System.out.println(Ver);
                 System.out.println("Differenent Version");
                 new Thread(){
                     public void run(){
                         controller.setCurrentScreen(BaseForm);
                     }
                 }.start();
              }
              else{
                  controller.showInfo("You have a updated version of the application.");
              }
          }

         public void updateDownloadCounter(){
          
         String uploadWebsite =  "http://"+controller.URL_BASE+"/php/mobile_app_download_counter.php";
         StreamConnection c = null;
         InputStream s = null;
         
        
         String url = uploadWebsite;
         System.out.print(url);
         try {
          c = (StreamConnection)Connector.open(url);
          s = c.openDataInputStream();
          System.out.println(s.read());
         }catch(Exception e){
             System.out.println("Counter Error"+e);
         }
             
         }
         
    public void initMenuList(){
         System.out.println("in initmenu");
        this.deleteAll();
        
        if (controller.selectedCity.LOCATION == 1){
            append("Locations", null);
        }
        
        if (controller.selectedCity.DIRECTIONS == 1){
        append("Directions",null );
        }
        if(controller.selectedCity.CAMERAS == 0 && controller.selectedCity.HOTSPOTS == 0 && controller.selectedCity.TRAFFIC_FINES == 0){
            
        } else{
            append("Traffic", null);
        }
        if (controller.selectedCity.BUS == 1){
        append("Bus routes", null);
        }
        append("Show Map", null);
        append("Help", null);
        append("Acknowledgment", null);
        addCommand(exit);
        addCommand(selectCity);
        setCommandListener (this);
        Controller.getController().getPointingCanvas("", controller.selectedCity.LATITUDE, controller.selectedCity.LONGITUDE);
        controller.pointresultCanvas.m_listMyPlaces.removeAllElements();
    }
}
