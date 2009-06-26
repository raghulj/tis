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

public class DirectionsFormField extends Form implements CommandListener {

    
       private TextField SearchQueryField;
       private Controller controller;
       private Command searchCommand;
       private Command cancelCommand;
       
       public DirectionsFormField(Controller controller) {
            super("Starting point");
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
        this.addCommand(searchCommand = new Command("suggest", Command.SCREEN, 1));
        this.addCommand(cancelCommand = new Command("Cancel", Command.BACK,
                        100));
        this.append("From");
         this.append(SearchQueryField);  
         this.append("Enter first few characters of any locality and click on suggest.");
    }
    public void commandAction(Command command, Displayable displayable) {
        if (command == searchCommand) {
           
            System.out.println(" query " +SearchQueryField.getString().toString());
            if(SearchQueryField.getString().toString().length() > 1)
            {
            controller.FromSuggestResults(SearchQueryField.getString().toString());
            }else{
                controller.showAlert("Enter the data for atleast 2  Letters", Alert.FOREVER, AlertType.INFO);
                controller.setCurrentScreen(this);
            }
                
        }
       else if (command == cancelCommand) {
            this.goBack();
       }
    }
    private void goBack() {
    
            controller.MainMenu();
        
    }
    public void clearQryField()
    {
        SearchQueryField.setString("");
    }
    
    public String getQuery()
    {
       return  SearchQueryField.getString().toString();
    }
    
       
}
