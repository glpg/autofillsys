/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Orders;
import com.kk.AutoFillSystem.Database.Entities.Products;
import com.kk.AutoFillSystem.Database.Entities.Stores;
import com.kk.AutoFillSystem.utility.Order;
import com.kk.AutoFillSystem.utility.Product;
import static com.kk.AutoFillSystem.utility.Tools.showAlert;
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
import javafx.scene.control.TextArea;
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
    private Order orderInfo;
    private ArrayList<Product> prods;
   
    
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
    private TextArea textAreaOrderlines;
    @FXML
    private Button buttonCreateOrder;
    @FXML
    private Button buttonCancel;
    
    
    //constructor
    public OrderWindowController(){
        dataCenter = DataController.getInstance();
        prods = new ArrayList();
        orderInfo = new Order();
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
        
        
        
        
    }    
    
    
    private void addItem() {
        
        try{
            String prodNum = comboBoxItem.getValue();
            if(prodNum == null || prodNum.length() == 0) {
                showAlert("Error", "Product Error :" , "You did not select product !");
                return;
            } 
            int count = Integer.parseInt(textFieldQuantity.getText());
            textAreaOrderlines.appendText(prodNum + " : " + count);
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
        textAreaOrderlines.clear();
        //clear saved products
        prods.clear();
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
        
        orderInfo.orderNum = textFieldOrderNum.getText();
        if (comboBoxStore.getValue() == null || comboBoxStore.getValue().length() == 0) {
            showAlert("Error", "Order Store Error :" , "Order store is required!");
            return;
        }
        
        orderInfo.storeName = comboBoxStore.getValue();
        LocalDate date = datePicker.getValue();
        if (date != null) {
            Date orderDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            orderInfo.orderDate = orderDate;
            
        }
        
        orderInfo.products = prods;
        
        Orders newOrder = dataCenter.createOrder(orderInfo);
        
        if (newOrder == null) {
            showAlert("Failed", "Creating Failed :" , "The order could not be created !");
        }
        else {
            showAlert("Success", "Record Created :" , "New order is created successfully !");
            mainWindow.getOrders().add(newOrder);
            //need to update mainwindow
            mainWindow.addNewOrder(newOrder);
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
