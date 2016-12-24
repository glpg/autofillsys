/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Products;
import com.kk.AutoFillSystem.Database.Entities.Trklines;
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
 *
 * @author Yi
 */
public class EditTrklineWindowController implements Initializable {

    private Trklines tl;
    private DataController dc; 
    private EditUsTrkWindowController parent;
    
    @FXML
    private TextField textFieldProdNum;
    @FXML
    private TextField textFieldQuantity;
    @FXML
    private Button btnUpdate;
    
    
    public EditTrklineWindowController(Trklines tl, EditUsTrkWindowController parent) {
        this.tl = tl;
        dc = DataController.getInstance();
        this.parent = parent;
        
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        textFieldProdNum.setText(tl.getProductId().getProdNum());
        textFieldQuantity.setText(Integer.toString(tl.getQuantity()));
        
        btnUpdate.setOnAction(e->update(e));
        
        
        
        
    }    
    
    private void update(ActionEvent e){
        boolean changed = false;
        String prodNum = textFieldProdNum.getText().trim();
        int quantity = Integer.parseInt(textFieldQuantity.getText());
        
        if (!prodNum.equals(tl.getProductId().getProdNum())) {
            Products pd = dc.getProduct(prodNum);
            if (pd != null) {
                tl.setProductId(pd);
                changed = true;
            } else {
                showAlert("Warning", "Update Error :", prodNum + " does not exist !", Alert.AlertType.WARNING);
                return;
            }
        }
        
        if (quantity != tl.getQuantity()){
            tl.setQuantity(quantity);
            changed = true;
        }

        if (changed && dc.isManaged(tl)) {
            
            dc.updateTrkline(tl);
        }
        
        //update viewlist
        parent.getListViewTrklines().refresh();
        
        closeWindow(e);
    }
    
    
}
