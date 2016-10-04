/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.AutoFillSystem;
import static com.kk.AutoFillSystem.AutoFillSystem.primaryStage;
import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Addresses;
import com.kk.AutoFillSystem.Database.Entities.Carriers;
import com.kk.AutoFillSystem.Database.Entities.Cntrkings;
import com.kk.AutoFillSystem.Database.Entities.Orders;
import com.kk.AutoFillSystem.Database.Entities.Products;
import com.kk.AutoFillSystem.Database.Entities.Stores;
import com.kk.AutoFillSystem.Database.Entities.Usanduscntrkings;
import com.kk.AutoFillSystem.Database.Entities.Ustocntrkings;
import com.kk.AutoFillSystem.Database.Entities.Ustrkings;
import com.kk.AutoFillSystem.utility.JoinRecord;
import static com.kk.AutoFillSystem.utility.LoggingAspect.addMessage;
import com.kk.AutoFillSystem.utility.TableFilter;
import static com.kk.AutoFillSystem.utility.Tools.expandInfo;
import static com.kk.AutoFillSystem.utility.Tools.readFileLines;
import static com.kk.AutoFillSystem.utility.Tools.showAlert;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

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
    
    //new order and new trk
    private Set<String> newOrders = new HashSet();
    private Set<String> newShipments = new HashSet();
    private Set<String> newIntlShipments = new HashSet();
    
    
    //records combining orders and trkings
    ArrayList<JoinRecord> records;
    
    //observable list 
    private ObservableList<JoinRecord> tableRows;
    
    //cp and paste intl trk
    private Ustocntrkings copyStoreIntlTrk;
    
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
    private MenuItem menuItemNewProduct;
     @FXML
    private MenuItem menuItemNewStore;
    @FXML
    private MenuItem menuItemNewAddr;
    @FXML
    private MenuItem menuItemNewCarrier;
    @FXML
    private MenuItem menuItemSyncEmails;
    @FXML
    private MenuItem menuItemReloadTable;
    @FXML
    private MenuItem menuItemEditOrder;
    @FXML
    private MenuItem menuItemEditUsTrk;
    @FXML
    private MenuItem menuItemExportTable;
    @FXML
    private MenuItem menuItemSyncHDB;
    @FXML
    private MenuItem menuItemOpenWebpage;
    @FXML
    private MenuItem menuItemQuit;
    @FXML
    private MenuItem menuItemConfirmDelivery;
    @FXML
    private MenuItem menuItemAbout;
    @FXML
    private MenuItem menuItemStat;
    
    
    
    
    //filter area
    @FXML
    private ComboBox comboBoxStore;
    @FXML
    private ComboBox comboBoxWarehouse;
    @FXML
    private ComboBox<String> comboBoxProduct;
    @FXML
    private ComboBox comboBoxUnshipped;
    @FXML
    private ComboBox comboBoxDestination;
    @FXML
    private Button btnApplyFilter;
    @FXML
    private Button btnClearFilter;
    @FXML
    private TextField textFieldSearchNum;
    @FXML
    private Button buttonSearch;
    @FXML
    private Button buttonReset;
    @FXML
    private ComboBox<String> comboBoxSearch;
    
    
    //tableview
    @FXML
    private TableView<JoinRecord> orderTable;
    @FXML
    private TableColumn<JoinRecord, Number> rowNumber;
    @FXML
    private TableColumn<JoinRecord, String> store;
    @FXML
    private TableColumn<JoinRecord, String> orderNum;
    @FXML
    private TableColumn<JoinRecord, Date> orderDate;
    @FXML
    private TableColumn<?, ?> orderList;
    @FXML
    private TableColumn<?, ?> usCarrier;
    @FXML
    private TableColumn<JoinRecord, String> usTrkNum;
    @FXML
    private TableColumn<?, ?> shipList;
   
    @FXML
    private TableColumn<?, ?> warehouse;
    @FXML
    private TableColumn<JoinRecord, String> intlTrkNum;
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
    @FXML
    private TableColumn<JoinRecord, Boolean> delivered;
   
    
    
    //constructor
    public MainWindowController() {
        dataCenter = DataController.getInstance();

        loadData();
        instance = this;
    }
    
    
    private void loadData() {
        
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
        for(JoinRecord record : records) {
            expandInfo(record);
        }
        
        //tableRows data.
        tableRows = FXCollections.observableArrayList(records);
        
        
    }
    
    public void reloadTable() {
        dataCenter.clearCache();
        loadData();
        orderTable.setItems(tableRows);
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
            record.setOrder(order);
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
            record.setUsTrk(ustrk);
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
                temp.setCnTrk(cnTrk);
                records.add(temp);
            }
            
        }
        
        for(JoinRecord record : records) {
            record.setIntlTrk(intlTrk);
        }
        
        return records;
        
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
        
        comboBoxUnshipped.getItems().addAll("US trking", "Intl trking", "CN trking", "Delivered");
        
        ObservableList<String> destinationList = FXCollections.observableArrayList();
        for(Addresses dest: addresses) {
            if (dest.getCountry() != null && dest.getCountry().equals("CN"))
                destinationList.add(dest.getName());
        }
        FXCollections.sort(destinationList);
        comboBoxDestination.setItems(destinationList);
        
        comboBoxSearch.getItems().addAll("US Track", "Order", "Intl Track", "CN Track");
        comboBoxSearch.setValue("US Track");
        
        //set up buttons
        btnApplyFilter.setOnAction(e->{applyFilter();});
        btnClearFilter.setOnAction(e->{clearFilter();});
        
        buttonSearch.setOnAction(e->{search();});
        buttonReset.setOnAction(e->{reset();});
        
        //set up enter event for search text
        textFieldSearchNum.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ENTER)
                search();
        });
        
    }
    
    private void search(){
        if(textFieldSearchNum.getText() == null || textFieldSearchNum.getText().isEmpty() ) {
            showAlert("Error", "Search field Error: ", "You did not input a number for search !", AlertType.ERROR);
            return;
        }
        ObservableList<JoinRecord> results = FXCollections.observableArrayList();
        for (JoinRecord record: tableRows) {
            String recordValue ;
            switch (comboBoxSearch.getValue()) {
                case "US Track" : {
                    recordValue = record.getUsTrkNum();
                    break;
                }
                case "Order" :{
                    recordValue = record.getOrderNum();
                    break;
                }
                
                case "CN Track" :{
                    recordValue = record.getCnTrkNum();
                    break;
                }
                default: {
                    recordValue = record.getIntlTrkNum();
                }
            
            
            }
            
            
            if (recordValue != null && recordValue.contains(textFieldSearchNum.getText())) {
                results.add(record);
            }

        }
        orderTable.setItems(results);

        
        
    }
    
   
    
    private void reset(){
        orderTable.setItems(tableRows);
        textFieldSearchNum.clear();
        
    }
    
    private void applyFilter(){
        String selectedStore = (comboBoxStore.getValue() == null)? null : (String)comboBoxStore.getValue();
        String selectedWarehouse = (comboBoxWarehouse.getValue() == null)? null : (String)comboBoxWarehouse.getValue();
        String selectedProduct = (comboBoxProduct.getValue() == null)? null : (String)comboBoxProduct.getValue();
        String selectedUnshipped = (comboBoxUnshipped.getValue() == null)? null : (String)comboBoxUnshipped.getValue();
        String selectedDestination = (comboBoxDestination.getValue() == null)? null : (String)comboBoxDestination.getValue();
        
        if (selectedStore != null)
            filter.setStoreFilter(selectedStore);
        
        if (selectedWarehouse != null)
            filter.setWarehouseFilter(selectedWarehouse);
        
        if (selectedProduct != null)
            filter.setProdutFilter(selectedProduct);
        
        if (selectedUnshipped != null) 
            filter.setUnshippedFilter(selectedUnshipped);
        
        if (selectedDestination != null) 
            filter.setDestinationFilter(selectedDestination);
        
        filter.applyFilters();
    }    
    
    private void clearFilter() {
        
        comboBoxStore.setValue(null);
        comboBoxProduct.setValue(null);
        comboBoxWarehouse.setValue(null);
        comboBoxUnshipped.setValue(null);
        comboBoxDestination.setValue(null);
        filter.clearFilters();
        orderTable.setItems(tableRows);
        
    }
    
    //set up all menu items
    private void setupMenu() {
        menuItemNewOrder.setOnAction(e->{showOrderWindow(e);});
        menuItemNewUsTrk.setOnAction(e->{showUsTrkWindow(e);});
        menuItemNewIntlTrk.setOnAction(e->{showIntlTrkWindow(e);});
        menuItemNewCnTrk.setOnAction(e->{showCnTrkWindow(e);});
        menuItemNewProduct.setOnAction(e->{showProdWindow(e);});
        menuItemNewCarrier.setOnAction(e->{showCarrierWindow(e);});
        menuItemNewAddr.setOnAction(e->{showAddressWindow(e);});
        menuItemNewStore.setOnAction(e->{showStoreWindow(e);});
        
        menuItemSyncEmails.setOnAction(e->{showSyncWindow(e);});
        menuItemSyncHDB.setOnAction(e->{syncHDB();});
        
        menuItemEditOrder.setOnAction(e->{showEditOrderWindow(e);});
        menuItemEditUsTrk.setOnAction(e->{showEditUsTrkWindow(e);});
        
        menuItemExportTable.setOnAction(e->{exportTable();});
        menuItemReloadTable.setOnAction(e->{reloadTable();});
        
        
        menuItemOpenWebpage.setOnAction(e->{showWebWindow();});
        menuItemQuit.setOnAction(e->{systemQuit(e);});
        
        menuItemConfirmDelivery.setOnAction(e->{confirmDelivery();});
        
        menuItemStat.setOnAction(e->{showStat();});
       
    }
    
    private void showStat(){
        if (comboBoxProduct.getValue() == null ||comboBoxProduct.getValue().isEmpty()) {
            showAlert("Error", "Missing Product :" , "Product number is required for statistics summary !", AlertType.ERROR);
            return;
        }
        String prod  = comboBoxProduct.getValue();
        int orderCnt = 0; 
        int shipCnt = 0; 
        
        Pattern pa = Pattern.compile(prod +" : ([0-9]+)");
        
        
        ObservableList<JoinRecord> items = orderTable.getItems();
        Set<String> orders = new HashSet();
        Set<String> trks = new HashSet();
        
        
        for(JoinRecord record: items) {
            if(!orders.contains(record.getOrderNum())) {
                orders.add(record.getOrderNum());
                //count in numbers
                Matcher m = pa.matcher(record.getOrderList());
                while(m.find()) {
                    int cnt = Integer.parseInt(m.group(1));
                    orderCnt += cnt;
                }
                
            }
            
            if(record.getUsTrk()!= null && !trks.contains(record.getUsTrkNum())) {
                trks.add(record.getUsTrkNum());
                //count in numbers
                Matcher m = pa.matcher(record.getShipList());
                if(m.find()) {
                    int cnt = Integer.parseInt(m.group(1));
                    shipCnt += cnt;
                }
            }
            
            

        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Ordered : ").append(orderCnt).append("\n").append("Shipped : ").append(shipCnt).append("\n");
        
        showAlert("Stat Summary", prod + " Stats :",sb.toString(), AlertType.INFORMATION);
        
    }
    
    
    private void confirmDelivery() {
        ObservableList<JoinRecord> selectedRows = orderTable.getSelectionModel().getSelectedItems();
        if (selectedRows == null || selectedRows.size() == 0) return;
        else {
            Set<String> trkNums = new HashSet();
            for(JoinRecord tmp : selectedRows) {
                if (tmp.getCnTrk() == null) continue;
                else {
                    if (trkNums.contains(tmp.getCnTrk().getTrkingNum())) {
                        expandInfo(tmp);
                    }
                    else {
                        Cntrkings cntrk = tmp.getCnTrk();
                        if (!cntrk.getDelivered()) {
                            cntrk.setDelivered(true);
                            dataCenter.updateDelivery(cntrk);
                            expandInfo(tmp);
                            trkNums.add(cntrk.getTrkingNum());
                        }
                        else {
                            if (! tmp.getDelivered()) tmp.setDelivered(true);
                        }
                        
                        
                    }
                }
            }
            
            //force table reloading
            //forcing refreshing table
            orderTable.getColumns().get(0).setVisible(false);
            orderTable.getColumns().get(0).setVisible(true);
        }
        
    }
    
    private void systemQuit(ActionEvent e) {
        
        Platform.exit();
    }
    
    private void showWebWindow() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AutoFillSystem.class.getResource("Windows/WebWindow.fxml"));
            
            WebWindowController webController = new WebWindowController();
            webController.setMainWindow(instance);
            
            loader.setController(webController);
            AnchorPane webWindow = (AnchorPane) loader.load();
            

            stage.setScene(new Scene(webWindow));
            stage.setTitle("Web browser");
