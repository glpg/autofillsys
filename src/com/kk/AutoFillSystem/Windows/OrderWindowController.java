/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Orderlines;
import com.kk.AutoFillSystem.Database.Entities.Orders;
import com.kk.AutoFillSystem.Database.Entities.Products;
import com.kk.AutoFillSystem.Database.Entities.Stores;
import com.kk.AutoFillSystem.utility.ComboBoxListener;
import com.kk.AutoFillSystem.utility.JoinRecord;
import static com.kk.AutoFillSystem.utility.Tools.expandInfo;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Yi
 */
public class OrderWindowController implements Initializable {
    private DataController dataCenter;
    private MainWindowController mainWindow;
    private Orders order;
    private ArrayList<Orderlines> orderlines;
    
    
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField textFieldOrderNum;
    @FXML
    private ComboBox<String> comboBoxStore;
    @FXML
    private TextField textFieldQuantity;
    @FXML
    private ComboBox<String> comboBoxItem;
    @FXML
    private Button buttonAdd;
    @FXML
    private Button buttonClear;
    @FXML
    private ListView listViewOrderlines;
    @FXML
    private Button buttonCreateOrder;
    @FXML
    private Button buttonCancel;
    
    
    //constructor
    public OrderWindowController(){
        dataCenter = DataController.getInstance();
        order = new Orders();
        orderlines = new ArrayList();
        
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //buttons
        buttonCreateOrder.setOnAction(e->{createOrder(e);});
        buttonCancel.setOnAction(e->{closeWindow(e);});
        buttonAdd.setOnAction(e->{addItem();});
        buttonClear.setOnAction(e->{clearItems();});
        
        
        //load comboBox for stores and products
        ObservableList<String> storeList = FXCollections.observableArrayList();
        for(Stores store: mainWindow.getStores()) {
            storeList.add(store.getName());
        }
        
        FXCollections.sort(storeList);
        comboBoxStore.setItems(storeList);
        
        ObservableList<String> productList = FXCollections.observableArrayList();
        for(Products prd: mainWindow.getProducts()) {
            if (!prd.getProdNum().equals("00000"))
                productList.add(prd.getProdNum());
        }
        FXCollections.sort(productList);
        comboBoxItem.setItems(productList);
        
        comboBoxItem.getEditor().textProperty().addListener(new ComboBoxListener(comboBoxItem));
        
        listViewOrderlines.setCellFactory(param -> new ListCell<Orderlines>() {
            @Override
            protected void updateItem(Orderlines item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getProductId().getProdNum() + " : " + item.getQuantity());
                }
            }
        });
        
        
    }  
    
    
    
    
    private void addItem() {
        
        try{
            String prodNum = comboBoxItem.getValue();
            if(prodNum == null || prodNum.length() == 0) {
                showAlert("Error", "Product Error :" , "You did not select product !");
                return;
            } 
            
            int count = Integer.parseInt(textFieldQuantity.getText());
           
            
            //new order line
            Orderlines newOl = new Orderlines();
            newOl.setQuantity(count);
            for(Products prd : mainWindow.getProducts()) {
                if (prd.getProdNum().equals(prodNum)) {
                    newOl.setProductId(prd);
                    break;
                }
            }
            
            orderlines.add(newOl);
            listViewOrderlines.getItems().add(newOl);
            
            comboBoxItem.setValue(null);
            textFieldQuantity.clear();
            
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
        listViewOrderlines.getItems().clear();
        //clear saved products
        orderlines.clear();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
            }
        });
    }
    
    private void createOrder(ActionEvent e){
        
        if (textFieldOrderNum.getText() == null || textFieldOrderNum.getText().length() == 0) {
            showAlert("Error", "Order Number Error :" , "Order number is required!");
            return;
        }
        
        order.setOrderNum(textFieldOrderNum.getText());
        if (comboBoxStore.getValue() == null || comboBoxStore.getValue().length() == 0) {
            showAlert("Error", "Order Store Error :" , "Order store is required!");
            return;
        }
        
        for(Stores store: mainWindow.getStores()) {
            if (store.getName().equals(comboBoxStore.getValue())) {
                order.setStoreId(store);
                break;
            }
                
        }
        
        LocalDate date = datePicker.getValue();
        if (date != null) {
            Date orderDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            order.setOrderDate(orderDate);
        }
        
        //create order
        dataCenter.createOrder(order);
        
        
        
        
         
        if (!dataCenter.createOrder(order)) {
            showAlert("Failed", "Creating Failed :" , "The order could not be created, it might already exists!");
            return;
        }
        else {
            //create orderlines
            for (Orderlines ol : orderlines) {
                ol.setOrderId(order);
                dataCenter.createOrderline(ol);
                order.getOrderlinesCollection().add(ol);
            }
            showAlert("Success", "Record Created :" , "New order is created successfully !");
            mainWindow.getOrders().add(order);
            //need to update mainwindow
            JoinRecord record = new JoinRecord();
            record.setOrder(order);
            expandInfo(record);
            mainWindow.getTableRows().add(record);
        }
        closeWindow(e);
        
    }
    
    private void closeWindow(ActionEvent e) {
        Node source;
        source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
        
    }

    public MainWindowController getMainWindow() {
        return mainWindow;
    }

    public void setMainWindow(MainWindowController mainWindow) {
        this.mainWindow = mainWindow;
    }
    
    

    
}
