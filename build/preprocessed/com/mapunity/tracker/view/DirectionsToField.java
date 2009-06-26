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

public class DirectionsToField extends Form implements CommandListener {

    
       private TextField SearchQueryField;
       private Controller controller;
       private Command searchCommand;
       private Command cancelCommand;
       private Command backToFromList;
       
       public DirectionsToField(Controller controller) {
            super("End point");
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
                        3));
        this.addCommand(backToFromList = new Command("back", Command.BACK, 2));
        this.append("To ");
         this.append(SearchQueryField);  
         this.append("Enter first few characters of any locality and click on suggest.");
    }
    public void commandAction(Command command, Displayable displayable) {
        if (command == searchCommand) {
             if(SearchQueryField.getString().toString().length() > 1)
            {
            controller.ToSuggestResults(SearchQueryField.getString().toString());
            }else{
                controller.showAlert("Enter the data for atleast 2 Letters", Alert.FOREVER, AlertType.INFO);
                controller.setCurrentScreen(this);
            }
           
                
        }
       else if (command == cancelCommand) {
            this.goBack();
       }
       else if(command == backToFromList)
       {
           controller.ShowFromSuggestResults();
       }
    }
    private void goBack() {
    
           // controller.ShowDirectionsFormForTo();
        controller.MainMenu();
        
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
