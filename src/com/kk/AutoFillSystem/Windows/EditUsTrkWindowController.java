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
import com.kk.AutoFillSystem.utility.JoinRecord;
import com.kk.AutoFillSystem.utility.Product;
import static com.kk.AutoFillSystem.utility.Tools.closeWindow;
import static com.kk.AutoFillSystem.utility.Tools.expandInfo;
import static com.kk.AutoFillSystem.utility.Tools.showAlert;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 *
 * @author Yi
 */
public class EditUsTrkWindowController implements Initializable{
    private DataController dataCenter;
    private MainWindowController mainWindow;
    
    private ArrayList<Product> prods;
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

    
    public EditUsTrkWindowController(JoinRecord record) {
        dataCenter = DataController.getInstance();
        prods = new ArrayList();
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
        buttonCreate.setOnAction(e->{edit(e);});
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
        
        ObservableList<String> addrList = FXCollections.observableArrayList();
        for(Addresses addr: mainWindow.getWarehouses()) {
            addrList.add(addr.getName());
        }
        
        FXCollections.sort(addrList);
        comboBoxShipto.setItems(addrList);
        
        //set up known info
        buttonCreate.setText("Update Tracking");
        
        Instant instant = Instant.ofEpochMilli(record.getOrderDate().getTime());
        
        LocalDate localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
        datePicker.setValue(localDate);
        textFieldTrkNum.setText(record.getUsTrkNum());
        comboBoxCarrier.setValue(record.getUsCarrier());
        if (!record.getWarehouse().equals("unknown")) {
            comboBoxShipto.setValue(record.getWarehouse());
            comboBoxShipto.setDisable(true);
        }
        if(record.getUsTrk().getTrklinesCollection() != null && record.getUsTrk().getTrklinesCollection().size() > 0) {
            for(Trklines item : record.getUsTrk().getTrklinesCollection()) {
                textAreaShipLines.appendText(item.getProductId().getProdNum() + " : " + item.getQuantity() + "\n");
            }
        }
        
        //set disable
        datePicker.setDisable(true);
        textFieldTrkNum.setDisable(true);
        comboBoxCarrier.setDisable(true);
        
        
        
        
    }    
    
    

    private void addItem() {
        
        try{
            String prodNum = comboBoxItem.getValue();
            if(prodNum == null || prodNum.length() == 0) {
                showAlert("Error", "Product Error :" , "You did not select product !");
                return;
            } 
            int count = Integer.parseInt(textFieldQuantity.getText());
            textAreaShipLines.appendText(prodNum + " : " + count +"\n");
            prods.add(new Product(prodNum, count));
        }
        catch (NumberFormatException e) {
            showAlert("Error", "Quantity Error :" , "Quantity has to be number greater than 0 !");
            textFieldQuantity.setText(null);
        }
        
    }
    
    
    private void clearItems() {
        //clear nodes
        comboBoxItem.setValue(null);
        textFieldQuantity.clear();
        textAreaShipLines.clear();
        //clear saved products
        prods.clear();
    }
    
    
    
    private void edit(ActionEvent e) {
        
        if(!comboBoxShipto.isDisabled()) {
            if (comboBoxShipto.getValue() == null ||comboBoxShipto.getValue().isEmpty()) {
                showAlert("Error", "Address Failed :" , "The shipto field is required !");
            }
            else {
                Ustrkings ustrk = record.getUsTrk();
                for(Addresses item : mainWindow.getWarehouses()) {
                    if (comboBoxShipto.getValue().equals(item.getName())){
                        ustrk.setAddressId(item);
                        //now persist data
                        Ustrkings updated = dataCenter.updateUsTrking(ustrk);
                        record.setUsTrk(updated);
                    }
                }
            }
        }
        
        
        for(Product prod  : prods) {
            for(Products entry : mainWindow.getProducts()) {
                if (prod.name.equals(entry.getProdNum())) {
                    Trklines temp = new Trklines();
                    temp.setProductId(entry);
                    temp.setUstrkingId(record.getUsTrk());
                    temp.setQuantity(prod.count);
                    
                    dataCenter.createTrkline(temp);
                    record.getUsTrk().getTrklinesCollection().add(temp);
                    
                    for(JoinRecord tblRecord : mainWindow.getTableRows()) {
                        if (tblRecord.getUsTrkNum().equals(record.getUsTrkNum())) {
                            expandInfo(tblRecord);
                        }
                    }
                }
            }
            
        }
        
        
        showAlert("Success", "Record Updated :" , "Us shipment is updated successfully !");
        //forcing refreshing table
        mainWindow.getOrderTable().getColumns().get(0).setVisible(false);
        mainWindow.getOrderTable().getColumns().get(0).setVisible(true);
         
        closeWindow(e);
        
    }
    
    
    
    
    
    
    public MainWindowController getMainWindow() {
        return mainWindow;
    }

    public void setMainWindow(MainWindowController mainWindow) {
        this.mainWindow = mainWindow;
    }
    
    
    
    
}
