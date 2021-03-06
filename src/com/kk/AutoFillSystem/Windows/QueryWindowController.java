/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.DataCenter.DataController;
import static com.kk.AutoFillSystem.utility.Tools.showAlert;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

/**
 * FXML Controller class
 *
 * @author Yi
 */
public class QueryWindowController implements Initializable {
    
    private DataController dt ;
    
    @FXML
    private TextField txtFieldPnum;
    @FXML
    private Label labelBought;
    @FXML
    private Label labelShipped;
  
    @FXML
    private Label labelSold;

    
    public QueryWindowController(){
        dt = DataController.getInstance();
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
        txtFieldPnum.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                
                query();
            }
                
        });
    }    
    
    private void query(){
        String prdNum = txtFieldPnum.getText();
        if(prdNum == null || prdNum.isEmpty()) {
            showAlert("Error", "Input Error :" , "You did not input any Lego number", Alert.AlertType.ERROR);
            return;
        }
        
        Object bought = dt.queryBoughtSum(prdNum);
        if (bought == null) bought = "null";
        labelBought.setText(bought.toString());
        
        Object shipped = dt.queryShippedSum(prdNum);
        if (shipped == null) shipped = "null";
        labelShipped.setText(shipped.toString());
        
        
        Object result = dt.querySoldSum(prdNum);
        if (result == null) result = "null";
        labelSold.setText(result.toString());
    }
    
}
