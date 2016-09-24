/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Addresses;
import static com.kk.AutoFillSystem.utility.Tools.closeWindow;
import static com.kk.AutoFillSystem.utility.Tools.showAlert;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Yi
 */
public class NewAddressWindowController implements Initializable {
    private DataController dataCenter;
    private MainWindowController mainWindow;
    
    
    @FXML
    private TextField textFieldAddr2;
    @FXML
    private Button buttonCreate;
    @FXML
    private Button buttonCancel;
    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldAddr1;
    @FXML
    private TextField textFieldCity;
    @FXML
    private TextField textFieldState;
    @FXML
    private TextField textFieldZip;
    @FXML
    private ComboBox<String> comboBoxCountry;
    @FXML
    private TextField textFieldPhone;
    
    
    public NewAddressWindowController(){
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
        
        //set up combobox
        comboBoxCountry.getItems().addAll("US", "CN");
        
    }    
    
    
    private void create(ActionEvent e) {
        Addresses addr = new Addresses();
        if (textFieldName.getText() == null || textFieldName.getText().isEmpty()) {
            showAlert("Error", "Carrier Error :" , "Carrier name is required!", AlertType.ERROR);
            return;
        }
        
        addr.setName(textFieldName.getText());
        //check if exists
        for(Addresses temp : mainWindow.getAddresses()) {
            if (addr.getName().equals(temp.getName())) {
                showAlert("Error", "Duplicate Error :" , "Address already exists!", AlertType.WARNING);
                return;
            }
                
        }
        
        //country must set
        if (comboBoxCountry.getValue() == null || comboBoxCountry.getValue().isEmpty()) {
            showAlert("Error", "Country Error :" , "Country is required!", AlertType.ERROR);
            return;
        }
        
        addr.setCountry(comboBoxCountry.getValue());
        
        //addr1
        if (!(textFieldAddr1.getText() == null || textFieldAddr1.getText().isEmpty())) {
            addr.setAddressline1(textFieldAddr1.getText());
        }
        
        //addr2
        if (!(textFieldAddr2.getText() == null || textFieldAddr2.getText().isEmpty())) {
            addr.setAddressline2(textFieldAddr2.getText());
        }
        
        //city
        if (!(textFieldCity.getText() == null || textFieldCity.getText().isEmpty())) {
            addr.setCity(textFieldCity.getText());
        }
        
        
        //state
        if (!(textFieldState.getText() == null || textFieldState.getText().isEmpty())) {
            addr.setState(textFieldState.getText());
        }
        
        //zip
        if (!(textFieldZip.getText() == null || textFieldZip.getText().isEmpty())) {
            addr.setZip(textFieldZip.getText());
        }
        
        //create address
        dataCenter.createAddress(addr);
        
        showAlert("Success", "Record Created :" , "New address is created successfully !", AlertType.INFORMATION);
        mainWindow.getAddresses().add(addr);
        
        if (addr.getCountry().equals("CN")) 
            mainWindow.getComboBoxDestination().getItems().add(addr.getName());
        else
            mainWindow.getComboBoxWarehouse().getItems().add(addr.getName());
        
        
        closeWindow(e);
    }
    
    
    //getters and setters

    public MainWindowController getMainWindow() {
        return mainWindow;
    }

    public void setMainWindow(MainWindowController mainWindow) {
        this.mainWindow = mainWindow;
    }
    
    
    
    
}
