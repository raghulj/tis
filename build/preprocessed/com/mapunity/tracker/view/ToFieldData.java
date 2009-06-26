/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mapunity.tracker.view;



import javax.microedition.lcdui.*;

import com.mapunity.tracker.controller.Controller;
import com.mapunity.tracker.model.*;

/**
 *
 * @author raghul
 */

public class ToFieldData extends Form implements CommandListener {

    
       private TextField SearchQueryField;
       private Controller controller;
       private Command searchCommand;
       private Command cancelCommand;
       
       public ToFieldData(Controller controller) {
            super("Enter To Field");
               this.SearchQueryField = new TextField("", "kora", 64, TextField.ANY);
               this.initialize(controller);
       
        }
       private void initialize(Controller controller) {
        this.controller = controller;
        this.initializeCommands();
        this.setCommandListener(this);
    }
           /** Initialize commands */
    private void initializeCommands() {
        SearchQueryField.setString("");
        this.addCommand(searchCommand = new Command("suggest me", Command.SCREEN, 1));
        this.addCommand(cancelCommand = new Command("Cancel", Command.BACK,
                        100));
        this.append("To ");
         this.append(SearchQueryField);  
    }
    public void commandAction(Command command, Displayable displayable) {
        if (command == searchCommand) {
            controller.showsearchResults(SearchQueryField.getString().toString());
                
        }
       else if (command == cancelCommand) {
            this.goBack();
       }
    }
    private void goBack() {
    
            controller.ShowPointingCanvas();
        
    }
    public void clearQryField()
    {
        SearchQueryField.setString("");
    }
    
    public String getQuery()
    {
       return  SearchQueryField.getString();
    }
    
       
}
