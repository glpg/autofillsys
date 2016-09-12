/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Stores;
import static com.kk.AutoFillSystem.utility.Tools.closeWindow;
import static com.kk.AutoFillSystem.utility.Tools.showAlert;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Yi
 */
public class NewStoreWindowController implements Initializable {
    private DataController dataCenter;
    private MainWindowController mainWindow;
    
    
    @FXML
    private Button buttonCreate;
    @FXML
    private Button buttonCancel;
    @FXML
    private TextField textFieldStore;
    
    public NewStoreWindowController() {
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
        Stores store = new Stores();
        if (textFieldStore.getText() == null || textFieldStore.getText().isEmpty()) {
            showAlert("Error", "Store Error :" , "Store name is required!");
            return;
        }
        
        store.setName(textFieldStore.getText());
        
        //check if store is duplicated
        
        for(Stores temp : mainWindow.getStores()) {
            if (store.getName().equals(temp.getName())) {
                showAlert("Error", "Duplicate Error :" , "Store already exists!");
                return;
            }
                
        }
        
       
        
        dataCenter.createStore(store);
        
        showAlert("Success", "Record Created :" , "New store is created successfully !");
        mainWindow.getStores().add(store);
        mainWindow.getComboBoxStore().getItems().add(store.getName());
      
        closeWindow(e);
        
    }

    public void setMainWindow(MainWindowController mainWindow) {
        this.mainWindow = mainWindow;
    }
    
    
    
}
