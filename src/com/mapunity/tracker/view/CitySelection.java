/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mapunity.tracker.view;

import com.mapunity.tracker.controller.Controller;
import com.mapunity.tracker.model.CityData;
import com.mapunity.tracker.model.RecorderSettings;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

/**
 *
 * @author raghul
 */
public class CitySelection extends List implements CommandListener {
    
    private Controller controller;
    
    private Command cancel;
    private Command ok;
    
    
    public CitySelection(Controller controler){
        
        super("Select City",Choice.EXCLUSIVE);
        this.controller = controler;
        
        this.append(controller.BLR, null);
        this.append(controller.HYD, null);
        this.append(controller.CHE,null);
        this.append(controller.DL, null);
        this.append(controller.PUN, null);
        this.append(controller.IND,null);
        this.append(controller.MYS,null);
        
        ok = new Command("OK", Command.OK, 1);
        cancel = new Command("Cancel",Command.CANCEL,2);
        
        this.addCommand(ok);
        this.addCommand(cancel);
        
        this.setCommandListener(this);
                
    }

    public void commandAction(Command command,Displayable displayable){
        
        if(command == cancel){
             RecorderSettings settings = controller.getSettings();
             settings.setCity("");
             System.out.println(settings.getCity());
             controller.MainMenu();
        }
        if(command == ok){
             RecorderSettings settings = controller.getSettings();
             settings.setCity(this.getString(getSelectedIndex()));
             controller.selectedCity = (CityData) controller.cityHash.get(this.getString(getSelectedIndex()));
             controller.showAlert("selected city saved", 0, AlertType.INFO);
             controller.MainMenu();
        }
    }
}
