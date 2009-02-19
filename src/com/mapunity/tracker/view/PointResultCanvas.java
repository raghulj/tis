
package com.mapunity.tracker.view;



import javax.microedition.lcdui.game.Sprite;

import com.mapunity.gps.GpsPosition;
import com.mapunity.map.MapLocator;
import com.mapunity.map.TileDownloader;
import com.mapunity.tracker.controller.Controller;
import com.mapunity.util.*;
import com.mapunity.util.ProjectionUtil;
import com.mapunity.util.StringUtil;
import com.mapunity.tracker.model.*;


import javax.microedition.lcdui.game.GameCanvas.*;
import javax.microedition.lcdui.*;
import java.util.*;


public class PointResultCanvas extends BaseCanvas implements Runnable{

    private LocatePoint lastPosition;
    private CanvasPoint lastCanvasPoint;
    
    public double glbLatitude;
    public double glbLongitude;
    private int counter;
    private String error;
    public double GPSLatitude;
    public double GPSLongitude;
    private int GPSIndex;
    BaseCanvas basecanvas;
    private Timer dispTimer;
   
    private DispTimerTask dTt;
    Graphics glbg;
     LocationPointer lcptr;

    private Sprite compassArrows;
    private Image compass;

    /** Trail drawing helpers */
    private int midWidth;
    private int midHeight;
    private int movementSize;
    private int verticalMovement;
    private int horizontalMovement;

    private final int MAX_ZOOM = 20;
    private final int MIN_ZOOM = 1;

    private Image redDotImage;
    private Image gpsDot;
    private Image CenterPointer;
    private Image idMarkers;
   
    
     private boolean largeDisplay;
   

     public int px;
     public int py;
     public int startup=0; 
     public LocationPointer wayPoint;
     public LocationPointer currentSel;
    
     Graphics gPoint ;
     public LocationPointer gpsPoint; 
    
     public int zoom =6; // Used by both the map and trail
     public int dispCounter = 0;
     public int infoCounter =2;

  
     private Image mapTiles[] = new Image[9];
     public int m[] = new int[] { 0, 1, 2, 0, 1, 2, 0, 1, 2 };
     public int n[] = new int[] { 0, 0, 0, 1, 1, 1, 2, 2, 2 };

     public int xpoint;
     public int ypoint;

     public int screenWidth;
     public int screenHeight;
     
     public int X0Point;
     public int Y0Point;
     public static int tRate = 256;
     
     private long O;
     public boolean w;

     // vector for search
     public Vector m_listMyPlaces;
     // for bus stages points
     public Vector busStagesPoints;
     // vector for curremt GPS position
     public Vector TempGpsPointer;
     
     public Vector mapIdName;
     
     public Vector TrafficPoints; 
     
     public LocationPointer GPSpointer;
     
     public Vector BusPosition;
    
     private TileDownloader tileDownloader = null;

     CanvasPoint forSearch;
    /**
     * Creates a new instance of TrailCanvas
     * 
     * @param initialPosition
     */
    public PointResultCanvas(LocatePoint initialPosition) {
        super();
        this.setLastPosition(initialPosition);
        controller.globalZoom = zoom;
        m_listMyPlaces = new Vector();
        mapIdName = new Vector();
        busStagesPoints = new Vector();
        TrafficPoints = new Vector();
        BusPosition = new Vector();
        
        verticalMovement = 0;
        horizontalMovement = 0;

        // init images 
        redDotImage = ImageUtil.loadImage("/images/mark.png");
        CenterPointer = ImageUtil.loadImage("/images/pointer.png");
        idMarkers = ImageUtil.loadImage("/images/idmarker.png");
        gpsDot = ImageUtil.loadImage("/images/gpsDot.png");
        counter = 0;
       
        calculateDisplaySize(getWidth(),getHeight()) ;        

        screenWidth = getWidth();
        screenHeight = getHeight();
        
        Thread thread = new Thread(this);
        thread.start();
     
        // initial position of the map
        gotoLonLat( Float.parseFloat(Controller.getController().initLon),Float.parseFloat(Controller.getController().initLat), controller.globalZoom, false);
     
        dispTimer = new Timer();
        dTt = new DispTimerTask(); 
 dispTimer.schedule(dTt,0,2000);    
    }
    
    

