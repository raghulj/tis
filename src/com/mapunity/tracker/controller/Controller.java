/*
 * Controller.java
 *
 * Copyright (C) 2005-2008 Tommi Laukkanen
 * http://www.substanceofcode.com
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

package com.mapunity.tracker.controller;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Gauge;
import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Form;
import java.util.Timer;
import java.util.TimerTask;

import com.mapunity.bluetooth.BluetoothDevice;
import com.mapunity.bluetooth.BluetoothUtility;
import com.mapunity.bluetooth.Device;
import com.mapunity.data.FileIOException;
import com.mapunity.data.FileSystem;
import com.mapunity.gps.GpsPosition;
import com.mapunity.gpsdevice.GpsDevice;
import com.mapunity.gpsdevice.GpsDeviceFactory;
import com.mapunity.gpsdevice.GpsUtilities;
import com.mapunity.gpsdevice.Jsr179Device;
import com.mapunity.gpsdevice.MockGpsDevice;
import com.mapunity.net.Downloader;
import com.mapunity.tracker.model.AlertHandler;
import com.mapunity.tracker.model.AudioShortcutAction;
import com.mapunity.tracker.model.Backlight;
import com.mapunity.tracker.model.GpsRecorder;
import com.mapunity.tracker.model.RecorderSettings;
import com.mapunity.tracker.model.Track;
import com.mapunity.tracker.model.Place;
import com.mapunity.tracker.model.ShortcutAction;



import com.mapunity.util.*;
import com.mapunity.tracker.view.*;
import java.util.Enumeration;
import com.mapunity.tracker.*;
import com.mapunity.tracker.model.CityData;
import java.util.Hashtable;

/**
 * Controller contains methods for the application flow.
 * 
 * @author Tommi Laukkanen
 * @author Mario Sansone
 * @author Raghul J
 */
public class Controller {

    /**
     * Static reference to the last instanciation of this class XXX : mchr :
     * perhaps this class should be a proper singleton pattern?
     */
    private static Controller controller;
    /** Status codes */
    public final static int STATUS_STOPPED = 0;
    public final static int STATUS_RECORDING = 1;
    public final static int STATUS_NOTCONNECTED = 2;
    public final static int STATUS_CONNECTING = 3;

    public boolean disableMaps=false;

    public String FromField;
    public String ToField;

    public String URL_BASE = "btis.in";
    /**
     * Vector of devices found during a bluetooth search
     */
    public Vector mapIdNames;
    public Vector route;
    private Vector devices;
    public Vector TrafficStatus;
    public int globalZoom = 6;
        
    public String mapCity;
    public CityData selectedCity;
        
    public String InfoDisplay;
        
    public LocationPointer [] routesData;
    
    public int initScreen;
    /**
     * Current status value
     */
    private int status;
    /**
     * GPS device being used
     */
    private Device gpsDevice;
    /**
     * GpsRecorder which will do the actual logging
     */
    private GpsRecorder recorder;
    /**
     * Current places in use XXX : mchr : shouldn't this be in the model?
     */
    private Vector places;
    /**
     * Settings object
     */
    private RecorderSettings settings;
    /**
     * Backlight maintenance object
     */
    private Backlight backlight;
    /**
     * Ghost Track
     */
    private Track ghostTrail;
    public String[] DirectionsArray;
    public static String initLat;
    public static String initLon;
    public String mapIdName;

    // ----------------------------------------------------------------------------
    // Screens and Forms
    // ----------------------------------------------------------------------------
    private MIDlet midlet;
   // private TrailCanvas trailCanvas;
    private ElevationCanvas elevationCanvas;
    private DeviceList deviceList;
    private AboutScreen aboutScreen;
    private SettingsList settingsList;
    private RecordingSettingsForm recordingSettingsForm;
    private ExportSettingsForm exportSettingsForm;
    private FileChooser filechooser;
    private DisplaySettingsForm displaySettingsForm;
    private PlaceForm placeForm;
    private PlaceList placesList;
    private TrailsList trailsList;
    private DevelopmentMenu developmentMenu;
    private TrailActionsForm trailActionsForm;
    private SmsScreen smsScreen;
    private ImportTrailScreen importTrailScreen;
    public MainMenu mainmenu;
    private DirectionSuggestFrom directionSuggestFrom;
    private DirectionSuggestTo directionSuggestTo;

    public Locations searchFormScreen;
    public DirectionsFormField directionsFormField;
    public DirectionsToField directionsToField;

    private searchResults searchResutls;
    public PointResultCanvas pointresultCanvas;
    private LocatePoint locatePoint;

    private Acknowledgement ackScreen;
    private TrafficCams trafficCams;
    private Buses buses;
    private CitySelection citySelection;
    /**
     * Display which we are drawing to
     */
    private Display display;
   
    /**
     * Array of defined screens XXX : mchr : It would be nice to instantiate the
     * contents here but there are dependancies in the Constructor
     */
    private BaseCanvas[] screens;
    /**
     * Index into mScreens of currently active screen
     */
    private int currentDisplayIndex = 0;
    /**
     * XXX : mchr : What error does this hold?
     */
    private String error;
    
      
    public Alert NetworkErrorAlert;
    private Timer tm;               // The Timer
    private DownloadTimer tt;       // The task to run
    /**
     *  Controls whether jsr179 is used or not
     */
    private boolean useJsr179 = false;
    /**
     *  Controls whether FileCache is used or not
     */
    private boolean useFileCache = false;
    
        //// Progress Ba
    
    public Gauge ProgressBar;
    public Form ProgressForm;
    private int delta = 1;
 
    /**
     *  Enable navigation
     */
    private boolean navigationOn = false;
    /**
     * Navigation Place
     */
    private Place navpnt;
    
    
    public String cityListURL = "http://127.0.0.1/web/citydata.txt";
    /**
     * The features of the application
     */
    
    public int LOCATION = 0;
    public int MAPID = 1;
    public int DIRECTIONS = 2;
    public int BUS = 3;
    public int CAMERAS = 4;
    public int HOTSPOTS = 5;
    public int TRAFFIC_FINES = 6;
    public int BUS_POSITION = 7;
    
    /**
     * The information of the features avaialble for each cities
     */
    public Hashtable cityHash;
    
           

    
    
    /** 
     * City name strings
     */
    public String BLR = "Bangalore";
    public String HYD = "Hyderabad";
    public String CHE = "Chennai";
    public String DL  = "Delhi" ;
    public String PUN = "Pune";
    public String IND = "Indoor";
    public String MYS = "Mysore";
    
    

