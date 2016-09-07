/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Orders;
import com.kk.AutoFillSystem.Database.Entities.Trklines;
import com.kk.AutoFillSystem.Database.Entities.Ustrkings;
import com.kk.AutoFillSystem.utility.JoinRecord;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author Yi
 */
public class MainWindowController implements Initializable {
    
    private DataController dataCenter;
    private List<Orders> orders;
    private ArrayList<JoinRecord> records;
    private ObservableList tableRows;
    
    @FXML
    private TableView<?> orderTable;
    @FXML
    private TableColumn<JoinRecord, Orders> store;
    @FXML
    private TableColumn<JoinRecord, Orders> orderNum;
    @FXML
    private TableColumn<JoinRecord, Orders> orderDate;
    @FXML
    private TableColumn<JoinRecord, Ustrkings> usCarrier;
    @FXML
    private TableColumn<JoinRecord, Ustrkings> usTrkNum;
    @FXML
    private TableColumn<JoinRecord, Trklines> item;
    @FXML
    private TableColumn<JoinRecord, Trklines> quantity;
    @FXML
    private TableColumn<JoinRecord, Ustrkings> warehouse;
    @FXML
    private TableColumn<?, ?> intlTrkNum;
    @FXML
    private TableColumn<?, ?> weight;
    @FXML
    private TableColumn<?, ?> fee;
    @FXML
    private TableColumn<?, ?> cnCarrier;
    @FXML
    private TableColumn<?, ?> cnTrkNum;
    @FXML
    private TableColumn<?, ?> address;
    
    
    
    //constructor
    public MainWindowController() {
        dataCenter = DataController.getInstance();
        orders = dataCenter.getOrders();
        records = new ArrayList();
        
        for(Orders ord : orders) {
            System.out.println(ord.getOrderNum());
            for(Ustrkings ustrk : ord.getUstrkingsCollection()) {
                System.out.println(ustrk.getTrkingNum());
                System.out.println(ustrk.getTrklinesCollection().size());
                for(Trklines prd : ustrk.getTrklinesCollection()) {
                    
                    JoinRecord record = new JoinRecord();
                    record.setOrder(ord);
                    record.setUsTrk(ustrk);
                    record.setTrkline(prd);
                    records.add(record);
                    
                }
            }
        }
        
        tableRows = FXCollections.observableArrayList(records);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
    }    
    
    
    private void setupTable()
    {
     // set up the order part
        
        orderNum.setCellValueFactory(new PropertyValueFactory<>("order"));
        orderDate.setCellValueFactory(new PropertyValueFactory<>("order"));
        store.setCellValueFactory(new PropertyValueFactory<>("order"));
        
        usCarrier.setCellValueFactory(new PropertyValueFactory<>("usTrk"));
        usTrkNum.setCellValueFactory(new PropertyValueFactory<>("usTrk"));
        item.setCellValueFactory(new PropertyValueFactory<>("trkline"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("trkline"));
        warehouse.setCellValueFactory(new PropertyValueFactory<>("usTrk"));
        
       
        orderNum.setCellFactory( col -> {
            return new TableCell<JoinRecord, Orders>() {
                @Override
                protected void updateItem(Orders item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (item == null || empty) {
                        setText(null);
                
                    } else {
                        String orderNum = item.getOrderNum();
                        setText(orderNum);
                     
                    }
                }
            };     
        });
        
        
        store.setCellFactory( col -> {
            return new TableCell<JoinRecord, Orders>() {
                @Override
                protected void updateItem(Orders item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (item == null || empty) {
                        setText(null);
                
                    } else {
                        String storeName = item.getStoreId().getName();
                        setText(storeName);
                     
                    }
                }
            };     
        });
        
        //date format to mm/dd/yy
        orderDate.setCellFactory( col -> {
            return new TableCell<JoinRecord, Orders>() {
                @Override
                protected void updateItem(Orders item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
                        setText(sdf.format(item.getOrderDate()));  
                    }
                }
            };           
        });
        
        
        usCarrier.setCellFactory( col -> {
            return new TableCell<JoinRecord, Ustrkings>() {
                @Override
                protected void updateItem(Ustrkings item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (item == null || empty) {
                        setText(null);
                
                    } else {
                        
                        setText(item.getCarrierId().getName());
                     
                    }
                }
            };     
        });
        
        
        usTrkNum.setCellFactory( col -> {
            return new TableCell<JoinRecord, Ustrkings>() {
                @Override
                protected void updateItem(Ustrkings item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (item == null || empty) {
                        setText(null);
                
                    } else {
                        
                        setText(item.getTrkingNum());
                     
                    }
                }
            };     
        });
        
        warehouse.setCellFactory( col -> {
            return new TableCell<JoinRecord, Ustrkings>() {
                @Override
                protected void updateItem(Ustrkings item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (item == null || empty) {
                        setText(null);
                
                    } else {
                        
                        setText(item.getAddressId().getName());
                     
                    }
                }
            };     
        });
        
        item.setCellFactory( col -> {
            return new TableCell<JoinRecord, Trklines>() {
                @Override
                protected void updateItem(Trklines item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (item == null || empty) {
                        setText(null);
                
                    } else {
                        setText(item.getProductId().getProdNum());
                     
                    }
                }
            };     
        });
        
        quantity.setCellFactory( col -> {
            return new TableCell<JoinRecord, Trklines>() {
                @Override
                protected void updateItem(Trklines item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (item == null || empty) {
                        setText(null);
                
                    } else {
                        
                        setText(Integer.toString(item.getQuantity()));
                     
                    }
                }
            };     
        });
        
        
        
        orderTable.setItems(tableRows);        
    }
}
