/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mapunity.net;

import java.io.*;
import javax.microedition.io.*;

import com.mapunity.tracker.controller.Controller;
import javax.microedition.lcdui.AlertType;

/**
 *
 * @author raghul
 */
public class Downloader {

    Controller controller;

    public Downloader(Controller con) {
        controller = con;

    }

    public String requestForData(String URL) {
       
        String resultData = null;
        StreamConnection c = null;
        InputStream s = null;
        StringBuffer b = new StringBuffer();

        String url = URL;
        System.out.print(url);
        try {
            c = (StreamConnection) Connector.open(url);
            s = c.openDataInputStream();
            int ch;
            int k = 0;
            while ((ch = s.read()) != -1) {
                System.out.print((char) ch);
                b.append((char) ch);
            }

            String result = b.toString();

            if (!result.equals("")) {

                resultData = result.toString();
            }
        } catch (Exception e) {
            System.out.print(e);
            controller.ShowPointingCanvas();
            controller.showAlert("Network Error", 3, AlertType.ERROR);
        }

        return resultData;


    }
}
