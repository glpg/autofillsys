/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Addresses;
import com.kk.AutoFillSystem.Database.Entities.Carriers;
import com.kk.AutoFillSystem.Database.Entities.Cntrkings;
import com.kk.AutoFillSystem.utility.JoinRecord;
import static com.kk.AutoFillSystem.utility.Tools.closeWindow;
import static com.kk.AutoFillSystem.utility.Tools.expandInfo;
import static com.kk.AutoFillSystem.utility.Tools.showAlert;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Yi
 */
public class CnTrkWindowController implements Initializable {
    
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
    private Label labelIntlTrkNum;
    @FXML
    private ComboBox<String> comboBoxCarrier;
    @FXML
    private ComboBox<String> comboBoxAddr;
    
    
    public CnTrkWindowController(JoinRecord record) {
        dataCenter = DataController.getInstance();
       
        this.record = record;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //set up order number label
        labelIntlTrkNum.setText(record.getIntlTrkNum());
        
        //buttons
        buttonCreate.setOnAction(e->{create(e);});
        buttonCancel.setOnAction(e->{closeWindow(e);});
        
        
        //load comboBox for stores and products and addresses
        ObservableList<String> carrierList = FXCollections.observableArrayList();
        for(Carriers carrier: mainWindow.getCarriers()) {
            carrierList.add(carrier.getName());
        }
        
        FXCollections.sort(carrierList);
        comboBoxCarrier.setItems(carrierList);
        
       
        
        ObservableList<String> addrList = FXCollections.observableArrayList();
        for(Addresses addr: mainWindow.getAddresses()) {
            if (addr.getCountry() != null && addr.getCountry().equals("CN"))
                addrList.add(addr.getName());
        }
        
        FXCollections.sort(addrList);
        comboBoxAddr.setItems(addrList);
    }    
    
    
    
    private void create(ActionEvent e) {
        Cntrkings cnTrk = new Cntrkings();
        //set trk number
        if (textFieldTrkNum.getText() == null || textFieldTrkNum.getText().length() == 0) {
            showAlert("Error", "Trk Number Error :" , "Tracking number is required!", AlertType.ERROR);
            return;
        }
        
        
        
        cnTrk.setTrkingNum(textFieldTrkNum.getText().trim());
        
        //check if trking already exsited
        for(Cntrkings trk : record.getIntlTrk().getCntrkingsCollection()) {
            
            if (trk.getTrkingNum().equals(cnTrk.getTrkingNum())) {
                showAlert("Error", "Trk Number Error :" , "Tracking number already exists!", AlertType.ERROR);
                return;
            }
            
        }
        
        
        
        //set shipdate
        LocalDate date = datePicker.getValue();
        if (date != null) {
            Date shipDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            cnTrk.setShipDate(shipDate);
            
        }
        
        //set carrier
        if (comboBoxCarrier.getValue() == null || comboBoxCarrier.getValue().length() == 0) {
            showAlert("Error", "Carrier Error :" , "Carrier is required!", AlertType.ERROR);
            return;
        }
        
        for(Carriers carrier : mainWindow.getCarriers()) {
            if (carrier.getName().equals(comboBoxCarrier.getValue())) {
                cnTrk.setCarrierId(carrier);
                break;
            }
                
        }
        
        //set address
        if (comboBoxAddr.getValue() == null || comboBoxAddr.getValue().length() == 0) {
            showAlert("Error", "Address Error :" , "Address is required!",AlertType.ERROR);
            return;
        }
        
        for(Addresses addr:mainWindow.getAddresses()) {
            if (addr.getName().equals(comboBoxAddr.getValue())) {
                cnTrk.setAddressId(addr);
                break;
            }
                
        }
        
        cnTrk.setUstocntrkingId(record.getIntlTrk());
        
        
        

        dataCenter.createCnTrking(cnTrk);
        showAlert("Success", "Record Created :" , "New China shipment is created successfully !", AlertType.INFORMATION);
        //now modify tablerows
        if (record.getCnTrk() == null) {
            record.setCnTrk(cnTrk);
            record.setCnCarrier(cnTrk.getCarrierId().getName());
            record.setCnTrkNum(cnTrk.getTrkingNum());
            record.setAddress(cnTrk.getAddressId().getName());
            
            //forcing refreshing table
            mainWindow.getOrderTable().getColumns().get(0).setVisible(false);
            mainWindow.getOrderTable().getColumns().get(0).setVisible(true);
        }
        
        else {
            JoinRecord newRecord = new JoinRecord();
            //copy info from record
            newRecord.setOrder(record.getOrder());
            newRecord.setUsTrk(record.getUsTrk());
            newRecord.setIntlTrk(record.getIntlTrk()); 
            newRecord.setCnTrk(cnTrk);
            expandInfo(newRecord);
            
            //add to tablerows
            mainWindow.getTableRows().add(newRecord);
            
        }
        
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
