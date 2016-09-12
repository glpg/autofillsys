/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Usanduscntrkings;
import com.kk.AutoFillSystem.Database.Entities.Ustocntrkings;
import com.kk.AutoFillSystem.utility.JoinRecord;
import com.kk.AutoFillSystem.utility.Product;
import com.kk.AutoFillSystem.utility.Shipment;
import static com.kk.AutoFillSystem.utility.Tools.closeWindow;
import static com.kk.AutoFillSystem.utility.Tools.expandInfo;
import static com.kk.AutoFillSystem.utility.Tools.showAlert;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Yi
 */
public class IntlTrkWindowController implements Initializable {
    
    private DataController dataCenter;
    private MainWindowController mainWindow;
    private JoinRecord record;
    
    
    
    
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField textFieldTrkNum;
    @FXML
    private Button buttonCreate;
    @FXML
    private Button buttonCancel;
    @FXML
    private Label labelUsTrkNum;
    @FXML
    private Label labelWarehouse;
    @FXML
    private TextField textFieldWeight;
    @FXML
    private TextField textFieldFee;
    
    
    public IntlTrkWindowController(JoinRecord record) {
        dataCenter = DataController.getInstance();
       
        this.record = record;
    
    }
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //set up order number label
        labelUsTrkNum.setText(record.getUsTrkNum());
        labelWarehouse.setText(record.getWarehouse());
        
        //buttons
        buttonCreate.setOnAction(e->{create(e);});
        buttonCancel.setOnAction(e->{closeWindow(e);});
        
    }    
    
    
    private void create(ActionEvent e) {
        Ustocntrkings intlTrk = new Ustocntrkings();
        //set trk number
        if (textFieldTrkNum.getText() == null || textFieldTrkNum.getText().length() == 0) {
            showAlert("Error", "Trk Number Error :" , "Tracking number is required!");
            return;
        }
        
        
        
        intlTrk.setTrkingNum(textFieldTrkNum.getText().trim());
        
        //check if trking already exsited
        for(Usanduscntrkings relation : record.getUsTrk().getUsanduscntrkingsCollection()) {
            Ustocntrkings intl = relation.getUstocntrkingId();
            if (intlTrk.getTrkingNum().equals(intl.getTrkingNum())) {
                showAlert("Error", "Trk Number Error :" , "Tracking number already exists!");
                return;
            }
            
        }
        
        
        
        //set shipdate
        LocalDate date = datePicker.getValue();
        if (date != null) {
            Date shipDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            intlTrk.setShipDate(shipDate);
            
        }
        
        //set warehouse
        intlTrk.setAddressId(record.getUsTrk().getAddressId());
        
        //set weight and shipping fee
        if (!(textFieldWeight.getText() == null || textFieldWeight.getText().isEmpty())) {
            try {
                intlTrk.setWeight(Integer.parseInt(textFieldWeight.getText()));
            } catch (NumberFormatException ex) {
                showAlert("Error", "Weight Error :", "Weight has to be integers !");
                textFieldWeight.setText(null);
            }

        }
        
        if (!(textFieldFee.getText() == null || textFieldFee.getText().isEmpty())) {
            try {
                BigDecimal fee = new BigDecimal(Double.parseDouble(textFieldFee.getText()));
                intlTrk.setShippingfee(fee);
            } catch (NumberFormatException ex) {
                showAlert("Error", "ShippingFee Error :", "Shipping fee has to be float value !");
                textFieldFee.setText(null);
            }

        }

        dataCenter.createIntlTrking(intlTrk, record.getUsTrk());
        showAlert("Success", "Record Created :" , "New international shipment is created successfully !");
        //now modify tablerows
        if (record.getIntlTrk() == null) {
            record.setIntlTrk(intlTrk);
            record.setIntlTrkNum(intlTrk.getTrkingNum());
            record.setWeight(intlTrk.getWeight());
            record.setFee(intlTrk.getShippingfee().doubleValue());
            
            //forcing refreshing table
            mainWindow.getOrderTable().getColumns().get(0).setVisible(false);
            mainWindow.getOrderTable().getColumns().get(0).setVisible(true);
        }
        
        else {
            JoinRecord newRecord = new JoinRecord();
            //copy info from record
            newRecord.setOrder(record.getOrder());
            newRecord.setUsTrk(record.getUsTrk());
            newRecord.setIntlTrk(intlTrk); 
            expandInfo(newRecord);
            
            //add to tablerows
            mainWindow.getTableRows().add(newRecord);
            
        }
        
        closeWindow(e);
        
    }
    
    
    //setter and getter

    public MainWindowController getMainWindow() {
        return mainWindow;
    }

    public void setMainWindow(MainWindowController mainWindow) {
        this.mainWindow = mainWindow;
    }
    
    
    
    
}
