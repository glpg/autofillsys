/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.AutoFillSystem;
import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Addresses;
import com.kk.AutoFillSystem.Database.Entities.Carriers;
import com.kk.AutoFillSystem.Database.Entities.Cntrkings;
import com.kk.AutoFillSystem.Database.Entities.Orders;
import com.kk.AutoFillSystem.Database.Entities.Products;
import com.kk.AutoFillSystem.Database.Entities.Stores;
import com.kk.AutoFillSystem.Database.Entities.Trklines;
import com.kk.AutoFillSystem.Database.Entities.Usanduscntrkings;
import com.kk.AutoFillSystem.Database.Entities.Ustocntrkings;
import com.kk.AutoFillSystem.Database.Entities.Ustrkings;
import com.kk.AutoFillSystem.utility.JoinRecord;
import com.kk.AutoFillSystem.utility.LoggingAspect;
import com.kk.AutoFillSystem.utility.TableFilter;
import static com.kk.AutoFillSystem.utility.Tools.showAlert;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Yi
 */
public class MainWindowController implements Initializable {
    
    private DataController dataCenter;
    private TableFilter filter;
    private MainWindowController instance;
    
    
    //get data from db
    private List<Orders> orders;
    private List<Stores> stores;
    private List<Addresses> warehouses;
    private List<Products> products;
    private  List<Carriers> carriers;
    private  List<Addresses> addresses;
    
    //records combining orders and trkings
    ArrayList<JoinRecord> records;
    
    //observable list 
    private ObservableList tableRows;
    
    //menu
    @FXML
    private MenuItem menuItemNewOrder;
     @FXML
    private MenuItem menuItemNewUsTrk;
    @FXML
    private MenuItem menuItemNewIntlTrk;
     @FXML
    private MenuItem menuItemNewCnTrk;
    @FXML
    private MenuItem menuItemApplyFilter;
    @FXML
    private MenuItem menuItemClearFilter;
    
    //filter area
    @FXML
    private ComboBox comboBoxStore;
    @FXML
    private ComboBox comboBoxWarehouse;
    @FXML
    private ComboBox comboBoxProduct;
    @FXML
    private ComboBox comboBoxUnshipped;
    @FXML
    private Button btnApplyFilter;
    @FXML
    private Button btnClearFilter;
    
    
    //tableview
    @FXML
    private TableView<?> orderTable;
    @FXML
    private TableColumn<JoinRecord, String> store;
    @FXML
    private TableColumn<JoinRecord, String> orderNum;
    @FXML
    private TableColumn<JoinRecord, Date> orderDate;
    @FXML
    private TableColumn<?, ?> usCarrier;
    @FXML
    private TableColumn<?, ?> usTrkNum;
    @FXML
    private TableColumn<?, ?> shipList;
   
    @FXML
    private TableColumn<?, ?> warehouse;
    @FXML
    private TableColumn<?, ?> intlTrkNum;
    @FXML
    private TableColumn<JoinRecord, Integer> weight;
    @FXML
    private TableColumn<JoinRecord, Double> fee;
    @FXML
    private TableColumn<?, ?> cnCarrier;
    @FXML
    private TableColumn<?, ?> cnTrkNum;
    @FXML
    private TableColumn<?, ?> address;
    
    
    
    
    //constructor
    public MainWindowController() {

        initData();
        instance = this;
    }
    
    
    private void initData() {
        dataCenter = DataController.getInstance();
        orders = dataCenter.getOrders();
        
        //get stores, products , carriers, and addresses
        stores = dataCenter.getStores();
        products = dataCenter.getProducts();
        carriers = dataCenter.getCarriers();
        addresses = dataCenter.getAddresses();
        warehouses = new ArrayList();
        
        //us addresses are warehouses
        for(Addresses addr : addresses) {
            if (addr.getCountry() != null && addr.getCountry().equals("US")) {
                warehouses.add(addr);
            }
        }
        
        //initialize JoinRecord
        records = new ArrayList();
        
        //populate JoinRecord
        for(Orders ord : orders) {
            
            records.addAll(createRecordsFromOrder(ord));
          
        }
        //tableRows data.
        tableRows = FXCollections.observableArrayList(records);
        
        System.out.println(records.size());
    }
    
    
    private List<JoinRecord> createRecordsFromOrder(Orders order) {
        ArrayList<JoinRecord> records = new ArrayList();
        
        if (order.getUstrkingsCollection() == null || order.getUstrkingsCollection().size() == 0) {
            //only one new record
            JoinRecord record = new JoinRecord();
            records.add(record);
        }
        
        else {
            for(Ustrkings ustrk : order.getUstrkingsCollection()) {
                
                records.addAll(createRecordsFromUsTrk(ustrk));
            }
        }
        
        for(JoinRecord record : records) {
            addOrderInfo(record, order);
        }
        
        return records;
        
        
    }
    