    /**
     * Creates a new instance of Controller which performs the following:
     * <ul>
     * <li> Status = NOT_CONNECTED
     * <li> Constructs a GpsRecorder
     * <li> Constucts a GPS Device
     * <li> Load any existing places
     * <li> Apply backlight settings
     * </ul>
     * @param midlet
     * @param display 
     */
    public Controller(MIDlet midlet, Display display) {
        
        locatePoint = new LocatePoint();
        Controller.controller = this;
        this.midlet = midlet;
        this.display = display;
        status = STATUS_NOTCONNECTED;
        settings = new RecorderSettings(midlet);

        // Do mandatory initializations
        
        mapCity = settings.getCity();
        cityHash = new Hashtable();
        
        System.out.println("Default city is "+mapCity);
        if(!mapCity.equals("")){
            
            loadCityMatrix();
            
        }else{
             controller.ShowCitySelectionList();      
        }
        
        // Initialize Logger, as it must have an instance of RecorderSettings on
        // it's first call.
        Logger.init(settings);
        
        ProgressBar = new Gauge("Download in Progress",false,20,1);
        ProgressForm = new Form("");
        ProgressForm.append("");
        ProgressForm.append("");
        ProgressForm.append("");
        ProgressForm.append("");
        ProgressForm.append(ProgressBar);
          
        tm = new Timer();
        tt = new DownloadTimer();
        tm.scheduleAtFixedRate(tt, 0, 200);    
          
        NetworkErrorAlert = new Alert("Network Error","Error in retreving data",null,null);
        NetworkErrorAlert.setTimeout(Alert.FOREVER);  

    }

    public void executeStarShortcut() {
        // TODO: Get shortcut from settings
        ShortcutAction action = new AudioShortcutAction();
        action.execute();        
    }

    public void initialize() {

        // logger = Logger.getLogger(settings);
        // XXX : mchr : Dependency from Logger to getTrailCanvas prevents this
        // array definition from being any higher - we have to tell the Logger
        // class about the RecorderSettings which in turn depend on midlet
        screens = new BaseCanvas[]{ getElevationCanvas(),
                    new InformationCanvas(), new PlacesCanvas(),
                    new SatelliteCanvas(), new SkyCanvas()
                };
        String gpsAddress = settings.getGpsDeviceConnectionString();

        recorder = new GpsRecorder(this);
        if (gpsAddress.length() > 0) {

            try {
                gpsDevice = GpsDeviceFactory.createDevice(gpsAddress, "GPS");
            } catch (java.lang.SecurityException se) {
                Logger.warn("GpsDevice could not be created because permission was not granted.");
            }

        } else {
            // XXX : mchr : what is going on here?
            // Causes exception since getcurrentScreen returns null at this
            // point in time.
            // showError("Please choose a bluetooth device from Settings->GPS");
        }

        /** Places */
        places = settings.getPlaces();
        if (places == null) {
            places = new Vector();
        }

        /** Backlight class is used to keep backlight always on */
        if (backlight == null) {
            backlight = new Backlight(midlet);
        }
        if (settings.getBacklightOn()) {
            backlight.backlightOn();
        }
        useJsr179 = settings.getJsr179();
    }

    /**
     * XXX : mchr : This may not be a sensible exposure but is currently needed
     * for the AlertHandler class.
     * 
     * @return
     */
    public MIDlet getMIDlet() {
        return midlet;
    }

    /**
     * @return Last instantiation of this class XXX : mchr : Should this be
     *         changed to proper singleton pattern?
     */
    public static Controller getController() {
        return Controller.controller;
    }

    /**
     * Tells this Controller if the Backlight class should keep the backlight on
     * or switch to phone's default behaviour
     * 
     * @param xiBacklightOn
     *                <ul>
     *                <li>true = keep backlight always on
     *                <li>false = switch to phone's default backlight behaviour
     *                </ul>
     */
    public void backlightOn(boolean backlightOn) {
        if (backlightOn) {
            backlight.backlightOn();
        } else {
            backlight.backlightOff();
        }
    }

    public void repaintDisplay() {
        Displayable disp = display.getCurrent();
        if (disp instanceof BaseCanvas) {
            BaseCanvas canvas = (BaseCanvas) disp;
            canvas.repaint();
        }
    }

    public void searchDevices() {
        // Jsr179 will find external bluetooth devices if a suitable internal
        // one can't be found
        // So really we only want to do one of these searches, ie use the
        // location api if
        // it is present, search bluetooth devices if it isn't
        if (useJsr179 && GpsUtilities.checkJsr179IsPresent()) {
            Logger.debug("Using JSR179 for Location services");
            searchDevicesByJsr();
        } else {
            Logger.debug("Using bluetooth for Location services");
            searchBTDevices();
        }
    }

    /**
     * See if there are any supported JSRs that provide a location api ie Jsr179
     */
    public void searchDevicesByJsr() {

        if (devices == null) {
            devices = new Vector();
        } else {
            devices.removeAllElements();
        }


        if (GpsUtilities.checkJsr179IsPresent()) {
            Device dev = Jsr179Device.getDevice("internal",
                    "Internal GPS (Jsr 179)");
            devices.addElement(dev);
        }
    }

    /**
     * Search for all available bluetooth devices
     */
    public void searchBTDevices() {
        try {
            BluetoothUtility bt = new BluetoothUtility();
            Logger.debug("Initializing bluetooth utility");
            bt.initialize();
            int countDown = BluetoothUtility.SearchTimeoutLimitSecs;
            Logger.debug("Finding devices." + countDown);
            bt.findDevices();
            // TODO : mchr : Add explicit timeout to avoid infinite loop?
            // yes

            while (!bt.searchComplete() && !bt.searchTimeOutExceeded()) {
                // Logger.debug("Finding devices.");
                Thread.sleep(100);


            }
            System.out.println("Getting devices.");
            //addDevices(devices, bt.getDevices());
            devices = bt.getDevices();
        } catch (Exception ex) {
            System.err.println("Error in Controller.searchDevices: " + ex.toString());
            ex.printStackTrace();
        }
    }

    public void showWebRecordingSettings() {
        display.setCurrent(new WebRecordingSettingsForm(this));
    }

    /**
     * Utility method to add the contents of one vector to another
     * 
     * @param dest
     *                the Vector into which the new contents are added
     * @param src
     *                the Vector containing the contents to be added to dest
     *                returns Vector A vector containing the sum of src and dest
     */
    private Vector addDevices(Vector dest, Vector src) {

        if (dest == null) {
            dest = new Vector();
            Logger.debug("dest was null, creating.");
        }

        int endIdx = dest.size() - 1;
        endIdx = (endIdx < 0) ? 0 : endIdx;

        for (int i = 0; i < src.size(); i++) {
            dest.insertElementAt(src.elementAt(i), endIdx + i);
        }

        return dest;
    }

    /**
     * Return list of bluetooth devices discovered during a search
     */
    public Vector getDevices() {
        return devices;
    }

