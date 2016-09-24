/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Carriers;
import static com.kk.AutoFillSystem.utility.Tools.closeWindow;
import static com.kk.AutoFillSystem.utility.Tools.showAlert;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Yi
 */
public class NewCarrierWindowController implements Initializable {
    
    private DataController dataCenter;
    private MainWindowController mainWindow;
    
    @FXML
    private Button buttonCreate;
    @FXML
    private Button buttonCancel;
    @FXML
    private TextField textFieldCarrier;
    
    public NewCarrierWindowController() {
        dataCenter = DataController.getInstance();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //buttons
        buttonCreate.setOnAction(e->{create(e);});
        buttonCancel.setOnAction(e->{closeWindow(e);});
    }    
    
    private void create(ActionEvent e) {
        Carriers carrier = new Carriers();
        if (textFieldCarrier.getText() == null || textFieldCarrier.getText().isEmpty()) {
            showAlert("Error", "Carrier Error :" , "Carrier name is required!", AlertType.ERROR);
            return;
        }
        
        carrier.setName(textFieldCarrier.getText());
        
        //check if store is duplicated
        
        for(Carriers temp : mainWindow.getCarriers()) {
            if (carrier.getName().equals(temp.getName())) {
                showAlert("Error", "Duplicate Error :" , "Carrier already exists!", AlertType.WARNING);
                return;
            }
                
        }
        
       
        
        dataCenter.createCarrier(carrier);
        
        showAlert("Success", "Record Created :" , "New carrier is created successfully !", AlertType.INFORMATION);
        mainWindow.getCarriers().add(carrier);
       
        closeWindow(e);
    }

    public void setMainWindow(MainWindowController mainWindow) {
        this.mainWindow = mainWindow;
    }
    
    
}