    /** 
     * Paint trails and maps
     * @param g 
     */
    public void paint(Graphics g) {
        
        try {
            final int height = getHeight();
            final int width = getWidth();
            g.setClip(X0Point, Y0Point, screenWidth, screenHeight);
            
             if(width/2 != midWidth || height/2 !=midHeight ) {
                calculateDisplaySize(width,height) ;
            }
           

           
            /** Fill background with white */
            g.setColor(COLOR_WHITE);
            g.fillRect(0, 0, width, height);
           
            RecorderSettings settings = controller.getSettings();
            if( controller.disableMaps != true)
            {
                drawMaps(g);
            }

            int scale = 1 << controller.globalZoom;
            int xCordi = xpoint / scale - screenWidth / 2;
            int yCordi = ypoint / scale - screenHeight / 2;
            int tyPt = Y0Point - yCordi % tRate;
            int tempYcordi = yCordi;
            
            do
            {
                int lpXpt = X0Point - xCordi % tRate;
                int tempXcordi = xCordi;
                do
                {
                    lpXpt += tRate;
                    tempXcordi += tRate;
                } while(lpXpt < screenWidth);
                tyPt += tRate;
                tempYcordi += tRate;
            } while(tyPt < screenHeight);
             
        
            // Locate the pointing objects in the screen
            byte mvDist = 20;
            int initMv = 0;
            LocationPointer locPtr = pointSelectedMarks(g, xpoint, ypoint + initMv, controller.globalZoom, mvDist);
            if(locPtr != null)
            {
            locPtr.paint(g, xpoint, ypoint, X0Point, Y0Point, screenWidth, screenHeight,controller.globalZoom,true);
            }

            /** Draw status bar */
            drawStatusBar(g);
            
            //used when external gps is used 
            drawCompass(g);


            /** Draw current trail */
            Track currentTrail = controller.getTrack();
            gPoint =g;
            drawTrail(g, currentTrail, 0xDD0000, settings.getDrawWholeTrail());
            
            /** Draw current location with Cross Marker */
            
      
           g.drawImage(CenterPointer, midWidth, midHeight, Graphics.VCENTER | Graphics.HCENTER);
               
           /** scale bar at bottom of screen */
            drawZoomScaleBar(g);
           
            
        } catch (Exception e) {
            Logger.debug("Caught exception:"+e.getMessage());
        }
        
       
    }


    public void setLastPosition(LocatePoint position) {
        if (position != null)
        {
           // this.lastPosition = null;
            this.lastPosition = position;
            setLastPosition(position.latitude, position.longitude, controller.globalZoom);
        }
    }
    
    public void setSearchPosition() {
       
           // this.lastPosition = null;
            this.lastPosition.latitude = this.glbLatitude;
            this.lastPosition.longitude = this.glbLongitude;

            setLastPosition(lastPosition.latitude, lastPosition.longitude, controller.globalZoom);
       
    }

    private void calculateDisplaySize(int width, int height) {
        midWidth = width / 2;
        midHeight = height / 2;
        movementSize = width/8;    
        Image tempCompassArrows = ImageUtil
                .loadImage("/images/compass-arrows.png");
        compass = ImageUtil.loadImage("/images/compass.png");
        
         // Check for high resolution (eg. N80 352x416)
        compassArrows = new Sprite(tempCompassArrows, 11, 11);
            

       
 
    }
    
        protected void drawCompass(Graphics g) {
        if (lastPosition != null) {
            int fix = 10;
            if (largeDisplay) {
                fix = 20;
            }
             compassArrows.setFrame(GPSIndex);
           
        }
    }
    
    /**
     * 
     * @param g
     * @param drawMap
     */
    private void drawMaps(Graphics g) {
        // conditionally draw background map tiles


            if (tileDownloader == null) {
                Logger.debug("Starting TileDownloader Instance:");
                tileDownloader = new TileDownloader();
                tileDownloader.start();
            }
            if (lastPosition != null) {
                if (tileDownloader != null
                        && tileDownloader.isStarted() == true) {
                  drawSomethingPanning(g);
                   
                }

            }
 

    }

    /** Draw waypoints */


    /** Set last position */
    private void setLastPosition(double lat, double lon, int zoom) {
        lastCanvasPoint = ProjectionUtil.toCanvasPoint(lat, lon, zoom);
    }

    /** Convert position to canvas point */
    private CanvasPoint convertPosition(double lat, double lon) {

        CanvasPoint merc = ProjectionUtil.toCanvasPoint(lat, lon, controller.globalZoom);

        int relativeX = (merc.X - lastCanvasPoint.X) + midWidth
                + horizontalMovement;
        int relativeY = (merc.Y - lastCanvasPoint.Y) + midHeight
                + verticalMovement;
           
      
         System.out.println("diffx: " + (int)relativeX);
         System.out.println("lastpoint: " + (int)(lastCanvasPoint.X));

        CanvasPoint relativePoint = new CanvasPoint((int) (relativeX),
                (int) (relativeY));
        return relativePoint;


    }

