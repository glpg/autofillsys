/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Stores;
import com.kk.AutoFillSystem.EmailInfo.GetKmart;
import com.kk.AutoFillSystem.EmailInfo.GetStore;
import com.kk.AutoFillSystem.EmailInfo.GetToysrus;
import com.kk.AutoFillSystem.EmailInfo.GetWalmart;
import com.kk.AutoFillSystem.EmailInfo.GetYoyo;
import com.kk.AutoFillSystem.utility.Order;
import com.kk.AutoFillSystem.utility.Shipment;
import static com.kk.AutoFillSystem.utility.Tools.readMapFile;
import static com.kk.AutoFillSystem.utility.Tools.showAlert;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javax.mail.Message;

/**
 * FXML Controller class
 *
 * @author Yi
 */
public class SyncWindowController implements Initializable {
    private MainWindowController mainWindow;
    private DataController dataCenter;
    
    
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> comboBoxStore;
    @FXML
    private ComboBox<String> comboBoxAcct;
    @FXML
    private TextArea textAreaInfo;
    @FXML
    private Button buttonSync;
    @FXML
    private Button buttonTest;
    @FXML
    private PasswordField passwordFieldPwd;
    
    public SyncWindowController() {
        dataCenter = DataController.getInstance();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
        buttonSync.setOnAction(e->{sync(e, true);});
        buttonTest.setOnAction(e->{sync(e, false);});
        
        //load stores
        ObservableList<String> storeList = FXCollections.observableArrayList();
        for(Stores store: mainWindow.getStores()) {
            storeList.add(store.getName());
        }
        
        FXCollections.sort(storeList);
        comboBoxStore.setItems(storeList);
        
        //preload accts
        String fileName = "C:\\Users\\" + System.getProperty("user.name") + 
                    "\\Documents\\AutoFillSystem\\acct.txt";
        Map<String, String> accts = readMapFile(fileName);
        List<String> gmails = new ArrayList<String>(accts.keySet());
        ObservableList<String> acctList = FXCollections.observableArrayList(gmails);
        FXCollections.sort(acctList);
        comboBoxAcct.setItems(acctList);
        comboBoxAcct.setValue(null);
        
        //add listener to combobox
        comboBoxAcct.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String oldValue, String newValue) {
                passwordFieldPwd.setText(accts.get(newValue));
            }
        });
        
    }
    

    
    private void sync(ActionEvent e, boolean save) {
        
        //disable once start
        buttonSync.setDisable(true);
        buttonTest.setDisable(true);
        
        
        //check missing info
        LocalDate date = datePicker.getValue();
        if (date == null) {
            
            showAlert("Error", "Start Date Error :" , "Start date is required!", AlertType.ERROR);
            return;    
        }
        
        if (comboBoxStore.getValue() == null || comboBoxStore.getValue().isEmpty()) {
            showAlert("Error", "Store Error :" , "Store is required!", AlertType.ERROR);
            return;
        }
        
        
        if (comboBoxAcct.getValue() == null || comboBoxAcct.getValue().isEmpty()) {
            showAlert("Error", "Account Error :" , "Gmail account is required!", AlertType.ERROR);
            return;
        }
        
        if (passwordFieldPwd.getText() == null || passwordFieldPwd.getText().isEmpty()) {
            showAlert("Error", "Password Error :" , "Password is required!", AlertType.ERROR);
            return;
        }
        
        //get filled values
        //date
        Date startDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        SimpleDateFormat df1 = new SimpleDateFormat("MM/dd/yy");
        String sinceDate = df1.format(startDate);
        
        //store
        String store = comboBoxStore.getValue();
        
        //acct
        String account = comboBoxAcct.getValue();
        
        //pwd
        String pwd = passwordFieldPwd.getText().trim();
        
        GetStore query;
        
        switch (store) {
            case "Walmart" : {
                query = new GetWalmart(account,pwd);
                break;
            }
            case "Toysrus" : {
                query = new GetToysrus(account, pwd);
                break;
            }
            case "Kmart" :{
                query = new GetKmart(account,pwd);
                break;
            }
            case "Yoyo" :{
                System.out.println("yoyo selected");
                query = new GetYoyo(account, pwd);
                break;
            }
            default :{
                showAlert("Error", "Not Supported Error :" , store + " is not supported yet !", AlertType.WARNING);
                return;
            }
                
                
        }
        
        
        Task task = new Task<Void>() {
            @Override public Void call() {
                String msg = "";
                try {
                    
                    //connect gmail
                    msg = "Start to connect to gmail ... \n";
                    updateMessage(msg);
                    query.connectGmail();
                    msg += "Logged in sucessfully ! \n";
                    updateMessage(msg);
                    
                    //extract emails
                    msg += "Starting to retrive emails from " + store + " ... \n";
                    updateMessage(msg);
                    
                    Message[] emails = query.getMessagesSinceDate(sinceDate);
                    
                    msg += "Total " + emails.length + " emails retrieved ! \n";
                    updateMessage(msg);
                    
                    //extract order info from emails
                    for (Message email : emails) {

                        if (email.getSubject().toLowerCase().contains(query.getOrderSubject())) {
                            

                            Order order = query.extractOrder(email);
                            
                            query.getOrders().add(order);

                            msg += "\n" + order.toString() + "\n";
                            updateMessage(msg);

                        }

                        if (email.getSubject().toLowerCase().contains(query.getShipSubject())) {

                            //revise using extractshipments
                            ArrayList<Shipment> found = query.extractShipments(email);
                            for(Shipment tmp: found) {
                                msg += "\n" + tmp.toString() + "\n";
                                updateMessage(msg);
                            }
                            
                            query.getShipments().addAll(found);
                            
                            
                        }
                    }
                    
                    if (save) {
                        //finally, persist entries in db
                        for (Order orderInfo : query.getOrders()) {
                            if (dataCenter.createOrder(orderInfo) != null) {
                                msg += "\n" + "New order :" + orderInfo.orderNum + " is created successfully. \n";
                                updateMessage(msg);
                                mainWindow.getNewOrders().add(orderInfo.orderNum);
                            };
                        }

                        for (Shipment shipInfo : query.getShipments()) {
                            if (dataCenter.createUsTrking(shipInfo) != null) {
                                msg += "\n" + "New shipment :" + shipInfo.trackingNum + " is created successfully. \n";
                                updateMessage(msg);
                                mainWindow.getNewShipments().add(shipInfo.trackingNum);
                            }

                        }

                    }

                    
                    msg += "Sync is completed successfully ." ;
                    updateMessage(msg);
                    
                }
                catch(Exception ex) {
                    msg += "Failed to sync : " + ex.getMessage() + ". \n";
                    updateMessage(msg);
                }
            
                return null;
            }
        
            @Override
            protected void succeeded() {
                super.succeeded();
                buttonSync.setDisable(false);
                buttonTest.setDisable(false);
                mainWindow.reloadTable();
            }
        
        };
         
        textAreaInfo.textProperty().bind(task.messageProperty());
        
        

        
        
    }
    
    
    //getter and setter

    public MainWindowController getMainWindow() {
        return mainWindow;
    }

    public void setMainWindow(MainWindowController mainWindow) {
        this.mainWindow = mainWindow;
    }

    public TextArea getTextAreaInfo() {
        return textAreaInfo;
    }

    public void setTextAreaInfo(TextArea textAreaInfo) {
        this.textAreaInfo = textAreaInfo;
    }

   
    
    
}
