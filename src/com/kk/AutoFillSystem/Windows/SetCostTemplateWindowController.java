/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.utility.PriceEstimation;
import static com.kk.AutoFillSystem.utility.Tools.closeWindow;
import static com.kk.AutoFillSystem.utility.Tools.showAlert;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Yi
 */
public class SetCostTemplateWindowController implements Initializable {
    private MainWindowController mainWindow;
    private PriceEstimation pe = PriceEstimation.getInstance();
    
    private double currencyRate;
    private double discount;
    private double initfee;
    private double extrafee;
    private int boxweight;
    private double vipDiscount;
    private int shipCnt;
    
    
    
    @FXML
    private TextField textFieldRate;
    @FXML
    private TextField textFieldInitFee;
    @FXML
    private TextField textFieldExtraFee;
    @FXML
    private TextField textFieldDiscount;
    @FXML
    private TextField textFieldVIPDiscount;
    @FXML
    private TextField textFieldShipCount;
    @FXML
    private TextField textFieldBoxWeight;
    @FXML
    private Button btnSave;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // init values
        discount = pe.getDiscount();
        textFieldDiscount.setText("" + discount*100);
        textFieldDiscount.requestFocus();
        textFieldDiscount.textProperty().addListener((observable, oldValue, newValue) -> {
        try {
                discount = Double.parseDouble(newValue)/100;
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid Format :", "Discount has to be a number !", Alert.AlertType.ERROR);
                textFieldDiscount.setText(oldValue);
            }

        });
        
        
        currencyRate = pe.getCurrencyRatio();
        textFieldRate.setText(""+ currencyRate);
        textFieldRate.textProperty().addListener((observable, oldValue, newValue) -> {
        try {
                currencyRate = Double.parseDouble(newValue);
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid Format :", "Currency rate has to be a number !", Alert.AlertType.ERROR);
                textFieldRate.setText(oldValue);
            }

        });
        
        
        initfee = pe.getInitalShipFee();
        textFieldInitFee.setText(""+ initfee);
        textFieldInitFee.textProperty().addListener((observable, oldValue, newValue) -> {
        try {
                initfee = Double.parseDouble(newValue);
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid Format :", "Shipping initial fee has to be a number !", Alert.AlertType.ERROR);
                textFieldInitFee.setText(oldValue);
            }

        });
        
        
        extrafee = pe.getExtraShipFee();
        textFieldExtraFee.setText(""+ extrafee);
        textFieldExtraFee.textProperty().addListener((observable, oldValue, newValue) -> {
        try {
                extrafee = Double.parseDouble(newValue);
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid Format :", "Shipping extra fee has to be a number !", Alert.AlertType.ERROR);
                textFieldExtraFee.setText(oldValue);
            }

        });
        
        
        vipDiscount = pe.getVipDiscount();
        textFieldVIPDiscount.setText("" + vipDiscount *100);
        textFieldVIPDiscount.textProperty().addListener((observable, oldValue, newValue) -> {
        try {
                vipDiscount = Double.parseDouble(newValue) / 100;
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid Format :", "VIP discount has to be a number !", Alert.AlertType.ERROR);
                textFieldVIPDiscount.setText(oldValue);
            }

        });
        
        shipCnt = pe.getCount();
        textFieldShipCount.setText(""+ shipCnt);
        textFieldShipCount.textProperty().addListener((observable, oldValue, newValue) -> {
        try {
                shipCnt = Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid Format :", "Ship count has to be a number !", Alert.AlertType.ERROR);
                textFieldShipCount.setText(oldValue);
            }

        });
        
        boxweight = pe.getExtraBoxWeight();
        textFieldBoxWeight.setText("" + boxweight);
        
        textFieldBoxWeight.textProperty().addListener((observable, oldValue, newValue) -> {
        try {
                boxweight = Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid Format :", "Box weight has to be a number !", Alert.AlertType.ERROR);
                textFieldBoxWeight.setText(oldValue);
            }

        });
        
        //save button
        btnSave.setOnAction(e->save(e));
        
        
        
    }    
    
    private void save(ActionEvent e){
        pe.setCount(shipCnt);
        pe.setCurrencyRatio(currencyRate);
        pe.setDiscount(discount);
        pe.setExtraBoxWeight(boxweight);
        pe.setExtraShipFee(extrafee);
        pe.setInitalShipFee(initfee);
        pe.setVipDiscount(vipDiscount);
        showAlert("Success", "New Settings Saved :", "The new set of parameters has been save successfully !", Alert.AlertType.INFORMATION);
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