    /** Draw trail with a given color */
   private void drawTrail(Graphics g, Track trail, int color,
            boolean drawWholeTrail) {
        try {
            if (trail == null) {
                return;
            }

            g.setColor(color);

            // TODO: implement the drawing based solely on numPositions.
            final int numPositionsToDraw = controller.getSettings()
                    .getNumberOfPositionToDraw();

            final int numPositions;
            synchronized (trail) {
                /*
                 * Synchronized so that no element can be added or removed
                 * between getting the number of elements and getting the
                 * elements themselfs.
                 */
                numPositions = trail.getPositionCount();

                /** Set increment value */
                int increment;
                if (drawWholeTrail) {
                    increment = numPositions / numPositionsToDraw;
                    if (increment < 1) {
                        increment = 1;
                    }
                } else {
                    increment = 1;
                }

                int positionsDrawn = 0;

                try {
                   
                    if (trail != null && trail.getEndPosition() != null) {
                   
                        double lastLatitude = trail.getEndPosition().latitude;
                        double lastLongitude = trail.getEndPosition().longitude;

                        for (int index = numPositions - 2; index >= 0; index -= increment) {
                            GpsPosition pos = trail.getPosition(index);

                            double lat = pos.latitude;
                            double lon = pos.longitude;
                            CanvasPoint point1 = convertPosition(lat, lon);
                            CanvasPoint point2 = convertPosition(lastLatitude,lastLongitude);

                            lastLatitude = pos.latitude;
                            lastLongitude = pos.longitude;
                            GPSLatitude =  pos.latitude;
                            GPSLongitude = pos.longitude;
                            GPSIndex = pos.getHeadingIndex();
                           
                            // Adding GPS point to the vector so that new posision is displayed
                            TempGpsPointer.addElement(new LocationPointer("",(float)GPSLongitude,(float)GPSLatitude,controller.globalZoom,true));
                           
                            
                            positionsDrawn++;
                            if (!drawWholeTrail
                                    && positionsDrawn > numPositionsToDraw) {
                                break;
                            }
                        }
                    }
                } catch (NullPointerException npe) {
                    Logger.error("NPE while drawing trail");
                }
            }
        } catch (Exception ex) {
            Logger.warn("Exception occured while drawing trail: "
                    + ex.toString());
        }
    }

