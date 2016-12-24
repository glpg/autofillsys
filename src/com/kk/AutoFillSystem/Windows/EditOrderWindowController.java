/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.AutoFillSystem;
import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Orderlines;
import com.kk.AutoFillSystem.Database.Entities.Orders;
import com.kk.AutoFillSystem.Database.Entities.Products;
import com.kk.AutoFillSystem.Database.Entities.Stores;
import com.kk.AutoFillSystem.utility.ComboBoxListener;
import com.kk.AutoFillSystem.utility.JoinRecord;
import com.kk.AutoFillSystem.utility.Product;
import static com.kk.AutoFillSystem.utility.Tools.expandInfo;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Yi
 */
public class EditOrderWindowController implements Initializable {
    private DataController dataCenter;
    private MainWindowController mainWindow;
    
    private JoinRecord record;
    private ArrayList<Orderlines> orderlines;
    
    private EditOrderWindowController instance;
    
   
    
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
    public EditOrderWindowController(JoinRecord record){
        dataCenter = DataController.getInstance();
        
        orderlines = new ArrayList();
        this.record = record;
        instance = this;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
        //buttons
        buttonCreateOrder.setOnAction(e->{editOrder(e);});
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
        
        //get currrent order
        Orders order = record.getOrder();
        
        //set up known info
        
        buttonCreateOrder.setText("Update Order");
        
        Instant instant = Instant.ofEpochMilli(record.getOrderDate().getTime());
        
        LocalDate localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
        datePicker.setValue(localDate);
        textFieldOrderNum.setText(order.getOrderNum());
        comboBoxStore.setValue(order.getStoreId().getName());
        if(order.getOrderlinesCollection() != null && order.getOrderlinesCollection().size() > 0) {
            
            listViewOrderlines.getItems().addAll(order.getOrderlinesCollection());
            
        }
        
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
        
        listViewOrderlines.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2)
                {
                    Orderlines select = (Orderlines) listViewOrderlines.getSelectionModel().getSelectedItem();
                    
                    Stage stage = new Stage();
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(AutoFillSystem.class.getResource("Windows/EditOrderlineWindow.fxml"));
            
                    EditOrderlineWindowController controller = new EditOrderlineWindowController(select, instance);
            
                    loader.setController(controller);
                    AnchorPane window;
                    try {
                        window = (AnchorPane) loader.load();
                        stage.setScene(new Scene(window));
                        stage.setTitle("Edit orderline");
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.initOwner(((Node)event.getSource()).getScene().getWindow());
                        stage.show();
                    } catch (IOException ex) {
                        Logger.getLogger(EditOrderWindowController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
         
                }
            }
        });
            
        
        
        //set disable
        datePicker.setDisable(true);
        textFieldOrderNum.setDisable(true);
        comboBoxStore.setDisable(true);
        
        
        
        
        
        
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
        listViewOrderlines.getItems().removeAll(orderlines);
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
    
    private void editOrder(ActionEvent e){
        
        //add new orderlines
        for(Orderlines temp : orderlines) {
            temp.setOrderId(record.getOrder());
            dataCenter.createOrderline(temp);
            record.getOrder().getOrderlinesCollection().add(temp);
        }
        

        for (JoinRecord tblRecord : mainWindow.getTableRows()) {
            if (tblRecord.getOrderNum().equals(record.getOrderNum())) {
                expandInfo(tblRecord);
            }
        }
        
        showAlert("Success", "Order is updated :" , "Order is updated successfully !");
        //forcing refreshing table
        mainWindow.getOrderTable().getColumns().get(0).setVisible(false);
        mainWindow.getOrderTable().getColumns().get(0).setVisible(true);
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

    public ListView getListViewOrderlines() {
        return listViewOrderlines;
    }
    
    

    
}