    /** Set GPS device */
    public void setGpsDevice(String address, String alias) {

        gpsDevice = GpsDeviceFactory.createDevice(address, alias);
        settings.setGpsDeviceConnectionString(gpsDevice.getAddress());
    }

    /** Set Mock GPS device */
    public void setMockGpsDevice(String address, String alias) {
        gpsDevice = new MockGpsDevice(address, alias);
        settings.setGpsDeviceConnectionString(gpsDevice.getAddress());
    }

    /** Get status code */
    public int getStatusCode() {
        return status;
    }

    /**
     * @param err
     *                TODO : mchr : Set an error - I don't know what errors are
     *                expected
     */
    public void setError(String err) {
        error = err;
    }

    /**
     * @return TODO : mchr : Set an error - I don't know what errors are
     *         expected
     */
    public String getError() {
        return error;
    }

    /** Get current status text */
    public String getStatusText() {
        String statusText = "";
        switch (status) {
            case STATUS_STOPPED:
                statusText = "STOPPED";
                break;
            case STATUS_RECORDING:
                statusText = "RECORDING";
                break;
            case STATUS_NOTCONNECTED:
                statusText = "NOT CONNECTED";
                break;
            case STATUS_CONNECTING:
                statusText = "CONNECTING";
                break;
            default:
                statusText = "UNKNOWN";
        }
        return statusText;
    }

    /** Connect to a GPS device */
    public void connectToGpsDevice() {
        if(gpsDevice==null) {
            return;
        }
        new Thread() {
            public void run() {
                try {
                    if (gpsDevice instanceof BluetoothDevice) {
                        ((BluetoothDevice) gpsDevice).connect();
                        status = STATUS_STOPPED;
                    }
                } catch (Exception ex) {
                    Logger.error("Error while connection to GPS: " + ex.toString());
                    showError("Error while connection to GPS: " + ex.toString());
                }
            }
        }.start();
    }

    /** Method for starting and stopping the recording */
    public void startStop() {
        // --------------------------------------------------------------------------
        // Start Recording
        // --------------------------------------------------------------------------
        if (status != STATUS_RECORDING) {
            Logger.info("Starting Recording");
            // XXX : HACK(disabled)
            Logger.debug("gpsDevice is " + gpsDevice);
            if (gpsDevice == null) {
                showError("Please select a GPS device first");
            } else {
                if( status==STATUS_NOTCONNECTED ) {
                    connectToGpsDevice();
                }
                recorder.startRecording();
                status = STATUS_RECORDING;
            }
        } // --------------------------------------------------------------------------
        // Stop Recording
        // --------------------------------------------------------------------------
        else {
            Logger.info("Stopping Recording");
            // Stop recording the track
            recorder.stopRecording();
            status = STATUS_STOPPED;
            // Disconnect from GPS device
            //this.disconnect();
            // Show trail actions screen
            // XXX : HACK(disabled)
            // Track lTest = new Track();
            // lTest.addPosition(new GpsPosition((short)0,0,0,0,0,new Date()));
            // recorder.setTrack(lTest);
          //  if (trailActionsForm == null) {
         //       trailActionsForm = new TrailActionsForm(this);
         //   }
          //  display.setCurrent(trailActionsForm);
        }
    }

    /**
     * Disconnect from the GPS device. This will change our state ->
     * STATUS_NOTCONNECTED
     */
    private void disconnect() {
        // First, we have to set the status to "STOPPED", because otherwise
        // the GpsDevice thread tries to reconnect when gpsDevice.disconnect()
        // is called
        status = STATUS_NOTCONNECTED;
        try {
            // Disconnect from bluetooth GPS
            if (gpsDevice instanceof BluetoothDevice) {
                ((BluetoothDevice) gpsDevice).disconnect();
            }
        } catch (Exception e) {
            showError("Error while disconnecting from GPS device: " + e.toString());
        }
    }

    /** 
     * Get waypoints.
     * @return Get waypoints.
     */
    public Vector getPlaces() {
        return places;
    }

    /** Save new waypoint
     * @param waypoint Place to be saved.
     */
    public void savePlace(Place waypoint) {
        if (places == null) {
            places = new Vector();
        }
        places.addElement(waypoint);

        savePlaces(); // Save waypoints immediately to RMS

    }

    /**
     * Save the current trail
     * 
     * @param xiListener
     *                TODO
     * @param name 
     */
    public void saveTrail(AlertHandler xiListener, String name) {
        // XXX : mchr : Vulnerable to NPE...
        xiListener.notifyProgressStart("Saving Trail to RMS");
        xiListener.notifyProgress(2);
        try {
            Track track = recorder.getTrack();
            track.setName(name);
            track.saveToRMS();
            if (xiListener != null) {
                xiListener.notifySuccess("RMS : Save succeeded");
            }
        } catch (IllegalStateException e) {
            if (xiListener != null) {
                xiListener.notifyError(
                        "RMS : Can not save \"Empty\" Trail. must record at " + "least 1 point", null);
            }
        } catch (FileIOException e) {
            if (xiListener != null) {
                xiListener.notifyError(
                        "RMS : An Exception was thrown when attempting to save " + "the Trail to the RMS!", e);
            }
        }
    }

    /** 
     * Mark new waypoint
     * @param lat
     * @param lon 
     */
    public void markPlace(String lat, String lon) {
        if (placeForm == null) {
            placeForm = new PlaceForm(this);
        }
        /**
         * Autofill the waypoint form fields with current location and
         * autonumber (1,2,3...).
         */
        int waypointCount = places.size();
        placeForm.setValues("WP" + String.valueOf(waypointCount + 1), lat,
                lon);
        placeForm.setEditingFlag(false);
        display.setCurrent(placeForm);
    }

    /** 
     * Edit waypoint
     * @param wp 
     */
    public void editPlace(Place wp) {
        Logger.debug("Editing waypoint");
        if (wp == null) {
            showError("Selected waypoint is null");
            return;
        }
        if (placeForm == null) {
            placeForm = new PlaceForm(this);
        }
        placeForm.setValues(wp);
        placeForm.setEditingFlag(true);
        Logger.debug("Setting current display to display waypoint details");
        display.setCurrent(placeForm);
    }

    /**
     * @return Number of positions recorded
     */
    public int getRecordedPositionCount() {
        if (recorder != null) {
            Track recordedTrack = recorder.getTrack();
            int positionCount = recordedTrack.getPositionCount();
            return positionCount;
        } else {
            return 0;
        }
    }

    /**
     * @return Number of markers recorded
     */
    public int getRecordedMarkerCount() {
        if (recorder != null) {
            Track recordedTrack = recorder.getTrack();
            int markerCount = recordedTrack.getMarkerCount();
            return markerCount;
        } else {
            return 0;
        }
    }

