/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Carriers;
import com.kk.AutoFillSystem.Database.Entities.Ustocntrkings;
import com.kk.AutoFillSystem.Database.Entities.Ustrkings;
import com.kk.AutoFillSystem.utility.JoinRecord;
import static com.kk.AutoFillSystem.utility.Tools.closeWindow;
import static com.kk.AutoFillSystem.utility.Tools.expandInfo;
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
public class SearchIntlTrkWindowController implements Initializable {
    
    private DataController dataCenter;
    private MainWindowController mainWindow;
    
    @FXML
    private Button btnFind;
    @FXML
    private Button btnCancel;
    @FXML
    private TextField textFieldInput;
    
    public SearchIntlTrkWindowController() {
        dataCenter = DataController.getInstance();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //buttons
        btnFind.setOnAction(e->{searchIntlTrk(e);});
        btnCancel.setOnAction(e->{closeWindow(e);});
    }    
    
    private void searchIntlTrk(ActionEvent e) {
        if (textFieldInput.getText() == null || textFieldInput.getText().isEmpty()) {
            showAlert("Error", "Input Error :" , "Intl trking number is required!", Alert.AlertType.ERROR);
            return;
        }
        String intlTrkNum = textFieldInput.getText().trim();
        if (dataCenter.getIntlTrking(intlTrkNum) == null || dataCenter.getIntlTrking(intlTrkNum).size() == 0) {
            showAlert("Warning", "Not Found :" , "The intl trking number could not be found, please double check the input!", Alert.AlertType.WARNING);
            return;
        }
        
        else {
            Ustocntrkings found = dataCenter.getIntlTrking(intlTrkNum).get(0);

            JoinRecord currentRecord = (JoinRecord) mainWindow.getOrderTable().getSelectionModel().getSelectedItem();
            Ustrkings ustrk = currentRecord.getUsTrk();
            dataCenter.createUsAndIntlRelation(found, ustrk);
            showAlert("Success", "Trkings Correlated :" , "US and Intl trking are correlated successfully !", Alert.AlertType.INFORMATION);
            
            currentRecord.setIntlTrk(found);
            expandInfo(currentRecord);
            
             //forcing refreshing table
            mainWindow.getOrderTable().getColumns().get(0).setVisible(false);
            mainWindow.getOrderTable().getColumns().get(0).setVisible(true);
            closeWindow(e);

            
            
        }
       
         
    }

    public void setMainWindow(MainWindowController mainWindow) {
        this.mainWindow = mainWindow;
    }
    
}