    /** Draw controller.globalZoom scale bar */
    private void drawZoomScaleBar(Graphics g) {
        String text = "", unit = "";
        double lat, lon;

        if (lastPosition != null) {
            lat = lastPosition.latitude;
            lon = lastPosition.longitude;
        } else {
            lat = 0;
            lon = 0;
        }
        double pixelSize = ProjectionUtil.pixelSize(lat, lon, 17-controller.globalZoom);
        double barDist = 1;
        int scaleLength;
        int scaleParts;
        RecorderSettings settings = controller.getSettings();


        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN,
                Font.SIZE_SMALL));
        final int MARGIN_LEFT = 2; // left margin of the complete controller.globalZoom scale
        // bar
        final int MARGIN_BOTTOM = 3; // bottom margin of the complete controller.globalZoom
        // scale bar

        scaleLength = getWidth() / 2;
        pixelSize *= scaleLength;

        if (!settings.getUnitsAsKilometers()) {
            if (pixelSize > 1600) {
                pixelSize /= (1000 * UnitConverter.KILOMETERS_IN_A_MILE);
                unit = "ml";
            } else {
                pixelSize /= UnitConverter.METERS_IN_A_FOOT;
                unit = "ft";
            }
        }

        while (barDist < pixelSize)
            barDist *= 10;
        barDist /= 10;
        if ((barDist * 5) < pixelSize) {
            barDist *= 5;
            scaleParts = 5;
        } else {
            if ((barDist * 2) < pixelSize)
                barDist *= 2;
            scaleParts = 4;
        }

        scaleLength = (int) (scaleLength * barDist / pixelSize);

        g.setColor(0, 0, 0); // black color
        g.drawLine(MARGIN_LEFT, getHeight() - MARGIN_BOTTOM, MARGIN_LEFT
                + scaleLength, getHeight() - MARGIN_BOTTOM);
        g.drawLine(MARGIN_LEFT, getHeight() - MARGIN_BOTTOM, MARGIN_LEFT,
                getHeight() - MARGIN_BOTTOM - 3);
        g.drawLine(MARGIN_LEFT + scaleLength, getHeight() - MARGIN_BOTTOM,
                MARGIN_LEFT + scaleLength, getHeight() - MARGIN_BOTTOM - 3);

        /* Divide the complete scale bar into smaller parts */
        int scalePartLength = (int) (scaleLength / scaleParts);
        for (int i = 1; i < scaleParts; i++) {
            g.drawLine(MARGIN_LEFT + scalePartLength * i, getHeight()
                    - MARGIN_BOTTOM, MARGIN_LEFT + scalePartLength * i,
                    getHeight() - MARGIN_BOTTOM - 2);
        }

        /*
         * Build text for the right end of the scale bar and get width of this
         * text
         */
        if (settings.getUnitsAsKilometers()) {
            if (barDist > 1000) {
                barDist /= 1000;
                unit = "km";
            } else {
                unit = "m";
            }
        }
        text = Integer.toString((int) barDist);

        int textWidth = g.getFont().stringWidth(text);

        g.drawString("0", MARGIN_LEFT - 1, getHeight() - MARGIN_BOTTOM - 2,
                Graphics.BOTTOM | Graphics.LEFT);
        g.drawString(text + unit, MARGIN_LEFT + scaleLength - textWidth / 2,
                getHeight() - MARGIN_BOTTOM - 2, Graphics.BOTTOM
                        | Graphics.LEFT);
    }

    /** Draw status bar */
    private void drawStatusBar(Graphics g) {
        
        // int width = getWidth();
        int height = getHeight();

        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN,
                Font.SIZE_SMALL));
        Font currentFont = g.getFont();
        int fontHeight = currentFont.getHeight();

        /** Draw status */
        g.setColor(0, 0, 255);

        /** Draw status */
        g.setColor(0, 0, 0);
        if (lastPosition != null) {

            int positionAdd = currentFont.stringWidth("LAN:O");
            int displayRow = 1;

            RecorderSettings settings = controller.getSettings();

            Date now = Calendar.getInstance().getTime();

            /** Draw current time */


            /** Draw coordinates information */
            if (settings.getDisplayValue(RecorderSettings.DISPLAY_COORDINATES) == true) {
                g.drawString("LAT:", 1, fontHeight, Graphics.TOP
                        | Graphics.LEFT);
                g.drawString("LON:", 1, fontHeight * 2, Graphics.TOP
                        | Graphics.LEFT);

                double latitude = lastPosition.latitude;
                g.drawString(
                /* Get degrees in string format (with five decimals) */
                StringUtil.valueOf(latitude, 5), positionAdd, fontHeight,
                        Graphics.TOP | Graphics.LEFT);

                double longitude = lastPosition.longitude;
                g.drawString(
                /* Get degrees in string format (with five decimals) */
                StringUtil.valueOf(longitude, 5), positionAdd, fontHeight * 2,
                        Graphics.TOP | Graphics.LEFT);

                displayRow += 2;
            }
            
            g.drawString("Zoom :" + (17-controller.globalZoom), 1, fontHeight * displayRow,
             Graphics.TOP
             | Graphics.LEFT);
             displayRow++;
             
             if(controller.getStatusCode() == 1){
                  g.drawString("GPS : ON", getWidth() - 10, fontHeight * displayRow,
             Graphics.BOTTOM
             | Graphics.RIGHT); 
             }else
              if(controller.getStatusCode() == 0){
                  g.drawString("GPS : OFF", getWidth() - 10, fontHeight * displayRow,
             Graphics.BOTTOM
             | Graphics.RIGHT); 
             }
             
             

  
            long secondsSinceLastPosition = -1;
         /*   if (lastPosition.date != null) {
                secondsSinceLastPosition = (now.getTime() - lastPosition.date
                        .getTime()) / 1000;
            }*/

            if (secondsSinceLastPosition > 5) {
                String timeSinceLastPosition;
                if (secondsSinceLastPosition > 60) {
                    /*
                     * If it's been more than a minute, we should just give a
                     * rough estimate since last refresh to the second if under
                     * an hour, to the minute if under a day to the hour if over
                     * a day.
                     */

                    final long days = secondsSinceLastPosition / 86400;
                    secondsSinceLastPosition -= days * 86400;
                    final long hours = secondsSinceLastPosition / 3600;
                    secondsSinceLastPosition -= hours * 3600;
                    final long minutes = secondsSinceLastPosition / 60;
                    secondsSinceLastPosition -= minutes * 60;

                    if (days > 0) {
                        timeSinceLastPosition = days + " days " + hours
                                + " hours ";
                    } else if (hours > 0) {
                        timeSinceLastPosition = hours + " hours " + minutes
                                + " mins";
                    } else {
                        timeSinceLastPosition = minutes + " mins "
                                + secondsSinceLastPosition + " seconds";
                    }

                } else if (secondsSinceLastPosition == -1) {
                    timeSinceLastPosition = "No Time Info Available";
                } else {
                    timeSinceLastPosition = secondsSinceLastPosition
                            + " seconds";
                }

                g.drawString("Time from last fix:", 1, height
                        - (fontHeight * 4 + 6), Graphics.TOP | Graphics.LEFT);
                g.drawString(timeSinceLastPosition + " ago.", 1, height
                        - (fontHeight * 3 + 6), Graphics.TOP | Graphics.LEFT);

            }

        } else if (controller.getStatusCode() != Controller.STATUS_NOTCONNECTED) {
            g.drawString("No GPS fix. " + counter, 1, fontHeight, Graphics.TOP
                    | Graphics.LEFT);
        }

        /** Draw error texts */
        g.setColor(255, 0, 0);
        if (error != null) {
            g.drawString("" + error, 1, height - (fontHeight * 3 + 2),
                    Graphics.TOP | Graphics.LEFT);
        }
        if (controller.getError() != null) {
            g.drawString("" + controller.getError(), 1, height
                    - (fontHeight * 2 + 2), Graphics.TOP | Graphics.LEFT);
        }


    }

    public PointResultCanvas() {
    }

    
    
    public void keyPressed(int keyCode) {
        System.out.println("key=" + keyCode);

        /** Handle controller.globalZooming keys */
        switch (keyCode) {
            case (KEY_NUM1):
                if (controller.globalZoom < MAX_ZOOM) {
                    
                   if(controller.globalZoom == 17-11){
                        controller.globalZoom =17-14;
                    setLastPosition(lastPosition);
                    controller.globalZoom = controller.globalZoom;
                   }else
                   if(controller.globalZoom == 3) {
                        controller.globalZoom =1;
                        setLastPosition(lastPosition);
                    controller.globalZoom = controller.globalZoom;
                   }else
                     if(controller.globalZoom == 1) {
                         controller.globalZoom =1;
                        setLastPosition(lastPosition);
                    controller.globalZoom = controller.globalZoom;
                   }
                    
                }
                break;

            case (KEY_NUM3):
                if (controller.globalZoom > 0) {
                    // Zoom out
                  //  controller.globalZoom--;
                  //  // Calculate last position so that it recalculates the
                    // canvas positions.
                  //  setLastPosition(lastPosition);
                 //   controller.globalZoom = controller.globalZoom;
                    
                    if(controller.globalZoom == 6){
                        controller.globalZoom =6;
                    setLastPosition(lastPosition);
                    controller.globalZoom = controller.globalZoom;
                   }else
                   if(controller.globalZoom == 3) {
                        controller.globalZoom =6;
                        setLastPosition(lastPosition);
                    controller.globalZoom = controller.globalZoom;
                   }else
                     if(controller.globalZoom == 1) {
                         controller.globalZoom =3;
                        setLastPosition(lastPosition);
                    controller.globalZoom = controller.globalZoom;
                   }
                }
                break;

            case (' '):
            case (KEY_NUM0):
                // Change screen. Some phones 0 key defaults to space.
            //    controller.switchDisplay();
                break;
                
            case (KEY_STAR):
            case (KEY_POUND):
                Logger.debug("WaypointList getPosition called");
                GpsPosition lp = controller.getPosition();

                break;

            default:

        }

        /** Handle panning keys */
        int gameKey = -1;
        try {
            gameKey = getGameAction(keyCode);
        } catch (Exception ex) {
            /**
             * We don't need to handle this error. It is only caught because
             * getGameAction() method generates exceptions on some phones for
             * some buttons.
             */
        }
         int scale = (1 << (controller.globalZoom));
         int mv =20;
      
   
           
        if (gameKey == UP || keyCode == KEY_NUM2) {
            glbLatitude = lastPosition.latitude;
           
            
            ypoint -= mv * scale;
            
            lastPosition.latitude = getLatitude();
       
        }
        if (gameKey == DOWN || keyCode == KEY_NUM8) {
            glbLatitude = lastPosition.latitude;
               ypoint += mv * scale;
            lastPosition.latitude = getLatitude();
           
        }
        if (gameKey == LEFT || keyCode == KEY_NUM4) {
            glbLongitude = lastPosition.longitude;
             xpoint -= mv * scale;
           lastPosition.longitude = getLongitude();
          
        }
        if (gameKey == RIGHT || keyCode == KEY_NUM6) {
            glbLongitude = lastPosition.longitude;
            xpoint += mv * scale;
           lastPosition.longitude = getLongitude();
          // getCourseToTarget();

            
           }
        if (gameKey == FIRE || keyCode == KEY_NUM5) {
            verticalMovement = 0;
            horizontalMovement = 0;
        }
        this.repaint();
    }
    
    public void SetDirectionEndPoint(Graphics g)
    {
        g.drawImage(CenterPointer, midWidth, midHeight
                   , Graphics.VCENTER | Graphics.HCENTER);
    }
    


   public void keyRepeated (int keyCode) {

   // keyPressed( keyCode);
 //  repaint ();
  }
   
 
   public void drawSomethingPanning(Graphics g)
 {
     int mzpZoom =17-controller.globalZoom;
       int maxtiles = (int) MathUtil.pow(2, controller.globalZoom);
      int[] pt = MapLocator.conv(getLatitude(),getLongitude(), mzpZoom);
      
      
                           

                    if (pt[0] == 0 && mzpZoom != 1) {
                        pt[0] = maxtiles;
                    } else {
                        pt[0] = pt[0] - 1;
                    }


                    if (pt[1] == 0 && mzpZoom != 1) {
                        pt[1] = maxtiles;
                    } else {
                        pt[1] = pt[1] - 1;
                    }

                    try {
    
                            mapTiles[4] = tileDownloader.fetchTile(pt[0] + m[4],
                                pt[1] + n[4], mzpZoom, false);
                        mapTiles[1] = tileDownloader.fetchTile(pt[0] + m[1],
                                pt[1] + n[1], mzpZoom, false);
                        mapTiles[3] = tileDownloader.fetchTile(pt[0] + m[3],
                               pt[1] + n[3], mzpZoom, false);
                        mapTiles[5] = tileDownloader.fetchTile(pt[0] + m[5],
                                pt[1] + n[5], mzpZoom, false);
                        mapTiles[7] = tileDownloader.fetchTile(pt[0] + m[7],
                                pt[1] + n[7], mzpZoom, false);
                        mapTiles[0] = tileDownloader.fetchTile(pt[0] + m[0],
                                pt[1] + n[0], mzpZoom, true);
                        mapTiles[2] = tileDownloader.fetchTile(pt[0] + m[2],
                                pt[1] + n[2], mzpZoom, false);
                        mapTiles[6] = tileDownloader.fetchTile(pt[0] + m[6],
                                pt[1] + n[6], mzpZoom, false);
                        mapTiles[8] = tileDownloader.fetchTile(pt[0] + m[8],
                                pt[1] + n[8], mzpZoom, false);
                    
                                       
                    } catch (Exception e) {
                        e.printStackTrace();


                    }

          
       g.drawImage(mapTiles[0], midWidth - pt[2]
                            + horizontalMovement - TileDownloader.TILE_SIZE,
                            midHeight - pt[3] + verticalMovement
                                    - TileDownloader.TILE_SIZE, Graphics.TOP
                                    | Graphics.LEFT);
                    g.drawImage(mapTiles[1], midWidth - pt[2]
                            + horizontalMovement, midHeight - pt[3]
                            + verticalMovement - TileDownloader.TILE_SIZE,
                            Graphics.TOP | Graphics.LEFT);
                    g.drawImage(mapTiles[2], midWidth - pt[2]
                            + horizontalMovement + TileDownloader.TILE_SIZE,
                            midHeight - pt[3] + verticalMovement
                                    - TileDownloader.TILE_SIZE, Graphics.TOP
                                    | Graphics.LEFT);

                    g.drawImage(mapTiles[3], midWidth - pt[2]
                            + horizontalMovement - TileDownloader.TILE_SIZE,
                            midHeight - pt[3] + verticalMovement, Graphics.TOP
                                    | Graphics.LEFT);
                    g.drawImage(mapTiles[4], midWidth - pt[2]
                            + horizontalMovement, midHeight - pt[3]
                            + verticalMovement, Graphics.TOP | Graphics.LEFT);
                    g.drawImage(mapTiles[5], midWidth - pt[2]
                            + horizontalMovement + TileDownloader.TILE_SIZE,
                            midHeight - pt[3] + verticalMovement, Graphics.TOP
                                    | Graphics.LEFT);

                    g.drawImage(mapTiles[6], midWidth - pt[2]
                            + horizontalMovement - TileDownloader.TILE_SIZE,
                            midHeight - pt[3] + verticalMovement
                                    + TileDownloader.TILE_SIZE, Graphics.TOP
                                    | Graphics.LEFT);
                    g.drawImage(mapTiles[7], midWidth - pt[2]
                            + horizontalMovement, midHeight - pt[3]
                            + verticalMovement + TileDownloader.TILE_SIZE,
                            Graphics.TOP | Graphics.LEFT);
                    g.drawImage(mapTiles[8], midWidth - pt[2]
                            + horizontalMovement + TileDownloader.TILE_SIZE,
                            midHeight - pt[3] + verticalMovement
                                    + TileDownloader.TILE_SIZE, Graphics.TOP
                                    | Graphics.LEFT);
    
  
 }
   
    public float getLongitude()
    {
        return convertToLongitude(xpoint);
    }

    public static float convertToLongitude(int xpoint)
    {
        double d1 = ((float)xpoint * 180F) / 1.677722E+007F - 180F;
        return (float)d1;
    }

    public float getLatitude()
    {
        return convertToLatitude(ypoint);
    }
    
    
    
        public static float convertToLatitude(int i1)
    {
        double d2 = -5340353.7154408721D;
       
        double d1 = Float11.exp((double)((float)i1 - 1.677722E+007F) / d2);
        d1 = Math.toDegrees(Float11.atan(d1) * 2D - 1.5707963267948966D);

        String lat = new Float(d1).toString();
        return (float)d1;
    }
        
            public void gotoLonLat(float f1, float f2, int i1, boolean flag)
    {
   
          //  System.out.println("Goto  lon:" + f1 + " lat:" + f2);
        
        int j1 = xpoint;
        int k1 = ypoint;
        int l1 = 1 << i1;
        xpoint = xFromLon(f1);
        ypoint = yFromLat(f2);
      //  System.out.println( " the changed xpoint for the gotot "+xpoint +"     "+ypoint);
        glbLatitude =lastPosition.latitude;
        glbLongitude = lastPosition.longitude;
          lastPosition.longitude = Double.parseDouble(StringUtil.valueOf(getLongitude(), 5));
                  lastPosition.latitude = Double.parseDouble(StringUtil.valueOf(getLatitude(), 5));
                  
                  System.out.println("Goto Lat Lon"+lastPosition.latitude +"  "+lastPosition.longitude);
        controller.globalZoom = i1;
        
        if(Math.abs((j1 - xpoint) / l1) > getWidth() || Math.abs((k1 - ypoint) / l1) > screenHeight)
        {
            //BufferImage.cleanBuffer();
        }
    }
     
     

    public static int xFromLon(float f1)
    {
        float f2 = (1.677722E+007F * (f1 + 180F)) / 180F;
        return (int)f2;
    }

    public static int yFromLat(float f1)
    {
        if(f1 < -89F)
        {
            f1 = -89F;
        }
        if(f1 > 89F)
        {
            f1 = 89F;
        }
        double d2 = Math.sin(Math.toRadians(f1));
        double d3 = -Float11.log((1.0D + d2) / (1.0D - d2)) / 6.2831853071795862D;
        double d1 = (1.0D + d3) * 16777216D;
        return (int)d1;
    }
   