    /**
     * @return Current position
     */
    public synchronized GpsPosition getPosition() {
        if (gpsDevice == null) {
            return null;
        }
        //  Logger.debug("Controller getPosition called");
        return ((GpsDevice) gpsDevice).getPosition();
    }

    /**
     * @return Current GpsGPGSA data object
     */
    /*  public synchronized GpsGPGSA getGPGSA() {
    if (gpsDevice == null) {
    return null;
    }
    return ((GpsDevice) gpsDevice).getGPGSA();
    }*/
    /**
     * Exit application
     * <ul>
     * <li> Disconnect
     * <li> Pause XXX : mchr : why do we pause?
     * <li> Save way points
     * <li> Notify destroyed
     * </ul>
     * XXX : mchr : Should we not try and save the trail?
     */
    public void exit() {
        this.disconnect();
        // pause the current track
        // this is here mainly for testing purposes,
        // don't know whether it should remain here.
        this.pause();
        if (status == STATUS_RECORDING) {
            controller.startStop();
        }
        savePlaces();
        midlet.notifyDestroyed();
    }

    /** Get settings */
    public RecorderSettings getSettings() {
        return settings;
    }

    /**
     * @return GPS URL String or "-" if mGpsDevice is null
     */
    public String getGpsUrl() {
        if (gpsDevice != null) {
            return gpsDevice.getAddress();
        } else {
            return "-";
        }
    }

    /** Show stream recovery screen */
    public void showStreamRecovery() {
        display.setCurrent(new StreamRecovery());
    }

    /** Show trail */
    public void showTrail() {
        //display.setCurrent(getTrailCanvas());
    }

    /**
     * @return Existing TrailCanvas<br />
     *         OR<br />
     *         Instantiate a new TrailCanvas with a null initial position or if
     *         possible the last position saved into the RMS
     */
   /* public TrailCanvas getTrailCanvas() {
        if (trailCanvas == null) {
            GpsPosition initialPosition = null;
            try {
                initialPosition = this.recorder.getPositionFromRMS();
            } catch (Exception anyException) {/* discard 

            }
            trailCanvas = new TrailCanvas(initialPosition);
        }
        return trailCanvas;
    }*/

    /**
     * @return Existing ElevationCanvas<br />
     *         OR<br />
     *         Instantiate a new ElevationCanvas with a null initial position or
     *         if possible the last position saved into the RMS
     */
    private ElevationCanvas getElevationCanvas() {
        if (elevationCanvas == null) {
            GpsPosition initialPosition = null;
            try {
                initialPosition = this.recorder.getPositionFromRMS();
            } catch (Exception anyException) { /* discard */

            }
            elevationCanvas = new ElevationCanvas(initialPosition);
        }
        return elevationCanvas;
    }

    /** Show splash canvas */
    public void showSplash() {
        display.setCurrent(new SplashAndUpdateCanvas());
    }

    /** Show export settings */
    public void showExportSettings(final Displayable displayable) {

        /** 
         * Trying to avoid deadlock by displaying the FileChooser in another
         * thread. Otherwise you'll be getting the following warning:
         *     To avoid potential deadlock, operations that may block, such as 
         *     networking, should be performed in a different thread than the 
         *     commandAction() handler.
         */
        Thread t = new Thread() {

            public void run() {
                super.run();
                display.setCurrent(getFileChooser(displayable));
            }
        };
        t.start();
    }

    /** Show export settings form */
    private ExportSettingsForm getExportSettingsForm() {
        if (exportSettingsForm == null) {
            exportSettingsForm = new ExportSettingsForm(this);
        }
        return exportSettingsForm;
    }

    /** Show export the file chooser */
    public FileChooser getFileChooser(Displayable displayable) {
        filechooser = new FileChooser(this, settings.getExportFolder(), false, displayable);

        return filechooser;
    }

    public void showImportTrailsScreen(Displayable displayable) {
        if (importTrailScreen == null) {
            importTrailScreen = new ImportTrailScreen(displayable);
        }
        controller.setCurrentScreen(importTrailScreen);
    }

    /** Set about screens as current display */
    public void showAboutScreen() {
        if (aboutScreen == null) {
            aboutScreen = new AboutScreen();
        }
        display.setCurrent(aboutScreen);
    }

    /** Set SMS Screen as current display */
    public void showSMSScreen() {
        if (smsScreen == null) {
            smsScreen = new SmsScreen();
        }
        display.setCurrent(smsScreen);
    }

    /** Show settings list */
    public void showSettings() {
        display.setCurrent(getSettingsList());
    }

    /** Get instance of settings list */
    private SettingsList getSettingsList() {
        if (settingsList == null) {
            settingsList = new SettingsList(this);
        }
        return settingsList;
    }

    /** Show waypoint list */
    public void showPlacesList() {
        if (placesList == null) {
            placesList = new PlaceList(this);
        }
        placesList.setPlaces(places);
        display.setCurrent(placesList);
    }

    /** Show dev menu */
    public void showDevelopmentMenu() {
        if (developmentMenu == null) {
            developmentMenu = new DevelopmentMenu();
        }
        display.setCurrent(developmentMenu);
    }

    /**
     * Show displayable object.
     * @param displayable 
     */
    public void showDisplayable(Displayable displayable) {
        display.setCurrent(displayable);
    }

    /** Show list of trails */
    public void showTrailsList() {
        if (trailsList == null) {
            trailsList = new TrailsList(this);
        } else {
            trailsList.refresh();
        }
        display.setCurrent(trailsList);
    }

    /**
     * @param xiTrail
     *                Trail object to display
     * @param xiTrailName
     *                Name of trail XXX : mchr : Can we infer the name of the
     *                Trail from the Track object?
     */
    public void showTrailActionsForm(Track trail, String trailName) {
        TrailActionsForm form = new TrailActionsForm(this, trail, trailName);
        display.setCurrent(form);
    }

    /**
     * @param xiTrack
     *                Track to load. If we load a null track then we clear the
     *                track and setLastPosition to null. Otherwise we set the
     *                track and load the last position.
     */
  /*  public void loadTrack(Track track) {
        if (track == null) {
            this.recorder.clearTrack();
            this.trailCanvas.setLastPosition(null);
        } else {
            this.recorder.setTrack(track);
            GpsPosition pos;
            try {
                pos = track.getEndPosition();
            } catch (NoSuchElementException e) {
                Logger.debug("No EndPosition found when trying to call Controller.loadTrack(Track). Setting to null");
                pos = null;
            }
            this.trailCanvas.setLastPosition(pos);
            this.elevationCanvas.setLastPosition(pos);
        }
    }

    /**
     * @param xiTrailName
     *                Name of trail to load details of
     */
    public void showTrailDetails(String trailName) {
        try {
            display.setCurrent(new TrailDetailsScreen(this, trailName));
        } catch (IOException e) {
            showError("An error occured when trying to retrieve the trail from the RMS!" + e.toString());
        }
    }