    private List<JoinRecord> createRecordsFromUsTrk(Ustrkings ustrk) {
        ArrayList<JoinRecord> records = new ArrayList();
        
        if (ustrk.getUsanduscntrkingsCollection() == null || ustrk.getUsanduscntrkingsCollection().isEmpty()) {
            JoinRecord record = new JoinRecord();
            records.add(record);
            
        }
        
        else {
            for(Usanduscntrkings relationship : ustrk.getUsanduscntrkingsCollection()) {
                
                records.addAll(createRecordsFromIntlTrk(relationship.getUstocntrkingId()));
            }
            
        }
        
        for(JoinRecord record : records) {
            addUsTrkingInfo(record, ustrk);
        }
        
        return records;
        
        
        
    }
    
    
    private List<JoinRecord> createRecordsFromIntlTrk(Ustocntrkings intlTrk) {
        ArrayList<JoinRecord> records = new ArrayList();
        
        if (intlTrk.getCntrkingsCollection() == null || intlTrk.getCntrkingsCollection().isEmpty()) {
            JoinRecord record = new JoinRecord();
            records.add(record);
            
        }
        
        else {
            for(Cntrkings cnTrk : intlTrk.getCntrkingsCollection()) {
                JoinRecord temp = new JoinRecord();
                addCnTrkingInfo(temp,cnTrk);
                records.add(temp);
            }
            
        }
        
        for(JoinRecord record : records) {
            addIntlTrkingInfo(record, intlTrk);
        }
        
        return records;
        
    }
    
    
    
    
    //set record info for order
    private void addOrderInfo(JoinRecord record, Orders order) {
        record.setOrder(order);
        record.setStore(order.getStoreId().getName());
        record.setOrderDate(order.getOrderDate());
        record.setOrderNum(order.getOrderNum());
    }
    
    
    //set record info for UStrking
    private void addUsTrkingInfo(JoinRecord record, Ustrkings ustrk) {
        record.setUsTrk(ustrk);
        record.setUsTrkNum(ustrk.getTrkingNum());
        record.setWarehouse(ustrk.getAddressId().getName());
        record.setUsCarrier(ustrk.getCarrierId().getName());
        StringBuilder sb = new StringBuilder();
        for (Trklines prd : ustrk.getTrklinesCollection()) {

            sb.append(prd.getProductId().getProdNum()).append(" : ").append(prd.getQuantity()).append(" || ");

        }

        String items = sb.toString();
        //make sure items are not empty
        if (items.length() > 0) {
            record.setShipList(items.substring(0, items.length() - 3));
        }

    }
    
    //set record info for Intltrking
    private void addIntlTrkingInfo(JoinRecord record, Ustocntrkings intlTrk) {
        record.setIntlTrk(intlTrk);
        record.setIntlTrkNum(intlTrk.getTrkingNum());
        record.setWeight(intlTrk.getWeight());
        record.setFee(intlTrk.getShippingfee().doubleValue());
    }
    
    //set record info for cntrking
    private void addCnTrkingInfo(JoinRecord record, Cntrkings cntrk) {
        record.setCnTrk(cntrk);
        record.setCnTrkNum(cntrk.getTrkingNum());
        record.setCnCarrier(cntrk.getCarrierId().getName());
        record.setAddress(cntrk.getAddressId().getName());
    }
    
    public void addNewOrder(Orders order) {
        JoinRecord record = new JoinRecord();
        record.setOrder(order);

        record.setOrderDate(order.getOrderDate());
        record.setOrderNum(order.getOrderNum());
        record.setStore(order.getStoreId().getName());

        tableRows.add(record);
        
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
        setupTable();
       
        setupMenu();
        setUpFilterPane();
    }    
    
    private void setUpFilterPane() {
        //set up all comboBox
        ObservableList<String> storeList = FXCollections.observableArrayList();
        for(Stores store: stores) {
            storeList.add(store.getName());
        }
        
        FXCollections.sort(storeList);
        comboBoxStore.setItems(storeList);
        
        ObservableList<String> productList = FXCollections.observableArrayList();
        for(Products prd: products) {
            if (!prd.getProdNum().equals("00000"))
                productList.add(prd.getProdNum());
        }
        FXCollections.sort(productList);
        comboBoxProduct.setItems(productList);
        
        ObservableList<String> warehouseList = FXCollections.observableArrayList();
        for(Addresses warehouse: warehouses) {
            warehouseList.add(warehouse.getName());
        }
        FXCollections.sort(warehouseList);
        comboBoxWarehouse.setItems(warehouseList);
        
        comboBoxUnshipped.getItems().addAll("US trking", "Intl trking", "CN trking");
        
        //set up buttons
        btnApplyFilter.setOnAction(e->{applyFilter();});
        btnClearFilter.setOnAction(e->{clearFilter();});
        
    }
    
    
    private void applyFilter(){
        String selectedStore = (comboBoxStore.getValue() == null)? null : (String)comboBoxStore.getValue();
        String selectedWarehouse = (comboBoxWarehouse.getValue() == null)? null : (String)comboBoxWarehouse.getValue();
        String selectedProduct = (comboBoxProduct.getValue() == null)? null : (String)comboBoxProduct.getValue();
        String selectedUnshipped = (comboBoxUnshipped.getValue() == null)? null : (String)comboBoxUnshipped.getValue();
        
        if (selectedStore != null)
            filter.setStoreFilter(selectedStore);
        
        if (selectedWarehouse != null)
            filter.setWarehouseFilter(selectedWarehouse);
        
        if (selectedProduct != null)
            filter.setProdutFilter(selectedProduct);
        
        filter.applyFilters();
    }    
    