//            stage.initModality(Modality.WINDOW_MODAL);
//            stage.initOwner(AutoFillSystem.primaryStage);
            stage.show();
            stage.toFront();
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        
        
        
    }
    
    
    private void syncHDB() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(primaryStage);
        
        if (file != null) {
            List<String> lines = readFileLines(file);

            for (int i = 0; i < lines.size(); i += 4) {
                double shippingfee = 0.0;
                //us trk
                ArrayList<String> ustrks = new ArrayList();
                Pattern trkP = Pattern.compile("([a-zA-Z0-9]+)");
                Matcher m1 = trkP.matcher(lines.get(i));
                while (m1.find()) {
                    ustrks.add(m1.group(1));
                }

                //shipping fee
                Pattern feeP = Pattern.compile("([0-9.]+)");
                Matcher m = feeP.matcher(lines.get(i + 2));
                if (m.find()) {
                    shippingfee = Double.parseDouble(m.group(1));
                }

                //intl trk and weight
                String[] info = lines.get(i + 3).split("\\s+");
                String intlTrk = info[1];
                int weight = (int) (Double.parseDouble(info[2]) * 1000.0);

                if (addIntlTrk(ustrks, intlTrk, weight, shippingfee, "HDB")) {
                    showAlert("Success", "Update Finished :" , "HDB tracking " + intlTrk + " is updated successfully !", AlertType.INFORMATION);
                }
                else{
                    showAlert("Failed", "Update Error :" , "HDB tracking " + intlTrk + " already existed !", AlertType.WARNING);
                }
                
                
            }

        }

    }
    
    
    public boolean addIntlTrk(List<String> ustrks, String intlTrkNum, int weight, double fee, String warehouseName){
        if (dataCenter.getIntlTrking(intlTrkNum) == null || dataCenter.getIntlTrking(intlTrkNum).size() == 0) {
            Ustocntrkings intlTrk = new Ustocntrkings();
            intlTrk.setTrkingNum(intlTrkNum);
            intlTrk.setWeight(weight);
            for (Addresses addr : warehouses) {
                if (addr.getName().equals(warehouseName)) {
                    intlTrk.setAddressId(addr);
                    break;
                }
            }

            if (fee != 0.0) {
                intlTrk.setShippingfee(BigDecimal.valueOf(fee));
            }
            dataCenter.createIntlTrking(intlTrk);
            newIntlShipments.add(intlTrkNum);

            for (String ustrkNum : ustrks) {
                for (JoinRecord record : tableRows) {
                    if (record.getUsTrk() != null && record.getUsTrkNum().equalsIgnoreCase(ustrkNum) && record.getIntlTrk() == null) {
                        
                        dataCenter.createUsAndIntlRelation(intlTrk, record.getUsTrk());
                        record.setIntlTrk(intlTrk);
                        expandInfo(record);
                    }
                }
            }
            
            return true;

        }
        
        else {
            addMessage("Intl Tracking " + intlTrkNum + " existed already, pass!");
            return false;
        }
        
        
        
        
        
     
    }
    
    private void showSyncWindow(ActionEvent e) {
	
            
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AutoFillSystem.class.getResource("Windows/SyncWindow.fxml"));
            
            SyncWindowController syncController = new SyncWindowController();
            syncController.setMainWindow(instance);
            
            loader.setController(syncController);
            AnchorPane syncWindow = (AnchorPane) loader.load();
            

            stage.setScene(new Scene(syncWindow));
            stage.setTitle("Sync Data From Emails");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(AutoFillSystem.primaryStage);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

       
    }
    
    
    private void showAddressWindow(ActionEvent e) {
	
            
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AutoFillSystem.class.getResource("Windows/NewAddressWindow.fxml"));
            
            NewAddressWindowController addrController = new NewAddressWindowController();
            addrController.setMainWindow(instance);
            
            loader.setController(addrController);
            AnchorPane addrWindow = (AnchorPane) loader.load();
            

            stage.setScene(new Scene(addrWindow));
            stage.setTitle("Create Address");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(AutoFillSystem.primaryStage);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

       
    }
    
    private void showCarrierWindow(ActionEvent e) {
	
            
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AutoFillSystem.class.getResource("Windows/NewCarrierWindow.fxml"));
            
            NewCarrierWindowController carrierController = new NewCarrierWindowController();
            carrierController.setMainWindow(instance);
            
            loader.setController(carrierController);
            AnchorPane carrierWindow = (AnchorPane) loader.load();
            

            stage.setScene(new Scene(carrierWindow));
            stage.setTitle("Create Carrier");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(AutoFillSystem.primaryStage);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

       
    }
    
    
    
    private void showStoreWindow(ActionEvent e) {
	
            
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AutoFillSystem.class.getResource("Windows/NewStoreWindow.fxml"));
            
            NewStoreWindowController storeController = new NewStoreWindowController();
            storeController.setMainWindow(instance);
            
            loader.setController(storeController);
            AnchorPane storeWindow = (AnchorPane) loader.load();
            

            stage.setScene(new Scene(storeWindow));
            stage.setTitle("Create Store");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(AutoFillSystem.primaryStage);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

       
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
    
    
    private void showEditOrderWindow(ActionEvent e){
	try {
     
            JoinRecord currentRecord = (JoinRecord) orderTable.getSelectionModel().getSelectedItem();
            if (currentRecord == null) {
                showAlert("Error", "Order Error :" , "You did not select any order in the table !", AlertType.ERROR);
                return;
            }
            
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AutoFillSystem.class.getResource("Windows/OrderWindow.fxml"));
            
            EditOrderWindowController orderController = new EditOrderWindowController(currentRecord);
            orderController.setMainWindow(instance);
            
            loader.setController(orderController);
            AnchorPane orderWindow = (AnchorPane) loader.load();
            

            stage.setScene(new Scene(orderWindow));
            stage.setTitle("Edit order");
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
                showAlert("Error", "Order Error :" , "You did not select any order in the table !", AlertType.ERROR);
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
    
    private void showEditUsTrkWindow(ActionEvent e){
	try {
     
            JoinRecord currentRecord = (JoinRecord) orderTable.getSelectionModel().getSelectedItem();
            if (currentRecord == null || currentRecord.getUsTrk() == null) {
                showAlert("Error", "Record Error :" , "You did not select any record or the record does not have US tracking !", AlertType.ERROR);
                return;
            }
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AutoFillSystem.class.getResource("Windows/UsTrkWindow.fxml"));
            
            EditUsTrkWindowController usTrkController = new EditUsTrkWindowController(currentRecord);
            usTrkController.setMainWindow(instance);
            
            loader.setController(usTrkController);
            AnchorPane usTrkWindow = (AnchorPane) loader.load();
            

            stage.setScene(new Scene(usTrkWindow));
            stage.setTitle("Edit US tracking");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(AutoFillSystem.primaryStage);
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        

    }
    
    private void showIntlTrkWindow(ActionEvent e){
	try {
     
            JoinRecord currentRecord = (JoinRecord) orderTable.getSelectionModel().getSelectedItem();
            if (currentRecord == null || currentRecord.getUsTrk() == null) {
                showAlert("Error", "Record Error :" , "You did not select any record or the record does not have US trking !", AlertType.ERROR);
                return;
            }
            
            
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AutoFillSystem.class.getResource("Windows/IntlTrkWindow.fxml"));
            
            IntlTrkWindowController intlTrkController = new IntlTrkWindowController(currentRecord);
            intlTrkController.setMainWindow(instance);
            
            loader.setController(intlTrkController);
            AnchorPane intlTrkWindow = (AnchorPane) loader.load();
            

            stage.setScene(new Scene(intlTrkWindow));
            stage.setTitle("Create International tracking");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(AutoFillSystem.primaryStage);
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        

    }
    
    private void showCnTrkWindow(ActionEvent e){
	try {
     
            JoinRecord currentRecord = (JoinRecord) orderTable.getSelectionModel().getSelectedItem();
            if (currentRecord == null || currentRecord.getIntlTrk() == null) {
                showAlert("Error", "Record Error :" , "You did not select any record or the record does not have international trking !", AlertType.ERROR);
                return;
            }
            
            
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AutoFillSystem.class.getResource("Windows/CnTrkWindow.fxml"));
            
            CnTrkWindowController cnTrkController = new CnTrkWindowController(currentRecord);
            cnTrkController.setMainWindow(instance);
            
            loader.setController(cnTrkController);
            AnchorPane cnTrkWindow = (AnchorPane) loader.load();
            

            stage.setScene(new Scene(cnTrkWindow));
            stage.setTitle("Create China tracking");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(AutoFillSystem.primaryStage);
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
   

    }
    
    private void showProdWindow(ActionEvent e) {
	
            
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AutoFillSystem.class.getResource("Windows/NewProdWindow.fxml"));
            
            NewProdWindowController prdController = new NewProdWindowController();
            prdController.setMainWindow(instance);
            
            loader.setController(prdController);
            AnchorPane cnTrkWindow = (AnchorPane) loader.load();
            

            stage.setScene(new Scene(cnTrkWindow));
            stage.setTitle("Create Product");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(AutoFillSystem.primaryStage);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

       
    }
    
    
    private void exportTable() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save table data");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(primaryStage);
        System.out.println(file);
        
        if (file != null) {
            Writer writer = null;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-YYYY");
                writer = new BufferedWriter(new FileWriter(file));
                for (JoinRecord record : tableRows) {
                    String weight =""; 
                    String fee = "";
                    if (record.getWeight() != 0) weight = Integer.toString(record.getWeight()) ;
                    if (record.getFee() != 0.0) weight = Double.toString(record.getFee());
                    
                    String text = format(record.getStore())+ "," + format(record.getOrderNum()) + "," + 
                            format(record.getOrderDate()) +","+ format(record.getOrderList()) + ", " +
                            format(record.getUsCarrier()) + "," + format(record.getUsTrkNum()) + "," +
                            format(record.getShipList()) + "," + format(record.getWarehouse()) + "," +
                            format(record.getIntlTrkNum()) + "," + weight + "," +
                            fee + "," + format(record.getCnCarrier()) + "," + format(record.getCnTrk()) +"," +
                            format(record.getAddress()) +"\n";

                    writer.write(text);
                    
                }
                
                writer.flush();
                writer.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            } 
        }
        
        showAlert("Success", "Table Exported :" , "Table data is exported successfully !", AlertType.INFORMATION);
        
        
    }
    
    private String format(Object obj) {
        if (obj == null) return "";
        else {
            if (obj instanceof String) return  (String)obj ;
            if (obj instanceof Date) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-YYYY");
                return sdf.format((Date)obj);
            
            }
            
        }
        return "";
    }
    
    
   
    
    private void setupTable()
    {
         orderTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
         
        
        //set up cellvalue factory
        rowNumber.setCellValueFactory(column-> new ReadOnlyObjectWrapper<Number>(orderTable.getItems().indexOf(column.getValue()) +1));
        orderNum.setCellValueFactory(new PropertyValueFactory("orderNum"));
        orderDate.setCellValueFactory(new PropertyValueFactory("orderDate"));
        store.setCellValueFactory(new PropertyValueFactory("store"));
        orderList.setCellValueFactory(new PropertyValueFactory("orderList"));
        usCarrier.setCellValueFactory(new PropertyValueFactory("usCarrier"));
        usTrkNum.setCellValueFactory(new PropertyValueFactory("usTrkNum"));
        shipList.setCellValueFactory(new PropertyValueFactory("shipList"));
        
        warehouse.setCellValueFactory(new PropertyValueFactory("warehouse"));
        
        intlTrkNum.setCellValueFactory(new PropertyValueFactory("intlTrkNum"));
        weight.setCellValueFactory(new PropertyValueFactory("weight"));
        fee.setCellValueFactory(new PropertyValueFactory("fee"));
        
        
        cnCarrier.setCellValueFactory(new PropertyValueFactory("cnCarrier"));
        cnTrkNum.setCellValueFactory(new PropertyValueFactory("cnTrkNum"));
        address.setCellValueFactory(new PropertyValueFactory("address"));
        
        delivered.setCellValueFactory(new PropertyValueFactory("delivered"));
        
        orderNum.setCellFactory(column -> {
            return new TableCell<JoinRecord, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || item.isEmpty())
                        setText(null);
                    else {
                        if (newOrders.contains(item)){
                            setStyle("-fx-text-fill: blue; -fx-font-weight:bold;");
                            System.out.println("new order :" + item);
                        }
                        else
                            setStyle("-fx-text-fill: black");
                            
                        
                        setText(item);
                    }
                   

                }
            };
        });
        
        delivered.setCellFactory(column -> {
            return new TableCell<JoinRecord, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null)
                        setText(null);
                    else {
                        if (item){
                            setStyle("-fx-text-fill: #008000; -fx-font-weight:bold;");
                            setText("Yes");
                            
                        }
                        else{
                            setStyle("-fx-text-fill: red; -fx-font-weight:bold;");
                            setText("No");
                        }
                            
                       
                    }
                   

                }
            };
        });
        
        
        
        
        intlTrkNum.setCellFactory(column -> {
            return new TableCell<JoinRecord, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || item.isEmpty())
                        setText(null);
                    else {
                        if (newIntlShipments.contains(item)){
                            setStyle("-fx-text-fill: red; -fx-font-weight:bold;");
                            System.out.println("new intl trk :" + item);
                        }
                        else
                            setStyle("-fx-text-fill: black");
                            
                        
                        setText(item);
                    }
                   

                }
            };
        });
        
        
        usTrkNum.setCellFactory(column -> {
            return new TableCell<JoinRecord, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || item.isEmpty())
                        setText(null);
                    else {
                        if (newShipments.contains(item)){
                            System.out.println("new Ship :" + item);
                            setStyle("-fx-text-fill: #00FF00; -fx-font-weight:bold;");
                        }
                        else
                            setStyle("-fx-text-fill: black");
                            
                        
                        setText(item);
                    }
                   

                }
            };
        });
        
        
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
        
        
        //create a context menu and set it to tablerows
        ContextMenu menu = new ContextMenu();
        MenuItem addUsTrk = new MenuItem("Add Us Tracking");
        addUsTrk.setOnAction(e -> {
            showUsTrkWindow(e);
        });
        MenuItem addIntlTrk = new MenuItem("Add Intl Tracking");
        addIntlTrk.setOnAction(e -> {
            showIntlTrkWindow(e);
        });
        MenuItem addCnTrk = new MenuItem("Add Cn Tracking");
        addCnTrk.setOnAction(e -> {
            showCnTrkWindow(e);
        });
        SeparatorMenuItem line = new SeparatorMenuItem();
        MenuItem editOrder = new MenuItem("Edit Order");
        editOrder.setOnAction(e -> {
            showEditOrderWindow(e);
        });
        MenuItem editUsTrk = new MenuItem("Edit Us Tracking");
        
        editUsTrk.setOnAction(e -> {
            showEditUsTrkWindow(e);
        });
        SeparatorMenuItem line2 = new SeparatorMenuItem();
        
        
        MenuItem copyIntlTrk = new MenuItem("Copy Intl Tracking");
        copyIntlTrk.setOnAction(e -> {
            
            copyIntlTrk();
        });
        MenuItem pasteIntlTrk = new MenuItem("Paste Intl Tracking");
        
        pasteIntlTrk.setOnAction(e -> {
            pasteIntlTrk();
        });
        
        SeparatorMenuItem line3 = new SeparatorMenuItem();
        MenuItem copyToClipboard = new MenuItem("Copy US Trk to Clipboard");
        copyToClipboard.setOnAction(e -> {
            copyUsTrkToClipboard();
        });
        
        menu.getItems().addAll(addUsTrk, addIntlTrk, addCnTrk, line, editOrder, editUsTrk, line2, copyIntlTrk, pasteIntlTrk
        , line3, copyToClipboard);
        
        orderTable.setRowFactory(new Callback<TableView<JoinRecord>, TableRow<JoinRecord>>() {  
            @Override  
            public TableRow<JoinRecord> call(TableView<JoinRecord> tableView) {
                final TableRow<JoinRecord> row = new TableRow<>();
                row.setContextMenu(menu);
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        
                        TablePosition pos = orderTable.getSelectionModel().getSelectedCells().get(0);
                        int rowIndex = pos.getRow();
                        JoinRecord item = orderTable.getItems().get(rowIndex);
                        TableColumn col = pos.getTableColumn();
                        String data = col.getCellObservableValue(item).getValue().toString();
                        copyToClipboard(data);
   
                    }
                    
                });

                return row;
            }
            
            
            
        });  
        

        
        //set up table data
        orderTable.setItems(tableRows);
        
        //set up filter
        filter = new TableFilter(orderTable);
        
        
   
    }
    
    
    private void copyUsTrkToClipboard(){
        JoinRecord currentRecord = (JoinRecord) orderTable.getSelectionModel().getSelectedItem();
        String usTrkNum = currentRecord.getUsTrkNum();
        
        ClipboardContent content = new ClipboardContent();
        content.putString(usTrkNum);
        Clipboard.getSystemClipboard().setContent(content);
    }
    
    private void copyToClipboard(String value){
        
        ClipboardContent content = new ClipboardContent();
        content.putString(value);
        Clipboard.getSystemClipboard().setContent(content);
    }
    
    private void copyIntlTrk() {
        JoinRecord currentRecord = (JoinRecord) orderTable.getSelectionModel().getSelectedItem();
        copyStoreIntlTrk = currentRecord.getIntlTrk();
    }
    
    private void pasteIntlTrk() {
        if (copyStoreIntlTrk == null) {
            showAlert("Error", "Intl Trk Error :" , "No intl trking is copied, copy first !", AlertType.ERROR);
            return;
        }
        
        else{
            JoinRecord currentRecord = (JoinRecord) orderTable.getSelectionModel().getSelectedItem();
            Ustrkings ustrk = currentRecord.getUsTrk();
            dataCenter.createUsAndIntlRelation(copyStoreIntlTrk, ustrk);
            
            if (currentRecord.getIntlTrk() != null) {
                currentRecord = copyRecordforIntl(currentRecord);
                orderTable.getItems().add(currentRecord);
            }
                
              
            currentRecord.setIntlTrk(copyStoreIntlTrk);
            if (copyStoreIntlTrk.getCntrkingsCollection().size() == 1)
                currentRecord.setCnTrk((Cntrkings) copyStoreIntlTrk.getCntrkingsCollection().toArray()[0]);
            
            if (copyStoreIntlTrk.getCntrkingsCollection().size() > 1) {
                Cntrkings[] cntrks = (Cntrkings[]) copyStoreIntlTrk.getCntrkingsCollection().toArray();
                currentRecord.setCnTrk((Cntrkings) copyStoreIntlTrk.getCntrkingsCollection().toArray()[0]);
                for(int i = 1; i < cntrks.length; i++) {
                    JoinRecord newRecord = new JoinRecord();
                    newRecord.setOrder(currentRecord.getOrder());
                    newRecord.setUsTrk(currentRecord.getUsTrk());
                    newRecord.setIntlTrk(copyStoreIntlTrk);
                    newRecord.setCnTrk(cntrks[i]);
                    expandInfo(newRecord);
                    orderTable.getItems().add(newRecord);
                }
            }
            
            
            
            expandInfo(currentRecord);
            
            //force refreshing
            //forcing refreshing table
            orderTable.getColumns().get(0).setVisible(false);
            orderTable.getColumns().get(0).setVisible(true);
            //set copystore to null
            copyStoreIntlTrk = null;
        }
            
        
    }
    
    
    private JoinRecord copyRecordforIntl(JoinRecord record) {
        JoinRecord copy = new JoinRecord();
        copy.setOrder(record.getOrder());
        copy.setUsTrk(record.getUsTrk());
        expandInfo(copy);
        return copy;
        
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

    public ObservableList<JoinRecord> getTableRows() {
        return tableRows;
    }

    public void setTableRows(ObservableList<JoinRecord> tableRows) {
        this.tableRows = tableRows;
    }

    public TableView<?> getOrderTable() {
        return orderTable;
    }

    public ComboBox getComboBoxStore() {
        return comboBoxStore;
    }

    public ComboBox getComboBoxWarehouse() {
        return comboBoxWarehouse;
    }

    public ComboBox getComboBoxProduct() {
        return comboBoxProduct;
    }

    public ComboBox getComboBoxDestination() {
        return comboBoxDestination;
    }

    public Set<String> getNewOrders() {
        return newOrders;
    }

    public void setNewOrders(Set<String> newOrders) {
        this.newOrders = newOrders;
    }

    public Set<String> getNewShipments() {
        return newShipments;
    }

    public void setNewShipments(Set<String> newShipments) {
        this.newShipments = newShipments;
    }


 
    
}
        
    