    /** Show device list */
    public void showDevices() {
        if (deviceList == null) {
            deviceList = new DeviceList(this);
        }
        display.setCurrent(deviceList);
    }

    /**
     * Show error message to the user
     * 
     * @param message
     *                Message which should shown to the user
     * @param seconds
     *                Tells how long (in seconds) the message will be displayed.
     *                0 or Alert.FOREVER will show the message with no timeout,
     *                means user has to confirm the message
     * @param type
     *                TODO
     */
    public Alert showAlert(final String message, final int seconds,
            AlertType type) {
        
        final Alert alert = new Alert("Information", message, null, type);
        alert.setTimeout(seconds == 0 || seconds == Alert.FOREVER ? Alert.FOREVER
                : seconds * 1000);
        // Put it into a thread as 2 calls to this method in quick succession
        // would otherwise fail... miserably.
        final Thread t = new Thread(new Runnable() {

            public void run() {
                try {
                    Display.getDisplay(midlet).setCurrent(alert);
                } catch (IllegalArgumentException e) {
                    // do nothing just log
                    Logger.warn("IllegalArgumetException occured " + "in showAlert");
                }
            }
        });
        t.start();
        return alert;
    }

    /**
     * @param xiMessage
     *                Message to be displayed forever
     */
    public Alert showError(String message) {
        return this.showAlert(message, Alert.FOREVER, AlertType.ERROR);
    }

    /**
     * @param xiMessage
     *                Message to be displayed forever
     */
    public Alert showInfo(String message) {
       final  Alert alert = new Alert("Information", message, null, AlertType.INFO);
        alert.setTimeout(Alert.FOREVER);
         final Thread t = new Thread(new Runnable() {

            public void run() {
                try {
                    Display.getDisplay(midlet).setCurrent(alert);
                } catch (IllegalArgumentException e) {
                    // do nothing just log
                    Logger.warn("IllegalArgumetException occured " + "in showAlert");
                }
            }
        });
        t.start();
        return alert;
       // return this.showAlert(message, Alert.FOREVER, AlertType.INFO);
    }

    /**
     * TODO
     */
    public Alert createProgressAlert(final String message) {
        final Alert alert = new Alert("Progress", message, null, AlertType.INFO);
        final Gauge gauge = new Gauge(null, false, 10, 0);
        alert.setTimeout(Alert.FOREVER);
        alert.setIndicator(gauge);
        // Put it into a thread as 2 calls to this method in quick succession
        // would otherwise fail... miserably.
        final Thread t = new Thread(new Runnable() {

            public void run() {
                Display.getDisplay(midlet).setCurrent(alert);
            }
        });
        t.start();
        return alert;
    }

    /** Update selected waypoint */
    public void updateWaypoint(String m_oldWaypointName, Place newWaypoint) {
        Enumeration waypointEnum = places.elements();
        while (waypointEnum.hasMoreElements()) {
            Place wp = (Place) waypointEnum.nextElement();
            String currentName = wp.getName();
            if (currentName.equals(m_oldWaypointName)) {
                int updateIndex = places.indexOf(wp);
                places.setElementAt(newWaypoint, updateIndex);
                return;
            }
        }
        savePlaces(); // Save waypoints immediately to RMS

    }

    /** Save waypoints to persistent storage */
    private void savePlaces() {
        settings.setPlaces(places);
    }

    /** Remove selected waypoint
     * @param wp 
     */
    public void removePlace(Place wp) {
        places.removeElement(wp);
    }

    /** Remove all waypoints */
    public void removeAllPlaces() {
        places.removeAllElements();
    }

    /**
     * @param place         Place object to display
     * @param placeName     Name of waypoint
     * @param exportAllWps  Are we exporting all places?
     */
    public void showPlaceActionsForm(Place place, String placeName, int actionType) {
        PlaceActionsForm form = new PlaceActionsForm(this, place, placeName, actionType);
        display.setCurrent(form);
    }

    /** Display recording settings form */
    public void showRecordingSettings() {
        if (recordingSettingsForm == null) {
            recordingSettingsForm = new RecordingSettingsForm(this);
        }
        display.setCurrent(recordingSettingsForm);
    }

    /** Set recording interval */
    public void saveRecordingInterval(int interval) {
        settings.setRecordingInterval(interval);
        recorder.setInterval(interval);
    }

    /** Display display settings form */
    public void showDisplaySettings() {
        if (displaySettingsForm == null) {
            displaySettingsForm = new DisplaySettingsForm(this);
        }
        display.setCurrent(displaySettingsForm);
    }

    /** Set recording marker step */
    public void saveRecordingMarkerStep(int newStep) {
        settings.setRecordingMarkerInterval(newStep);
        recorder.setIntervalForMarkers(newStep);
    }

    /** Get recorded track */
    public Track getTrack() {
        return recorder.getTrack();
    }

    /** Get current satellite count */
    public int getSatelliteCount() {
        if (gpsDevice != null) {
            return ((GpsDevice) gpsDevice).getSatelliteCount();
        } else {
            return 0;
        }
    }

    /** Get current satellites */
    public Vector getSatellites() {
        if (gpsDevice != null) {
            return ((GpsDevice) gpsDevice).getSatellites();
        } else {
            return null;
        }
    }

    /**
     * @param xiDisplayable
     *                Screen to Display
     */
    public void setCurrentScreen(Displayable displayable) {
        display.setCurrent(displayable);
    }

    /**
     * @return The current screen being displayed
     */
    public Displayable getCurrentScreen() {
        return this.display.getCurrent();
    }

    /**
     * Pause the track and save it to the RMS
     */
    public void pause() {
        if (controller.getStatusCode() == Controller.STATUS_RECORDING) {
            Logger.debug("Pausing current track");
            recorder.getTrack().pause();
        }
    }

    /**
     * Unpause by loading the last saved Track from the RMS and setting it as
     * the current track.
     */
    public void unpause() {
        try {
            Logger.debug("Resuming from pause");
            Track pausedTrack;
            FileSystem fs = FileSystem.getFileSystem();
            if (fs.containsFile(Track.PAUSEFILENAME)) {
                pausedTrack = new Track(fs.getFile(Track.PAUSEFILENAME));
                recorder.clearTrack();
                recorder.setTrack(pausedTrack);
                fs.deleteFile(Track.PAUSEFILENAME);
            }
        } catch (IOException e) {
            Logger.error("Resume from pause failed: " + e.getMessage());
        }

    }

    /**
     * @return true if a pause file exists in the RMS
     */
    public boolean checkIfPaused() {
        FileSystem fs = FileSystem.getFileSystem();
        boolean status = false;
        if (fs.containsFile(Track.PAUSEFILENAME)) {
            status = true;
        }

        return status;

    }

