/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Orderlines;
import com.kk.AutoFillSystem.Database.Entities.Products;
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
public class EditOrderlineWindowController implements Initializable {
    
    private Orderlines ol;
    private DataController dc; 
    private EditOrderWindowController parent;
    
    @FXML
    private TextField textFieldProdNum;
    @FXML
    private TextField textFieldQuantity;
    @FXML
    private Button btnUpdate;
    
    
    public EditOrderlineWindowController(Orderlines ol, EditOrderWindowController parent) {
        this.ol = ol;
        dc = DataController.getInstance();
        this.parent = parent;
        
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        textFieldProdNum.setText(ol.getProductId().getProdNum());
        textFieldQuantity.setText(Integer.toString(ol.getQuantity()));
        
        btnUpdate.setOnAction(e->update(e));
        
        
        
        
    }    
    
    private void update(ActionEvent e){
        boolean changed = false;
        String prodNum = textFieldProdNum.getText().trim();
        int quantity = Integer.parseInt(textFieldQuantity.getText());
        
        if (!prodNum.equals(ol.getProductId().getProdNum())) {
            Products pd = dc.getProduct(prodNum);
            if (pd != null) {
                ol.setProductId(pd);
                changed = true;
            } else {
                showAlert("Warning", "Update Error :", prodNum + " does not exist !", Alert.AlertType.WARNING);
                return;
            }
        }
        
        if (quantity != ol.getQuantity()){
            ol.setQuantity(quantity);
            changed = true;
        }

        if (changed && dc.isManaged(ol)) {
            
            dc.updateOrderline(ol);
        }
        
        //update viewlist
        parent.getListViewOrderlines().refresh();
        
        closeWindow(e);
    }
    
    
}
