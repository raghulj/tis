package com.mapunity.tracker.view;

import com.mapunity.tracker.controller.Controller;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import java.util.*;

/**
 * 
 * @author Tommi
 * @author Barry Redmond
 */
public abstract class BaseCanvas extends Canvas implements CommandListener, LocationListener {

    /** The color White, in it's integer value form. */
    protected static final int COLOR_WHITE = 0xFFFFFF;
    /** The color Black, in it's integer value form. */
    protected static final int COLOR_BLACK = 0x0;
    /** The color Black, in it's integer value form. */
    protected static final int COLOR_RED = 0xFF0000;
    /** The color Black, in it's integer value form. */
    protected static final int COLOR_GREEN = 0x00FF00;
    /** The color Black, in it's integer value form. */
    protected static final int COLOR_BLUE = 0x0000FF;

    /** The color all BaseCanvas subclass titles should be. */
    protected static final int COLOR_TITLE = 0x008000;
    /** The Font all CbasCanvas subclass titles should be. */
    protected static final Font titleFont = Font.getFont(Font.FACE_SYSTEM,
            Font.STYLE_BOLD, Font.SIZE_SMALL);

    protected Controller controller;

    /** Commands */
    private Command startStopCommand;
    private Command settingsCommand;
    private Command exitCommand;
    public Command ShowCurrentPoint;
    private Command manageTrailsCommand;
    private Command manageWaypointsCommand;
    private Command Traffic_Cams;
    private Command Bus;
     public Command clearMap;
    
    private Command searchCommand;
    private Command directionsCommand;
    private Command AboutCommand;
    private Command ACKCommand;

    /*
     * private Command markWaypointCommand; private Command
     * editWaypointsCommand;
     */

    /** Creates a new instance of BaseCanvas */
    public BaseCanvas() {
        this.controller = Controller.getController();
        this.setFullScreenMode(true);
        initializeCommands();
        setCommandListener(this);
    }

    /* Initialize commands */
    private void initializeCommands() {


        // Start/Stop command for toggling recording
       startStopCommand = new Command("Start/Stop GPS", Command.ITEM, 1);
       addCommand(startStopCommand);
       
       ShowCurrentPoint = new Command("Show Current Position",Command.ITEM,2);
       
        
           // directions command
     //  directionsCommand = new Command("Directions", Command.ITEM, 1);
    //   addCommand(directionsCommand);
       
        // Search command
    //    searchCommand = new Command("Search ", Command.ITEM, 4);
     //   addCommand(searchCommand);
        
      
        
      // Traffic_Cams =  new Command("Traffic Cams", Command.ITEM, 2);
     //  addCommand(Traffic_Cams);
       
       // Bus =  new Command("Bus", Command.ITEM, 3);
      // addCommand(Bus);
       
        // Waypoints command
      //  manageWaypointsCommand = new Command("Manage Waypoints", Command.ITEM, 4);
     //   addCommand(manageWaypointsCommand);

        // Trails command
      ////  manageTrailsCommand = new Command("Manage Trails", Command.ITEM, 5);
     //   addCommand(manageTrailsCommand);

        // Settings command for showing settings list
       settingsCommand = new Command("Settings", Command.SCREEN, 3);
      addCommand(settingsCommand);

       clearMap = new Command("clear map ", Command.ITEM, 5);
       //addCommand(clearMap);
        
        // about command
     //   AboutCommand = new Command("User guide ", Command.ITEM, 6);
     //   addCommand(AboutCommand);
        // about command
    //    ACKCommand = new Command("Acknowledgement ", Command.ITEM, 7);
      //  addCommand(ACKCommand);
    //    
        //Exit
        exitCommand = new Command("back", Command.OK, 4);
        addCommand(exitCommand);

    }

    /** 
     * Handle commands.
     * @param command       Activated command.
     * @param displayable   Displayable object.
     */
    public void commandAction(Command command, Displayable displayable) {
        if (displayable == this) {
            if (command == startStopCommand) {
                controller.startStop();
            } else if (command == manageTrailsCommand) {
                controller.showTrailsList();
            } else if (command == manageWaypointsCommand) {
               // controller.showWaypointList();
            }
            if (command == settingsCommand) {
                controller.showSettings();
            }
            if (command == searchCommand) {
                System.out.print("In search command");
                controller.ShowSearchForm();
            }
            if (command == directionsCommand) {
                System.out.print("In Directions");
                controller.ShowDirectionsFormForFrom();
            }
            if (command == exitCommand) {
               // controller.exit();
                controller.MainMenu();
            }
            if (command == AboutCommand) {
                System.out.print("In about");
                controller.showAboutScreen();
            }
             if (command == ACKCommand) {
                System.out.print("In ack");
                controller.showAcknowledeScreen();
            }
            
            if (command == Traffic_Cams) {
               
                controller.showTrafficCams();
            }
             if (command == Bus) {
               
                controller.showBus();
            }
            if(command == clearMap)
            {
                try{
               
                    
                    controller.pointresultCanvas.m_listMyPlaces.removeAllElements();
                    controller.pointresultCanvas.busStagesPoints.removeAllElements();
                    controller.pointresultCanvas.mapIdName.removeAllElements();
                    controller.route= null;
                     controller.pointresultCanvas.TrafficPoints.removeAllElements();
                    // controller.route = new Vector();
                   //  controller.pointresultCanvas.busStagesPoints =  new Vector();
                   //  controller.pointresultCanvas.m_listMyPlaces = new Vector();
            
                }catch(Exception e){
                System.out.println("Error in removing "+ e);
                }
               
            }
            if(command == this.ShowCurrentPoint){
                if(controller.getUseJsr179()){
                    controller.gotoSpot(String.valueOf(controller.pointresultCanvas.GPSLongitude),String.valueOf(controller.pointresultCanvas.GPSLatitude));
                }else{
                controller.gotoSpot(String.valueOf(controller.pointresultCanvas.GPSLatitude), String.valueOf(controller.pointresultCanvas.GPSLongitude));    
                }
                
            }
        }
    }
    
    /** 
     * Key pressed handler
     * @param keyCode 
     */
  /*  protected void keyPressed(int keyCode) {
        /** Handle 0 key press. In some phones the 0 key defaults to space */
        /*if(keyCode==Canvas.KEY_NUM0 || keyCode==' ') {
            controller.switchDisplay();
        } 
    }*/
}