    /** Rotate around main displays */
/*    public void switchDisplay() {
        currentDisplayIndex++;
        if (currentDisplayIndex >= screens.length) {
            currentDisplayIndex = 0;
        }

        BaseCanvas nextCanvas = screens[currentDisplayIndex];
        if (nextCanvas != null) {
            display.setCurrent(screens[currentDisplayIndex]);
        }
    }
*/
    /** Get ghost trail */
    public Track getGhostTrail() {
        return ghostTrail;
    }

    /** Set ghost trail */
    public void setGhostTrail(Track ghostTrail) {
        this.ghostTrail = ghostTrail;
    }

    /** Export the current recorded trail to a file with the specified format */
    public void exportTrail(Track recordedTrack, int exportFormat,
            String trackName) {
        try {
            boolean useKilometers = settings.getUnitsAsKilometers();
            String exportFolder = settings.getExportFolder();
            recordedTrack.writeToFile(exportFolder, places, useKilometers,
                    exportFormat, trackName, null);
        } catch (Exception ex) {
            Logger.error(ex.toString());
            showError(ex.getMessage());
        // XXX : mchr : Do something more sensible with some exceptions?
        // or perhaps have a test write feature when setting up path to
        // try and avoid exceptions
        }
    }

    public int getNumAlphaLevels() {
        return display.numAlphaLevels();
    }

    public void setUseJsr179(boolean b) {
        useJsr179 = b;
        settings.setJsr179(useJsr179);
    }

    public boolean getUseJsr179() {
        return settings.getJsr179();
    }

    public void setUseFileCache(boolean b) {
        useFileCache = b;
        settings.setFileCache(useFileCache);
    }

    public boolean getUseFileCache() {
        return useFileCache;
    }

    public boolean getNavigationStatus() {
        return navigationOn;
    }

    public void setNavigationStatus(boolean b) {
        navigationOn = b;
    }

    public void setNavigationPlace(Place input) {
        navpnt = new Place("NAVPL", input.getLatitude(), input.getLongitude());
        setNavigationStatus(true);
    }

    public Place getNavigationPlace() {
        return navpnt;
    }
    
    /* Mapunity Modifications */
     public void MainMenu()
    {
         if(mainmenu == null){
             mainmenu = new MainMenu(this,midlet);
      
         }
              mainmenu.initMenuList();
             display.setCurrent(mainmenu);
         
           
        
       
        
    }
     
        
        /** Display Search Reuslts settings form */
    public void showsearchResults(String qry) {
        if (searchResutls == null) {
            
            searchResutls = new searchResults(this,qry);
            
            searchResutls.refreshSearchList();
        }
        else{
       
        searchResutls.setQueryWord(searchFormScreen.getQuery());
        searchResutls.refreshSearchList();
        }
         display.setCurrent(searchResutls);
    }

    public void FromSuggestResults(String qry) {
         
        if (directionSuggestFrom == null) {
            directionSuggestFrom = new DirectionSuggestFrom(this,qry);
            directionSuggestFrom.refreshSearchList();
        }
        else{
       
        directionSuggestFrom.setQueryWord(directionsFormField.getQuery());
        directionSuggestFrom.refreshSearchList();
        }
        controller.showProgressBar();
        // display.setCurrent(directionSuggestFrom);
    }
    
  public void ShowFromSuggestResults() {
      display.setCurrent(directionSuggestFrom);
      
  }
  public void ShowToSuggestResults(){
      display.setCurrent(directionSuggestTo);
  }
    public void ToSuggestResults(String qry) {
        if (directionSuggestTo == null) {
            directionSuggestTo = new DirectionSuggestTo(this,qry);
            directionSuggestTo.refreshSearchList();
        }
        else{
       
        directionSuggestTo.setQueryWord(directionsToField.getQuery());
        directionSuggestTo.refreshSearchList();
        }
         display.setCurrent(directionSuggestTo);
    }

    public void showProgressBar()
    {
        Display.getDisplay(midlet).setCurrent(ProgressForm);
    }
    
          /** Save waypoints to persistent storage */
    public void ShowSearchForm() {
         
         if (searchFormScreen == null)
         {
          
          searchFormScreen = new Locations(controller);
          }
          searchFormScreen.initList();
          display.setCurrent(searchFormScreen);
          
    }
    
    
     public void ShowDirectionsFormForFrom() {
       
         if (directionsFormField == null)
         {
          directionsFormField = new DirectionsFormField(controller);
          display.setCurrent(directionsFormField);
        
         }else{
         directionsFormField.clearQryField();
            display.setCurrent(directionsFormField);
            System.out.println("okok8888");
            
         }
    }
     public void ShowDirectionsFormForTo() {
       
         if (directionSuggestTo == null)
         {
          directionsToField = new DirectionsToField(controller);
          display.setCurrent(directionsToField);
        
         }else{
         directionsToField.clearQryField();
            display.setCurrent(directionsToField);
            System.out.println("okok8888");
            
         }
    }
     
         public PointResultCanvas getPointingCanvas(String name,String lat,String lon) {
         
          double aDoubleLat = Double.parseDouble(lat);
          //  initialPosition.latitude = aDoubleLat;
            
             double aDoubleLon = Double.parseDouble(lon);
        if (pointresultCanvas == null) {
          
              
             
           // initialPosition.longitude = aDoubleLon;
              locatePoint.setLocatePoint(name, aDoubleLat, aDoubleLon);
              
            try {
               // initialPosition = this.recorder.getPositionFromRMS();
            } catch (Exception anyException) {/* discard */
            }
              
            System.out.println("as double "+locatePoint.LocationName+"--"+locatePoint.latitude+"--"+locatePoint.longitude);
            try{
            pointresultCanvas = new PointResultCanvas(locatePoint);    
            }catch(Exception e){
                System.out.println("Error in creating PTCanvas"+e);
            }
            
        }
        else{
            System.out.println("not null ");
            pointresultCanvas.glbLatitude = aDoubleLat;
            pointresultCanvas.glbLongitude = aDoubleLon;
            pointresultCanvas.gotoLonLat((float)aDoubleLon, (float)aDoubleLat, pointresultCanvas.zoom, true) ;
            
            pointresultCanvas.m_listMyPlaces.addElement(new LocationPointer(name, (float)aDoubleLon,(float)aDoubleLat, pointresultCanvas.zoom, true));
        }
        return pointresultCanvas;
    }
   
