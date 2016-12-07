/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Addresses;
import com.kk.AutoFillSystem.Database.Entities.Carriers;
import com.kk.AutoFillSystem.Database.Entities.Products;
import com.kk.AutoFillSystem.Database.Entities.Trklines;
import com.kk.AutoFillSystem.Database.Entities.Ustrkings;
import com.kk.AutoFillSystem.utility.ComboBoxListener;
import com.kk.AutoFillSystem.utility.JoinRecord;
import com.kk.AutoFillSystem.utility.Product;
import com.kk.AutoFillSystem.utility.Shipment;
import static com.kk.AutoFillSystem.utility.Tools.closeWindow;
import static com.kk.AutoFillSystem.utility.Tools.expandInfo;
import static com.kk.AutoFillSystem.utility.Tools.showAlert;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Yi
 */
public class UsTrkWindowController implements Initializable {
    private DataController dataCenter;
    private MainWindowController mainWindow;
    
    private Ustrkings ustrk;
    private ArrayList<Trklines> trklines;
    
    private JoinRecord record;
    
    @FXML
    private Label labelOrderNum;
    
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField textFieldTrkNum;
    @FXML
    private ComboBox<String> comboBoxCarrier;
    @FXML
    private TextField textFieldQuantity;
    @FXML
    private ComboBox<String> comboBoxItem;
    @FXML
    private ComboBox<String> comboBoxShipto;
    @FXML
    private Button buttonAdd;
    @FXML
    private Button buttonClear;
    @FXML
    private TextArea textAreaShipLines;
    @FXML
    private Button buttonCreate;
    @FXML
    private Button buttonCancel;

    
    public UsTrkWindowController(JoinRecord record) {
        dataCenter = DataController.getInstance();
        trklines = new ArrayList();
        ustrk = new Ustrkings();
        this.record = record;
    
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //set up order number label
        labelOrderNum.setText(record.getOrderNum());
        
        //buttons
        buttonCreate.setOnAction(e->{create(e);});
        buttonCancel.setOnAction(e->{closeWindow(e);});
        buttonAdd.setOnAction(e->{addItem();});
        buttonClear.setOnAction(e->{clearItems();});
        
        
        //load comboBox for stores and products and addresses
        ObservableList<String> carrierList = FXCollections.observableArrayList();
        for(Carriers carrier: mainWindow.getCarriers()) {
            carrierList.add(carrier.getName());
        }
        
        FXCollections.sort(carrierList);
        comboBoxCarrier.setItems(carrierList);
        
        ObservableList<String> productList = FXCollections.observableArrayList();
        for(Products prd: mainWindow.getProducts()) {
            if (!prd.getProdNum().equals("00000"))
                productList.add(prd.getProdNum());
        }
        FXCollections.sort(productList);
        comboBoxItem.setItems(productList);
        comboBoxItem.getEditor().textProperty().addListener(new ComboBoxListener(comboBoxItem));
        
        
        ObservableList<String> addrList = FXCollections.observableArrayList();
        for(Addresses addr: mainWindow.getWarehouses()) {
            addrList.add(addr.getName());
        }
        
        FXCollections.sort(addrList);
        comboBoxShipto.setItems(addrList);
    }    
    
    

    private void addItem() {
        
        try{
            String prodNum = comboBoxItem.getValue();
            if(prodNum == null || prodNum.length() == 0) {
                showAlert("Error", "Product Error :" , "You did not select product !", AlertType.ERROR);
                return;
            } 
            int count = Integer.parseInt(textFieldQuantity.getText());
            textAreaShipLines.appendText(prodNum + " : " + count +"\n");
            
            //new order line
            Trklines newTl = new Trklines();
            newTl.setQuantity(count);
            for(Products prd : mainWindow.getProducts()) {
                if (prd.getProdNum().equals(prodNum)) {
                    newTl.setProductId(prd);
                    break;
                }
            }
            
            trklines.add(newTl);
        }
        catch (NumberFormatException e) {
            showAlert("Error", "Quantity Error :" , "Quantity has to be number greater than 0 !", AlertType.ERROR);
            textFieldQuantity.setText(null);
        }
        
    }
    
    
    private void clearItems() {
        //clear nodes
        comboBoxItem.setValue(null);
        textFieldQuantity.clear();
        textAreaShipLines.clear();
        //clear saved products
        trklines.clear();
    }
    
    
    