//    public double getCourseToTarget()
//    {
//        double d1 = (0.0D / 0.0D);
//        {
//            int i1 = xpoint;
//            int j1 = ypoint;
//            if(lastPosition != null)
//            {
//                i1 = xFromLon((float)lastPosition.longitude);
//                j1 = yFromLat((float)lastPosition.latitude);
//                
//            }
//            float f1 = -(float)(yFromLat((float)lastPosition.latitude) - xFromLon((float)glbLatitude));
//            float f2 = -(float)(xFromLon((float)lastPosition.longitude) - xFromLon((float)glbLongitude));
//            double d2 = Math.sqrt(f2 * f2 + f1 * f1);
//            if(d2 != 0.0D)
//            {
//                d1 = -Float11.asin((double)f2 / d2);
//                if(f1 < 0.0F)
//                {
//                    d1 = 3.1415926535897931D - d1;
//                }
//                d1 = Math.toDegrees(d1);
//            }
//        }
//        return d1;
//    }
                                                               
                                                      

    public LocationPointer pointSelectedMarks(Graphics g1, int xpoint, int ypoint, int zoom, int mvDist)
    {
         LocationPointer locPtr=null;
         if(currentSel != null)
        {
            int dist = currentSel.distFrom(xpoint, ypoint, zoom);
            if(dist < mvDist)
            {
                locPtr = currentSel;
                mvDist = dist;
            }
        }
    
       
        try{
            if(m_listMyPlaces!=null){
        
                Enumeration enumeration1 = m_listMyPlaces.elements();
       
          do
        {
            if(!enumeration1.hasMoreElements())
            {
                break;
            }
            LocationPointer lcptr = (LocationPointer)enumeration1.nextElement();
            if(lcptr.m_theListener == null)
            {
                   // oneloc1.setListener(this);
            }
            if(g1 != null)
            {
                lcptr.paint(g1, xpoint, ypoint, X0Point, Y0Point, screenWidth, screenHeight, controller.globalZoom,true);
              
            }
        } while(true);
            }
        }catch(Exception e){System.out.println("removig search");}
       
  
        try{
            if(busStagesPoints !=null){
       Enumeration busStages = busStagesPoints.elements();
       
          do
        {
            if(!busStages.hasMoreElements())
            {
                break;
            }
            LocationPointer bsStageptr = (LocationPointer)busStages.nextElement();
            if(bsStageptr.m_theListener == null)
            {
             //   oneloc1.setListener(this);
            }
            if(g1 != null)
            {
                bsStageptr.paintBusStage(g1, xpoint, ypoint, X0Point, Y0Point, screenWidth, screenHeight, controller.globalZoom,true);
            }
          } while(true);
            }
        }catch(Exception e){System.out.println("In bus clerarence");}
       
        int [] k={0,0};
    
        try{
        if(controller.route !=null)
        {
            int chk=0;
         Enumeration route = controller.route.elements();
        
          do
        {
              LocationPointer rotPtr=null;
            if(!route.hasMoreElements())
            {
                
                break;
            }
            rotPtr = (LocationPointer)route.nextElement();
            if(rotPtr.m_theListener == null)
            {
                //oneloc1.setListener(this);
            }
            if(g1 != null)
            {
                if(chk ==0)
                {
               k =  rotPtr.paint1(g1, xpoint, ypoint, X0Point, Y0Point, screenWidth, screenHeight, controller.globalZoom,0,k[0],k[1]);
               rotPtr.paint(g1, xpoint, ypoint, X0Point, Y0Point, screenWidth, screenHeight, controller.globalZoom,false);
                }
                else{
                    
                    
                     k =  rotPtr.paint1(g1, xpoint, ypoint, X0Point, Y0Point, screenWidth, screenHeight, controller.globalZoom,1,k[0],k[1]);
                     
                     
                     
                }
                
                  
            
            }
            
            chk++;
       } while(true);
        
         LocationPointer rotPtr = (LocationPointer)controller.route.lastElement();
                 k =  rotPtr.paint1(g1, xpoint, ypoint, X0Point, Y0Point, screenWidth, screenHeight, controller.globalZoom,1,k[0],k[1]);
                  rotPtr.paint(g1, xpoint, ypoint, X0Point, Y0Point, screenWidth, screenHeight, controller.globalZoom,false);
            if (infoCounter ==0){
                new Thread(){
                    public void run(){
                          controller.showInfo(controller.InfoDisplay);
                    }
                }.start();
               
                
            }
                             
         }
        }catch(Exception e){System.out.println ("Error "+e);}
        
        if(GPSLatitude != 0.0){
        
        if( controller.getUseJsr179() ){
            LocationPointer gpsLoc = new LocationPointer("",(float)GPSLatitude,(float) GPSLongitude,controller.globalZoom,true);
 
       int gpsPt [] =gpsLoc.calcXY(xpoint, ypoint, X0Point, Y0Point, screenWidth, screenHeight, controller.globalZoom);
       g1.drawImage(gpsDot, gpsPt[0], gpsPt[1]
                   , Graphics.VCENTER | Graphics.HCENTER);
       if(gpsPt.length !=0){
           this.addCommand(this.ShowCurrentPoint);
           this.setCommandListener(this);
       }
       else{
           this.removeCommand(this.ShowCurrentPoint);
       }
        
       
        }else{
            LocationPointer gpsLoc = new LocationPointer("",(float)GPSLongitude,(float)GPSLatitude,controller.globalZoom,true);
        
       int gpsPt [] =gpsLoc.calcXY(xpoint, ypoint, X0Point, Y0Point, screenWidth, screenHeight, controller.globalZoom);
       System.out.println("----------------"+gpsPt[0]+" "+gpsPt[1]);
             compassArrows.setPosition(gpsPt[0],gpsPt[1]);
       compassArrows.paint(g1);
        if(gpsPt.length !=0){
           this.addCommand(this.ShowCurrentPoint);
           this.setCommandListener(this);
       }
       else{
           this.removeCommand(this.ShowCurrentPoint);
       }
        }
        }
        
  
           try{
            if(this.mapIdName !=null){
              
                Enumeration enumeration1 =mapIdName.elements();
       
          do
        {
            if(!enumeration1.hasMoreElements())
            {
                break;
            }
             lcptr = (LocationPointer)enumeration1.nextElement();
            if(lcptr.m_theListener == null)
            {
                   // oneloc1.setListener(this);
            }
            if(g1 != null)
            {
                lcptr.pointIdMarkers(g1, xpoint, ypoint, X0Point, Y0Point, screenWidth, screenHeight, controller.globalZoom);
               
             if(dispCounter < 10)   {
        lcptr.paintFull(gPoint, xpoint, ypoint, X0Point, Y0Point, screenWidth, screenHeight, controller.globalZoom);
        
             }
                
                
                
            }
        } while(true);
            }
        }catch(Exception e){System.out.println("removig search");}

        // Bus position
        
         try{
            if(this.BusPosition !=null){
                
                Enumeration enumeration1 =BusPosition.elements();
       
          do
        {
            if(!enumeration1.hasMoreElements())
            {
                break;
            }
             lcptr = (LocationPointer)enumeration1.nextElement();
            if(lcptr.m_theListener == null)
            {
                   // oneloc1.setListener(this);
            }
            if(g1 != null)
            {
                lcptr.pointBusPosition(g1, xpoint, ypoint, X0Point, Y0Point, screenWidth, screenHeight, controller.globalZoom);
               
             if(dispCounter < 10)   {
        lcptr.paintFull(gPoint, xpoint, ypoint, X0Point, Y0Point, screenWidth, screenHeight, controller.globalZoom);
        
             }
                
                
                
            }
        } while(true);
            }
        }catch(Exception e){System.out.println("Problem in displaying bus position"+e);}

        // For Traffic Spots
        
           try{
            if(TrafficPoints !=null){
       Enumeration traPts = TrafficPoints.elements();
       
          do
        {
            if(!traPts.hasMoreElements())
            {
                break;
            }
            LocationPointer tPtrs = (LocationPointer)traPts.nextElement();
            if(tPtrs.m_theListener == null)
            {
             //   oneloc1.setListener(this);
            }
            if(g1 != null)
            {
                tPtrs.TrafficSpots(g1, xpoint, ypoint, X0Point, Y0Point, screenWidth, screenHeight, controller.globalZoom);
            }
          } while(true);
            }
        }catch(Exception e){System.out.println("In bus clerarence");}
        
        if(  TrafficPoints.isEmpty() == false || mapIdName.isEmpty() == false  || m_listMyPlaces.isEmpty() == false || busStagesPoints.isEmpty() == false  || controller.route != null  ){
            this.addCommand(clearMap);
            //this.setCommandListener(this);
            
        }
        else{
            this.removeCommand(clearMap);
        }
        
        return locPtr;
    }
    
      
         public void run()
    {
             repaint();
         for(w = true; w;)
        {
            try
            {
                Thread.sleep(100L);
                if(O != 0L && System.currentTimeMillis() - O > 1000L)
                {
                    O = 0L;
                    repaint();
                 }
          
            }
            catch(Exception exception)
            {
                exception.printStackTrace();
            }
        }

    }
   private class DispTimerTask extends TimerTask
  {
    public final void run()
    {
     dispCounter +=1;
     infoCounter -=1;
    // System.out.println("-----------------------------------Counter value "+dispCounter);
    }
  }         
     
        
}
    