   public PointResultCanvas pointBusStages(String lat,String lon)
   {
        double aDoubleLat = Double.parseDouble(lat);
        double aDoubleLon = Double.parseDouble(lon);
        if (pointresultCanvas == null) {
        locatePoint.setLocatePoint("", aDoubleLat, aDoubleLon);
              
            try {
               // initialPosition = this.recorder.getPositionFromRMS();
            } catch (Exception anyException) {/* discard */
            }
              
             pointresultCanvas = new PointResultCanvas(locatePoint);
        }
        else{
            System.out.println("not null ");
            pointresultCanvas.glbLatitude = aDoubleLat;
            pointresultCanvas.glbLongitude = aDoubleLon;
            pointresultCanvas.gotoLonLat((float)aDoubleLon, (float)aDoubleLat, pointresultCanvas.zoom, true) ;
            
            pointresultCanvas.busStagesPoints.addElement(new LocationPointer("", (float)aDoubleLon,(float)aDoubleLat, pointresultCanvas.zoom, true));
        }
        return pointresultCanvas;
   }
   
   public PointResultCanvas gotoSpot(String lat,String lon)
   {
        double aDoubleLat = Double.parseDouble(lat);
        double aDoubleLon = Double.parseDouble(lon);
        if (pointresultCanvas == null) {
        locatePoint.setLocatePoint("", aDoubleLat, aDoubleLon);
              
            try {
               // initialPosition = this.recorder.getPositionFromRMS();
            } catch (Exception anyException) {/* discard */
            }
              
             pointresultCanvas = new PointResultCanvas(locatePoint);
        }
        else{
            //System.out.println("not null ");
            pointresultCanvas.glbLatitude = aDoubleLat;
            pointresultCanvas.glbLongitude = aDoubleLon;
            System.out.println("LAt and lon is "+pointresultCanvas.glbLatitude +"  "+pointresultCanvas.glbLongitude);
            pointresultCanvas.gotoLonLat((float)aDoubleLon, (float)aDoubleLat, pointresultCanvas.zoom, true) ;
          
        }
        return pointresultCanvas;
   }
   
   public void showIdMarker(String name,String lat,String lon){
       display.setCurrent(MapIdPointer(name,lat,lon));
       
   }
   public PointResultCanvas MapIdPointer(String name,String lat,String lon) {
          double aDoubleLat = Double.parseDouble(lat);
          //  initialPosition.latitude = aDoubleLat;
            
             double aDoubleLon = Double.parseDouble(lon);
        if (pointresultCanvas == null) {
          
              
             
           // initialPosition.longitude = aDoubleLon;
              locatePoint.setLocatePoint(name, aDoubleLat, aDoubleLon);
              
            try {
               // initialPosition = this.recorder.getPositionFromRMS();
            } catch (Exception anyException) {/* discard */
            }
              
               System.out.println("as double "+locatePoint.LocationName+"--"+locatePoint.latitude+"--"+locatePoint.longitude);
            pointresultCanvas = new PointResultCanvas(locatePoint);
        }
        else{
            System.out.println("not null ");
            pointresultCanvas.glbLatitude = aDoubleLat;
            pointresultCanvas.glbLongitude = aDoubleLon;
            pointresultCanvas.gotoLonLat((float)aDoubleLon, (float)aDoubleLat, 1, true) ;
            
             pointresultCanvas.mapIdName.addElement(new LocationPointer(name, (float)aDoubleLon,(float)aDoubleLat, 1, true));
             System.out.println("passwd");
             pointresultCanvas.dispCounter = 0;
        }
        return pointresultCanvas;
      
      
   }
   
      public PointResultCanvas TrafficSpots(Vector trafficSpots) {
       
        if (pointresultCanvas == null) {
              //locatePoint.setLocatePoint("", Double.parseDouble(Controller.getController().initLat),Double.parseDouble(Controller.getController().initLon));
              
            try {
               // initialPosition = this.recorder.getPositionFromRMS();
            } catch (Exception anyException) {/* discard */
            }
              
           
            pointresultCanvas = new PointResultCanvas(locatePoint);
             pointresultCanvas.TrafficPoints = trafficSpots;
        }
        else{
            System.out.println("not null ");
            pointresultCanvas.TrafficPoints = trafficSpots;
        }
        
        
        return pointresultCanvas;
      
      
   }
   
   public PointResultCanvas DirectPointingCanvas(String lat,String lon,String [] DirectionsArray) {
        if (pointresultCanvas == null) {
          
              
              double aDoubleLat = Double.parseDouble(lat);
          //  initialPosition.latitude = aDoubleLat;
            
             double aDoubleLon = Double.parseDouble(lon);
           // initialPosition.longitude = aDoubleLon;
              locatePoint.setLocatePoint("", aDoubleLat, aDoubleLon);
              
            try {
               // initialPosition = this.recorder.getPositionFromRMS();
            } catch (Exception anyException) {/* discard */
            }
              
               System.out.println("as double "+locatePoint.LocationName+"--"+locatePoint.latitude+"--"+locatePoint.longitude);
            pointresultCanvas = new PointResultCanvas(locatePoint);
            //
        }
        return pointresultCanvas;
    }
     
      /** Show Pointing canvas */
    public void showPointinTrail(String name1,String lat1,String lon1) {
      /*  if(pointresultCanvas ==null){
          
            pointresultCanvas = new PointResultCanvas(locatePoint);
        }
        */
        System.out.println("as string "+name1+"--"+lat1+"--"+lon1);
        
        display.setCurrent(getPointingCanvas(name1,lat1,lon1));
    }
    
    public void ShowPointingCanvas()
    {
         display.setCurrent(pointresultCanvas);
    }
    
     public void ShowPointingCanvasBoot()
    {
         display.setCurrent(pointresultCanvas);
         //display.setCurrent(mainmenu);
    }
    
    
     
          public void drawDirections()
    {
            /*  if (controller.route.isEmpty() == false){
                  System.out.println("I am not empty");
                  controller.route.removeAllElements();
              }*/
              
         Vector temp = new Vector();
         String [] chunk,chunk1;
         double lat,lon,lastLatitude,lastLongitude;
         
         chunk =StringUtil.split(DirectionsArray[0].toString(),"||");
                            lat = Double.parseDouble(chunk[0].toString());
                           lon = Double.parseDouble(chunk[1].toString());
         pointresultCanvas.gotoLonLat((float)lon, (float)lat, pointresultCanvas.zoom, true) ;
         int len = DirectionsArray.length;
         System.out.println("length of the direction "+DirectionsArray.length);
         int i;
        for(i =0;i<DirectionsArray.length-2;i++)
            
        {
                           chunk =StringUtil.split(DirectionsArray[i].toString(),"||");
                            lat = Double.parseDouble(chunk[0].toString());
                           lon = Double.parseDouble(chunk[1].toString());
                            temp.addElement(new LocationPointer("",(float)lon,(float)lat,controller.globalZoom,true));
                          //  System.out.println("added to route" + lat +"  "+lon);
        }
         System.out.println("Km is as shown below "+DirectionsArray[DirectionsArray.length-1]);
         if (controller.selectedCity.autoFare == 0){
            this.InfoDisplay = " The distance for your trip is "+DirectionsArray[DirectionsArray.length-1] +" km."; 
         }else{
                      double Autofare =  Double.parseDouble(DirectionsArray[DirectionsArray.length-1]) * controller.selectedCity.autoFare;
         System.out.println("AutoFare --  "+Math.ceil(Autofare));
        this.InfoDisplay = " The distance for your trip is "+DirectionsArray[DirectionsArray.length-1] +" km.  The approximate auto fare is Rs. "+Math.ceil(Autofare) +".";
     
         }
    
         controller.pointresultCanvas.infoCounter =2;
         controller.route = null;
         controller.route=temp;
         
         
    }
    
