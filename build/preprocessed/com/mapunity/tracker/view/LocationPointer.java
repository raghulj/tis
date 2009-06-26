/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mapunity.tracker.view;

/**
 *
 * @author raghul
 */



import java.util.Hashtable;

import javax.microedition.lcdui.*;
import com.mapunity.util.*;





public class LocationPointer
    implements Runnable
{

    public float m_lon;
    public float m_lat;
    public int px;
    public int py;
    public int i;
    public int j;
    public static int m_defaultWidth = 90;
    public static int m_defaultHeight = 80;
    public int k;
    public int l;
    public String name;
    public String description;
    public String phoneNum;
    public String address;
    public String iconName;
    public Image iconImage;
    public Image Idmarker;
    public String imageName;
    public LocationListener m_theListener;
    public int zoom;
    public String URL;
    public boolean isModeSat;
    public int ima_offx;
    public int ima_offy;
    public int m_category;
    public int m_type;
    public int m_col;
    public int m_colForeground;
    private static int a = 0;
    private boolean b;
    public Hashtable m;
    Image ff;
    public int xpoint,ypoint;
  

    public LocationPointer()
    {
        i = 90;
        j = 80;
        k = 15;
        l = 30;
        name = "";
        description = "";
        phoneNum = null;
        address = "";
        iconName = null;
        iconImage = null;
        imageName = null;
        m_theListener = null;
        zoom = 1;
        URL = null;
        isModeSat = false;
        ima_offx = 0;
        ima_offy = 0;
        m_category = 0;
        m_type = 0;
        m_col = 0xff8080;
        m_colForeground = 0xffffff;
        b = false;
        m = null;
  
        
    }

    public LocationPointer(String s, float f, float f1, int xpoint, boolean flag)
    {
        i = 90;
        j = 80;
        k = 15;
        l = 30;
        name = "";
        description = "";
        phoneNum = null;
        address = "";
        iconName = null;
        iconImage = null;
        imageName = null;
        m_theListener = null;
        zoom = 1;
        URL = null;
        isModeSat = false;
        ima_offx = 0;
        ima_offy = 0;
        m_category = 0;
        m_type = 4;
        m_col = 0xff8080;
        m_colForeground = 0xffffff;
        b = false;
        m = null;
        init(s, f, f1, xpoint, flag);
    }
     public static int distFrom(int i1, int j1, int k1, int l1, int i2)
    {
        long l2 = (i1 - k1) / (1 << i2);
        long l3 = (j1 - l1) / (1 << i2);
        float f = l2 * l2 + l3 * l3;
        int j2 = (int)Math.sqrt(f);
        return j2;
    }

       public int distFrom(int i1, int j1, int k1)
    {
        return distFrom(px, py, i1, j1, k1);
    }

    public LocationPointer(float f, float f1)
    {
    }

    public void init(String s, float f, float f1, int xpoint, boolean flag)
    {
        name = s;
        m_lon = f;
        m_lat = f1;
        validate();
        zoom = xpoint;
        isModeSat = flag;
    }

   

  
    public void paint(Graphics g, int xpoint, int j1, int k1, int l1, int i2, int j2, 
            int zoom,boolean image)
    {
       
         try{
           iconImage = Image.createImage("/images/mark.png");
           }catch(Exception e){
         System.out.println("tryint to paint image" +e);}
        if(m_type == 2)
        {
            System.out.println("i am out");
            return;
        }
        int l2 = k1 + i2 / 2 + (px - xpoint) / (1 << zoom);
        int i3 = l1 + j2 / 2 + (py - j1) / (1 << zoom);
         this.xpoint = l2;
        this.ypoint = i3;
       if(l2 > -i && i3 > -j && l2 < i2 + k1 && i3 < j2 + l1)
        {
            if(image == false)
            {
                int j3 = l2 - i2 / 2;
                if(j3 < 0)
                {
                    j3 = -j3;
                }
                int k3 = i3 - j2 / 2;
                if(k3 < 0)
                {
                    k3 = -k3;
                }
                g.setColor(0x3b69de);
                switch(0)
                {
                case 0: // '\0'
                case 2: // '\002'
                default:
                   g.setColor(0x3b69de);
                    g.fillTriangle(l2, i3,l2+6, i3-12, l2-6, i3-12);
                    g.setColor(0);
                    g.drawLine(l2, i3, l2+6,i3-12);
                    g.drawLine(l2, i3, l2-6, i3-12);
                    g.drawLine(l2+6, i3-12, l2-6, i3-12);
                    
                    break;

                case 1: // '\001'
                    System.out.println("in 1 pa");
                    g.fillArc(l2 + 10, i3 - 16, 8, 8, 0, 360);
                    g.setColor(0);
                    g.drawLine(l2, i3, l2 + 10, i3 - 10);
                    g.drawArc(l2 + 10, i3 - 16, 7, 7, 0, 360);
                    break;

                case 3: // '\003'
                    System.out.println("in 3 pa");
                    g.fillTriangle(l2 - 5, i3, l2, i3 - 5, l2, i3 + 5);
                    g.fillTriangle(l2 + 5, i3, l2, i3 - 5, l2, i3 + 5);
                    g.setColor(0);
                    g.drawLine(l2 - 5, i3, l2, i3 - 5);
                    g.drawLine(l2, i3 - 5, l2 + 5, i3);
                    g.drawLine(l2 + 5, i3, l2, i3 + 5);
                    g.drawLine(l2, i3 + 5, l2 - 5, i3);
                    break;

                case 4: // '\004'
                    System.out.println("in 4 pa");
                    g.fillArc(l2 - 5, i3 - 5, 10, 10, 0, 360);
                    g.setColor(0);
                    g.drawArc(l2 - 5, i3 - 5, 10, 10, 0, 360);
                    break;
                }
            } else
            if(m_type != 5)
            {
                g.drawImage(iconImage, l2 - iconImage.getWidth() / 2, i3 - 10,36);
            } else
            {
                g.drawImage(iconImage, l2 + ima_offx, i3 + ima_offy, Graphics.TOP);
            }
        }
       
            Thread thread = new Thread(this);
            thread.start();
       
    }
    
     public void paintBusStage(Graphics g, int xpoint, int j1, int k1, int l1, int i2, int j2, 
            int zoom,boolean image)
    {
         try{
           iconImage = Image.createImage("/images/mark.png");
           }catch(Exception e){
         System.out.println("tryint to paint image" +e);}
  
        if(m_type == 2)
        {
            System.out.println("i am out");
            return;
        }
        int l2 = k1 + i2 / 2 + (px - xpoint) / (1 << zoom);
           int i3 = l1 + j2 / 2 + (py - j1) / (1 << zoom);
        if(l2 > -i && i3 > -j && l2 < i2 + k1 && i3 < j2 + l1)
        {
            
            if(image != false)
            {
                int j3 = l2 - i2 / 2;
                if(j3 < 0)
                {
                    j3 = -j3;
                }
                int k3 = i3 - j2 / 2;
                if(k3 < 0)
                {
                    k3 = -k3;
                }
                g.setColor(0x3b69de);
                switch(0)
                {
                case 0: // '\0'
                case 2: // '\002'
                default:
                   // System.out.println("in default pa");
                    g.drawLine(l2, i3, l2+6,i3-12);
                    g.drawLine(l2, i3, l2-6, i3-12);
                    g.drawLine(l2-6, i3-12, l2, i3-15);
                    g.drawLine(l2+6, i3-12, l2, i3-15);
                    g.setColor(0xf31111);
                    g.fillTriangle(l2, i3, l2+6, i3-12, l2-6, i3-12);
                    g.fillTriangle(l2+6, i3-12, l2-6, i3-12,l2, i3-15);
                    
                    break;

                case 1: // '\001'
                    System.out.println("in 1 pa");
                    g.fillArc(l2 + 10, i3 - 16, 8, 8, 0, 360);
                    g.setColor(0);
                    g.drawLine(l2, i3, l2 + 10, i3 - 10);
                    g.drawArc(l2 + 10, i3 - 16, 7, 7, 0, 360);
                    break;

                case 3: // '\003'
                    System.out.println("in 3 pa");
                    g.fillTriangle(l2 - 5, i3, l2, i3 - 5, l2, i3 + 5);
                    g.fillTriangle(l2 + 5, i3, l2, i3 - 5, l2, i3 + 5);
                    g.setColor(0);
                    g.drawLine(l2 - 5, i3, l2, i3 - 5);
                    g.drawLine(l2, i3 - 5, l2 + 5, i3);
                    g.drawLine(l2 + 5, i3, l2, i3 + 5);
                    g.drawLine(l2, i3 + 5, l2 - 5, i3);
                    break;

                case 4: // '\004'
                    System.out.println("in 4 pa");
                    g.fillArc(l2 - 5, i3 - 5, 10, 10, 0, 360);
                    g.setColor(0);
                    g.drawArc(l2 - 5, i3 - 5, 10, 10, 0, 360);
                    break;
                }
            } else
            if(m_type != 5)
            {
                g.drawImage(iconImage, l2 - iconImage.getWidth() / 2, i3 - 10,36);
            } else
            {
                g.drawImage(iconImage, l2 + ima_offx, i3 + ima_offy, Graphics.TOP);
            }
        }

            Thread thread = new Thread(this);
            thread.start();
      
    }



    public void paintFull(Graphics g, int xpoint, int j1, int k1, int l1, int i2, int j2, 
            int zoom)
    {
        int l2 = k1 + i2 / 2 + (px - xpoint) / (1 << zoom);
        int i3 = l1 + j2 / 2 + (py - j1) / (1 << zoom);
        int j3 = l2 - i2 / 2;
        if(j3 < 0)
        {
            j3 = -j3;
        }
        int k3 = i3 - j2 / 2;
        if(k3 < 0)
        {
            k3 = -k3;
        }
        g.setColor(0xff8080);
        g.setColor(0xffffff);
        int l3 = k;
       // g.setFont(UtilMidp.m_smallFont);
        int i4 = g.getFont().getHeight();
        if(description.equals("") && address.equals("") && name.equals(""))
        {
            if(iconImage != null)
            {
                k = iconImage.getHeight() - ima_offy;
                l += ima_offx;
                int j4 = m_colForeground;
                m_colForeground = 0xff0000;
                paint(g, xpoint, j1, k1, l1, i2, j2, zoom,false);
                m_colForeground = j4;
            } else
            {
                int k4 = m_col;
                m_col = 0xff0000;
                paint(g, xpoint, j1, k1, l1, i2, j2, zoom,false);
                m_col = k4;
            }
            return;
        }
        if(imageName == null)
        {
      
        }
        Image image = null;
        if(imageName != null)
        {
      
        }
        description = "test";
        address = "TEST" ;
        String s = name ;
        g.fillRoundRect(l2 - l, i3 - (j + k), i, j, 10, 10);
        g.fillTriangle(l2 + 8, i3 - 8, l2 + 10, i3 - k, l2 + 15, i3 - k);
        g.setColor(0);
        g.drawRoundRect(l2 - l, i3 - (j + k), i, j, 10, 10);
        g.drawLine(l2 + 8, i3 - 8, l2 + 10, i3 - k);
        g.drawLine(l2 + 8, i3 - 8, l2 + 15, i3 - k);
        int l4 = (l2 - l) + 4;
        int i5 = (i3 - (k + j)) + 2;
        if(image != null)
        {
            g.drawImage(image, l4, i5, 20);
            i5 += image.getHeight();
        }
        int j5 = (i - 2) / 5;
        Font font = g.getFont();
        do
        {
            s = s.trim();
            int k5 = s.indexOf('\n');
            if(k5 == -1)
            {
                k5 = s.length();
            }
            if(font.substringWidth(s, 0, k5) > i)
            {
                int l5;
                for(l5 = 0; l5 + 1 < s.length() && font.substringWidth(s, 0, l5 + 1) < i; l5++) { }
                k5 = l5 - 1;
                char c1;
                do
                {
                    c1 = s.charAt(l5--);
                } while(c1 != '\n' && c1 != ' ' && c1 != '-' && c1 != '.' && l5 > 0);
                if(l5 != 0)
                {
                    k5 = l5 + 1;
                }
            }
            g.drawString(s.substring(0, k5), l4, i5, 20);
            s = s.substring(k5);
            i5 += i4;
        } while(s.length() > 0 && i5 + i4 < i3 - k);
        g.setColor(0xffffff);
        g.drawLine(l2 + 10, i3 - k, l2 + 15, i3 - k);
        k = l3;
    }

    public void run()
    {
      //temp for now
    }

    public boolean validate()
    {
        px = xFromLon(m_lon);
        py = yFromLat(m_lat);
        i = m_defaultWidth;
        j = m_defaultHeight;
        return true;
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
        double d3 = -Float11.log((1.0D + d2) / (1.0D - d2)) / 6.2831853071795862D;// (22/7)*2
        double d1 = (1.0D + d3) * 16777216D;
        return (int)d1;
    }

    public static float convLon(int xpoint)
    {
        double d1 = ((float)xpoint * 180F) / 1.677722E+007F - 180F;
        return (float)d1;
    }
       public static float convLat(int xpoint)
    {
        double d2 = -5340353.7154408721D;
        
        double d1 = Float11.exp((double)((float)xpoint - 1.677722E+007F) / d2);
        d1 = Math.toDegrees(Float11.atan(d1) * 2D - 1.5707963267948966D);
        return (float)d1;
    }
       
   public int [] paint1(Graphics g, int xpoint, int j1, int k1, int l1, int i2, int j2, 
            int zoom,int flag,int ol1,int ol2)
    {
        
         
        int l2 = k1 + i2 / 2 + (px - xpoint) / (1 << zoom);
        int i3 = l1 + j2 / 2 + (py - j1) / (1 << zoom);
        if (flag == 0)
        {
            int [] a ={l2,i3};
            return a;
        }
        
        g.setColor(0x3b69de);
         g.drawLine(ol1, ol2, l2, i3);
         g.drawLine(ol1, ol2+1, l2, i3+1);
         g.drawLine(ol1, ol2+2, l2, i3+2);
      
         if(iconImage == null && iconName != null && !b)
        {
            Thread thread = new Thread(this);
            thread.start();
        }
        int [] a ={l2,i3};
        return a;
          }

    public void paintGPS(Graphics g, int xpoint, int j1, int k1, int l1, int i2, int j2, 
            int zoom,int GPSIndex)
    {
        if(m_type == 2)
        {
            System.out.println("i am out");
            return;
        }
        int l2 = k1 + i2 / 2 + (px - xpoint) / (1 << zoom);
        int i3 = l1 + j2 / 2 + (py - j1) / (1 << zoom);
      
       if(l2 > -i && i3 > -j && l2 < i2 + k1 && i3 < j2 + l1)
        {
           
            if(iconImage != null)
            {
                int j3 = l2 - i2 / 2;
                if(j3 < 0)
                {
                    j3 = -j3;
                }
                int k3 = i3 - j2 / 2;
                if(k3 < 0)
                {
                    k3 = -k3;
                }
                g.setColor(0x22f9ec);
                switch(4)
                {
                case 0: // '\0'
                case 2: // '\002'
                default:
               break;

                case 1: // '\001'
                    g.fillArc(l2 + 10, i3 - 16, 8, 8, 0, 360);
                    g.setColor(0);
                    g.drawLine(l2, i3, l2 + 10, i3 - 10);
                    g.drawArc(l2 + 10, i3 - 16, 7, 7, 0, 360);
                    break;

                case 3: // '\003'
                    break;

                case 4: // '\004'
                    break;
                }
            } else
            if(m_type != 5)
            {
                g.setColor(0);
                g.fillRoundRect(l2 - iconImage.getWidth() / 2 - 2, i3 - 10 - (iconImage.getHeight() + 2), iconImage.getWidth() + 4, iconImage.getHeight() + 4, 6, 6);
                g.drawImage(iconImage, l2 - iconImage.getWidth() / 2, i3 - 10,36);
            } else
            {
                g.drawImage(iconImage, l2 + ima_offx, i3 + ima_offy, Graphics.TOP);
            }
        }
       
            Thread thread = new Thread(this);
            thread.start();
        
    }
    
    
    public void pointIdMarkers(Graphics g, int xpoint, int j1, int k1, int l1, int i2, int j2, 
            int zoom)
    {
               
        if(m_type == 2)
        {
            System.out.println("i am out");
            return;
        }
        int l2 = k1 + i2 / 2 + (px - xpoint) / (1 << zoom);
        int i3 = l1 + j2 / 2 + (py - j1) / (1 << zoom);
    
       if(l2 > -i && i3 > -j && l2 < i2 + k1 && i3 < j2 + l1)
        {
                    g.setColor(0xffede);
                    g.fillTriangle(l2, i3, l2+10, i3-10, l2+12,i3-8);
                    g.fillArc(l2 + 10, i3 - 16, 8, 8, 0, 360);
                    g.setColor(0);
                    g.drawLine(l2, i3, l2 + 10, i3 - 10);
                    g.drawLine(l2, i3, l2 + 12, i3 - 8);
                    g.drawArc(l2 + 10, i3 - 16, 7, 7, 0, 360);
        
        }
       
            Thread thread = new Thread(this);
            thread.start();
        
    }
    
     public void TrafficSpots(Graphics g, int xpoint, int j1, int k1, int l1, int i2, int j2, 
            int zoom)
    {
               
        if(m_type == 2)
        {
            System.out.println("i am out");
            return;
        }
        int l2 = k1 + i2 / 2 + (px - xpoint) / (1 << zoom);
        int i3 = l1 + j2 / 2 + (py - j1) / (1 << zoom);
    
       if(l2 > -i && i3 > -j && l2 < i2 + k1 && i3 < j2 + l1)
        {
           
                  if(name.equals("Smooth")){
                      g.setColor(0x2cb604);
                  }else if(name.equals("Slow")){
                      g.setColor(0xfcd26c);
                  }else if(name.equals("Delay")){
                      g.setColor(0xf60e13);
                  }
                   
                    g.fillArc(l2 + 10, i3 - 16, 6, 6, 0, 360);
                    g.setColor(0);
                    g.drawArc(l2 + 10, i3 - 16, 5, 5, 0, 360);
        
        }
       
            Thread thread = new Thread(this);
            thread.start();
        
    }
    
    public int [] calcXY(int xpoint, int ypoint, int X0Point, int Y0Point, int screenWidth, int screenHeight, 
            int zoom)
    {
        int xpt = X0Point + screenWidth / 2 + (px - xpoint) / (1 << zoom);
        
        int ypt = Y0Point + screenHeight / 2 + (py - ypoint) / (1 << zoom);
        int a []={xpt,ypt};
        return a;
    }
    
    public void dispDialog(Graphics g, String info,int xpoint, int j1, int k1, int l1, int i2, int j2,  int zoom)
    {
            int l2 = k1 + i2 / 2 + (px - xpoint) / (1 << zoom);
        int i3 = l1 + j2 / 2 + (py - j1) / (1 << zoom);
        int j3 = l2 - i2 / 2;
        if(j3 < 0)
        {
            j3 = -j3;
        }
        int k3 = i3 - j2 / 2;
        if(k3 < 0)
        {
            k3 = -k3;
        }
        g.setColor(0xff8080);
        g.setColor(0xffffff);
        int l3 = k;
  
        int i4 = g.getFont().getHeight();
        if(description.equals("") && address.equals("") && name.equals(""))
        {
            if(iconImage != null)
            {
                k = iconImage.getHeight() - ima_offy;
                l += ima_offx;
                int j4 = m_colForeground;
                m_colForeground = 0xff0000;
                paint(g, xpoint, j1, k1, l1, i2, j2, zoom,false);
                m_colForeground = j4;
            } else
            {
                int k4 = m_col;
                m_col = 0xff0000;
                paint(g, xpoint, j1, k1, l1, i2, j2, zoom,false);
                m_col = k4;
            }
            return;
        }
        if(imageName == null)
        {
      
        }
        Image image = null;
        if(imageName != null) 
        {
        }
        description = info;
        address = "TEST" ;
        String s = name ;
        g.fillRoundRect(l2 - l, i3 - (j + k), i, j, 10, 10);
        g.fillTriangle(l2 + 8, i3 - 8, l2 + 10, i3 - k, l2 + 15, i3 - k);
        g.setColor(0);
        g.drawRoundRect(l2 - l, i3 - (j + k), i, j, 10, 10);
        g.drawLine(l2 + 8, i3 - 8, l2 + 10, i3 - k);
        g.drawLine(l2 + 8, i3 - 8, l2 + 15, i3 - k);
        int l4 = (l2 - l) + 4;
        int i5 = (i3 - (k + j)) + 2;
        if(image != null)
        {
            g.drawImage(image, l4, i5, 20);
            i5 += image.getHeight();
        }
        int j5 = (i - 2) / 5;
        Font font = g.getFont();
        do
        {
            s = s.trim();
            int k5 = s.indexOf('\n');
            if(k5 == -1)
            {
                k5 = s.length();
            }
            if(font.substringWidth(s, 0, k5) > i)
            {
                int l5;
                for(l5 = 0; l5 + 1 < s.length() && font.substringWidth(s, 0, l5 + 1) < i; l5++) { }
                k5 = l5 - 1;
                char c1;
                do
                {
                    c1 = s.charAt(l5--);
                } while(c1 != '\n' && c1 != ' ' && c1 != '-' && c1 != '.' && l5 > 0);
                if(l5 != 0)
                {
                    k5 = l5 + 1;
                }
            }
            g.drawString(s.substring(0, k5), l4, i5, 20);
            s = s.substring(k5);
            i5 += i4;
        } while(s.length() > 0 && i5 + i4 < i3 - k);
        g.setColor(0xffffff);
        g.drawLine(l2 + 10, i3 - k, l2 + 15, i3 - k);
        k = l3;
        
    }
    
      public void pointBusPosition(Graphics g, int xpoint, int j1, int k1, int l1, int i2, int j2, 
            int zoom)
    {
               
        if(m_type == 2)
        {
            System.out.println("i am out");
            return;
        }
        int l2 = k1 + i2 / 2 + (px - xpoint) / (1 << zoom);
        int i3 = l1 + j2 / 2 + (py - j1) / (1 << zoom);
    
       if(l2 > -i && i3 > -j && l2 < i2 + k1 && i3 < j2 + l1)
        {
                    g.setColor(0xffede);
                    g.fillTriangle(l2, i3, l2+10, i3-10, l2+12,i3-8);
                    g.fillArc(l2 + 10, i3 - 16, 8, 8, 0, 360);
                    g.setColor(0);
                    g.drawLine(l2, i3, l2 + 10, i3 - 10);
                    g.drawLine(l2, i3, l2 + 12, i3 - 8);
                    g.drawArc(l2 + 10, i3 - 16, 7, 7, 0, 360);
        
        }
       
            Thread thread = new Thread(this);
            thread.start();
        
    }
        
}