    private void clearFilter() {
        
        comboBoxStore.setValue(null);
        comboBoxProduct.setValue(null);
        comboBoxWarehouse.setValue(null);
        comboBoxUnshipped.setValue(null);
        
        filter.clearFilters();
        orderTable.setItems(tableRows);
        
    }
    
    //set up all menu items
    private void setupMenu() {
        menuItemNewOrder.setOnAction(e->{showOrderWindow(e);});
        menuItemNewUsTrk.setOnAction(e->{showUsTrkWindow(e);});
        
       
    }
    
    private void showOrderWindow(ActionEvent e){
	try {
     
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AutoFillSystem.class.getResource("Windows/OrderWindow.fxml"));
            
            OrderWindowController orderController = new OrderWindowController();
            orderController.setMainWindow(instance);
            
            loader.setController(orderController);
            AnchorPane orderWindow = (AnchorPane) loader.load();
            

            stage.setScene(new Scene(orderWindow));
            stage.setTitle("Create new order");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(AutoFillSystem.primaryStage);
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        

    }
    
    
    private void showUsTrkWindow(ActionEvent e){
	try {
     
            JoinRecord currentRecord = (JoinRecord) orderTable.getSelectionModel().getSelectedItem();
            if (currentRecord == null) {
                showAlert("Error", "Order Error :" , "You did not select any order in the table !");
                return;
            }
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AutoFillSystem.class.getResource("Windows/UsTrkWindow.fxml"));
            
            UsTrkWindowController usTrkController = new UsTrkWindowController(currentRecord);
            usTrkController.setMainWindow(instance);
            
            loader.setController(usTrkController);
            AnchorPane usTrkWindow = (AnchorPane) loader.load();
            

            stage.setScene(new Scene(usTrkWindow));
            stage.setTitle("Create new US tracking");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(AutoFillSystem.primaryStage);
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        

    }
    
    
    
    
    private void setupTable()
    {
     
        
        //set up cellvalue factory
        orderNum.setCellValueFactory(new PropertyValueFactory("orderNum"));
        orderDate.setCellValueFactory(new PropertyValueFactory("orderDate"));
        store.setCellValueFactory(new PropertyValueFactory("store"));
        
        usCarrier.setCellValueFactory(new PropertyValueFactory("usCarrier"));
        usTrkNum.setCellValueFactory(new PropertyValueFactory("usTrkNum"));
        shipList.setCellValueFactory(new PropertyValueFactory("shipList"));
        
        warehouse.setCellValueFactory(new PropertyValueFactory("warehouse"));
        
        intlTrkNum.setCellValueFactory(new PropertyValueFactory("intlTrkNum"));
        weight.setCellValueFactory(new PropertyValueFactory("weight"));
        fee.setCellValueFactory(new PropertyValueFactory("fee"));
        
        weight.setCellFactory(column -> {
            return new TableCell<JoinRecord, Integer>() {
            @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || item == 0) {
                        setText(null);
               
                    } else {
                        
                        setText(item.toString());
                    }
                }
            };
        });
        
        fee.setCellFactory(column -> {
            return new TableCell<JoinRecord, Double>() {
            @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || item == 0) {
                        setText(null);
               
                    } else {
                        
                        setText(item.toString());
                    }
                }
            };
        });
        
        
        //set up date display
        orderDate.setCellFactory(column -> {
            return new TableCell<JoinRecord, Date>() {
            @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
               
                    } else {
                        SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/YY");
                        setText(sdf.format(item));
                    }
                }
            };
        });
        
        //set up table data
        orderTable.setItems(tableRows);
        
        //set up filter
        filter = new TableFilter(orderTable);
        
        
   
    }
    
    //setters and getters

    public List<Orders> getOrders() {
        return orders;
    }

    public void setOrders(List<Orders> orders) {
        this.orders = orders;
    }

    public List<Stores> getStores() {
        return stores;
    }

    public void setStores(List<Stores> stores) {
        this.stores = stores;
    }

    public List<Addresses> getWarehouses() {
        return warehouses;
    }

    public void setWarehouses(List<Addresses> warehouses) {
        this.warehouses = warehouses;
    }

    public List<Products> getProducts() {
        return products;
    }

    public void setProducts(List<Products> products) {
        this.products = products;
    }

    public List<Carriers> getCarriers() {
        return carriers;
    }

    public void setCarriers(List<Carriers> carriers) {
        this.carriers = carriers;
    }

    public List<Addresses> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Addresses> addresses) {
        this.addresses = addresses;
    }

    public ObservableList getTableRows() {
        return tableRows;
    }

    public void setTableRows(ObservableList tableRows) {
        this.tableRows = tableRows;
    }
    
    
    
    
    
    
}
        
    



