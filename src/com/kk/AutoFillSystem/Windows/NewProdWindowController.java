/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Products;
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
public class NewProdWindowController implements Initializable {
    private DataController dataCenter;
    private MainWindowController mainWindow;
    
    @FXML
    private Button buttonCreate;
    @FXML
    private Button buttonCancel;
    @FXML
    private TextField textFieldProdNum;
    @FXML
    private TextField textFieldProdName;
    
    
    public NewProdWindowController(){
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
        Products prod = new Products();
        if (textFieldProdNum.getText() == null || textFieldProdNum.getText().isEmpty()) {
            showAlert("Error", "Product Number Error :" , "Product number is required!", AlertType.ERROR);
            return;
        }
        
        prod.setProdNum(textFieldProdNum.getText());
        
        for(Products temp : mainWindow.getProducts()) {
            if (prod.getProdNum().equals(temp.getProdNum())) {
                showAlert("Error", "Product Error :" , "Product already exists!", AlertType.WARNING);
                return;
            }
                
        }
        
        if (textFieldProdName.getText() != null && !textFieldProdName.getText().isEmpty()) {
            prod.setProdName(textFieldProdName.getText().trim());
        }
        
        
        dataCenter.createProduct(prod);
        
        showAlert("Success", "Record Created :" , "New product is created successfully !", AlertType.INFORMATION);
        mainWindow.getProducts().add(prod);
        mainWindow.getComboBoxProduct().getItems().add(prod.getProdNum());
        
        //clear input field
        textFieldProdNum.clear();
        textFieldProdName.clear();
        
        
    }
    

    public void setMainWindow(MainWindowController mainWindow) {
        this.mainWindow = mainWindow;
    }
    
    
    
}