           public void showAcknowledeScreen() {
        if (ackScreen == null) {
            ackScreen = new Acknowledgement();
        }
        display.setCurrent(ackScreen);
    }
        public void showTrafficCams() {
        if (trafficCams == null) {
            trafficCams = new TrafficCams(controller);
        }
        trafficCams.initList();
        display.setCurrent(trafficCams);
    }
        
         public void showBus() {
        if (buses == null) {
            buses = new Buses(controller);
        }
        buses.refreshData();
        display.setCurrent(buses);
    }
    public Display getDisp()
    {
        return this.display;
    }
    
       
   public void showBusPosition(String name,String lat,String lon){
       display.setCurrent(BusPosition(name,lat,lon));
       
   }
   public PointResultCanvas BusPosition(String name,String lat,String lon) {
          double aDoubleLat = Double.parseDouble(lat);
          //  initialPosition.latitude = aDoubleLat;
            
             double aDoubleLon = Double.parseDouble(lon);
        if (pointresultCanvas == null) {
          
              
             
           // initialPosition.longitude = aDoubleLon;
              locatePoint.setLocatePoint(name, aDoubleLat, aDoubleLon);
              
            try {
               // initialPosition = this.recorder.getPositionFromRMS();
            } catch (Exception anyException) {/* discard */
                
            }
              
               System.out.println("as double "+locatePoint.LocationName+"--"+locatePoint.latitude+"--"+locatePoint.longitude);
            pointresultCanvas = new PointResultCanvas(locatePoint);
        }
        else{
            System.out.println("not null ");
            pointresultCanvas.glbLatitude = aDoubleLat;
            pointresultCanvas.glbLongitude = aDoubleLon;
            pointresultCanvas.gotoLonLat((float)aDoubleLon, (float)aDoubleLat, 1, true) ;
            
             pointresultCanvas.BusPosition.addElement(new LocationPointer(name, (float)aDoubleLon,(float)aDoubleLat, 1, true));
             System.out.println("passwd");
             pointresultCanvas.dispCounter = 0;
        }
        return pointresultCanvas;
      
      
   }
   
   public CitySelection citySelectionList(){
       
       if (citySelection == null){
         
           citySelection = new CitySelection(controller);
       }
       
       return citySelection;
       
   }
    
  public void ShowCitySelectionList()
    {
         display.setCurrent(citySelectionList());
    }
    
  public void loadCityMatrix(){
                   try{
                  Downloader dwn = new Downloader(controller);
                   String cityData = settings.getCityMatrix();
                   System.out.println(" THe local data is "+cityData);
                   String [] TotalData = dwn.parsePipes(cityData);
                   controller.cityHash.clear();
                   for (int data=0;data<TotalData.length;data++){
                       String[] chunk = dwn.parseTilda(TotalData[data]);
                       
                       controller.cityHash.put(chunk[0].toString(), new CityData(chunk[0].toString(),chunk[1].toString(),Integer.parseInt(chunk[2]),Integer.parseInt(chunk[3]),Integer.parseInt(chunk[4]),Integer.parseInt(chunk[5]),Integer.parseInt(chunk[6]),Integer.parseInt(chunk[7]),Integer.parseInt(chunk[8]),Integer.parseInt(chunk[9]),chunk[10].toString(),chunk[11].toString(),chunk[12].toString()));                       

//                       for(int i=0;i<chunk.length;i++){
//                           System.out.print(chunk[i]);
//                       }
                   }
                 
                controller.selectedCity = (CityData) controller.cityHash.get(mapCity);     
                controller.initLat = controller.selectedCity.LATITUDE;
                controller.initLon = controller.selectedCity.LONGITUDE;
             }catch(Exception e){
            System.out.print("Error in loading city matrix"+e);
            //                                                                       loc , mapid, dir,  bus ,cam , hotpst , fine, buspos     lat        lon       autofare                 
           // cityHash.put("Bangalore", new CityData("Bangalore","btis.in",            1,    1,     1,    1,    1,   1,       1,       1   ,"12.97579","77.61142","7"));
            //cityHash.put("Hyderabad", new CityData("Hyderabad","htis.in",            1,    1,     1,    0,    0,   1,       0,       0   ,"17.4404", "78.3893",0));
    //        cityHash.put("Chennai",   new CityData("Chennai","chennaitraffic.in",    1,    1,     0,    1,    1,   1,       0,       0   ,"13.07086","80.2304006",0));
    //        cityHash.put("Delhi",     new CityData("Delhi","dtis.in",                1,    1,     1,    0,    0,   1,       0,       0   ,"28.642399","77.1842999"));
    //        cityHash.put("Pune",      new CityData("Pune","ptis.in",                 1,    1,     1,    0,    0,   1,       0,       0   ,"18.51558","73.85614"));
    //        cityHash.put("Indore",    new CityData("Indore","indoretransport.in",    1,    1,     1,    1,    0,   0,       0,       0   ,"22.72341","75.88134"));
    //        cityHash.put("Mysore",    new CityData("Mysore","mysoretransport.in",    1,    1,     1,    1,    0,   0,       1,       0   ,"12.97579","77.61142"));
    //      
            //controller.selectedCity = (CityData) controller.cityHash.get("Bangalore");
            settings.setCityMatrix("Bangalore~~btis.in~~1~~1~~1~~1~~1~~1~~1~~1~~12.97579~~77.61142~~7||Hyderabad~~htis.in~~1~~1~~1~~0~~0~~1~~0~~0~~17.41424~~78.48541~~0");
            loadCityMatrix();
             }
  }
  

  public class DownloadTimer extends TimerTask
  {
    public final void run()
    {
      // Is current value of gauge less than the max?
        int newValue = ProgressBar.getValue() + delta;
        if (newValue == ProgressBar.getMaxValue()) {
                 newValue = 0;
                //delta = 1;
            } else if (newValue == 0) {
                delta = 1;
            }

            ProgressBar.setValue(newValue);
 
    }
  }
}