    private void create(ActionEvent e) {
        if (textFieldTrkNum.getText() == null || textFieldTrkNum.getText().length() == 0) {
            showAlert("Error", "Trk Number Error :" , "Tracking number is required!", AlertType.ERROR);
            return;
        }
        
        
        ustrk.setTrkingNum(textFieldTrkNum.getText().trim());
        
        
        //check if trking already exsited
        for(Ustrkings ustrk : record.getOrder().getUstrkingsCollection()) {
            if (ustrk.getTrkingNum().toLowerCase().equals(ustrk.getTrkingNum().toLowerCase())) {
                showAlert("Error", "Trk Number Error :" , "Tracking number already exists!", AlertType.ERROR);
                return;
            }
        }
        
        
        if (comboBoxCarrier.getValue() == null || comboBoxCarrier.getValue().length() == 0) {
            showAlert("Error", "Carrier Error :" , "Carrier is required!", AlertType.ERROR);
            return;
        }
        
        for(Carriers carrier : mainWindow.getCarriers()) {
            if (carrier.getName().equals(comboBoxCarrier.getValue())) {
                ustrk.setCarrierId(carrier);
                break;
            }
        }
        
        
        
        if (comboBoxShipto.getValue() == null || comboBoxShipto.getValue().length() == 0) {
            showAlert("Error", "Shipping Address Error :" , "Shipping Address is required!", AlertType.ERROR);
            return;
        }
        
        for(Addresses addr : mainWindow.getAddresses()) {
            if (addr.getName().equals(comboBoxShipto.getValue())) {
                ustrk.setAddressId(addr);
                break;
            }
        }
        
        
        
        LocalDate date = datePicker.getValue();
        if (date != null) {
            Date orderDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            ustrk.setShipDate(orderDate);
            
        }
        

        ustrk.setOrderId(record.getOrder());
        
        
        
        if (!dataCenter.createUsTrking(ustrk)) {
            showAlert("Failed", "Creation Failed :" , "The us shipment could not be created, it might be duplicated !", AlertType.ERROR);
        }
            
        else {
            for(Trklines tl : trklines) {
                tl.setUstrkingId(ustrk);
                dataCenter.createTrkline(tl);
                ustrk.getTrklinesCollection().add(tl);
            }
            
            
            showAlert("Success", "Record Created :" , "New US shipment is created successfully !", AlertType.INFORMATION);
            //if the current record does not have us trking, add info in
            if (record.getUsTrk() == null) {
                record.setUsTrk(ustrk);
                expandInfo(record);

                //forcing refreshing table
                mainWindow.getOrderTable().getColumns().get(0).setVisible(false);
                mainWindow.getOrderTable().getColumns().get(0).setVisible(true);

            }
            
            else {
                //create new record to store info
                JoinRecord newRecord = new JoinRecord();
                newRecord.setOrder(record.getOrder());
                newRecord.setOrderDate(record.getOrderDate());
                newRecord.setOrderNum(record.getOrderNum());
                newRecord.setStore(record.getStore());
                newRecord.setUsTrk(ustrk);
                expandInfo(newRecord);
                
                mainWindow.getTableRows().add(newRecord);
                
            }
            
        }
        closeWindow(e);
        
    }
    
    
    private void setTrkInfo(JoinRecord record, Ustrkings newTrk) {
        record.setUsCarrier(newTrk.getCarrierId().getName());
        record.setUsTrkNum(newTrk.getTrkingNum());
        record.setWarehouse(newTrk.getAddressId().getName());

        StringBuilder sb = new StringBuilder();
        for (Trklines prd : newTrk.getTrklinesCollection()) {

            sb.append(prd.getProductId().getProdNum()).append(" : ").append(prd.getQuantity()).append(" || ");

        }

        String items = sb.toString();
        if (items.length() > 0) {
            record.setShipList(items.substring(0, items.length() - 3));
        }

    }
    
    
    
    
    
    
    
    public MainWindowController getMainWindow() {
        return mainWindow;
    }

    public void setMainWindow(MainWindowController mainWindow) {
        this.mainWindow = mainWindow;
    }
    
    
    
    
    
}
