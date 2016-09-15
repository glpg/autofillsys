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
import static com.kk.AutoFillSystem.EmailInfo.GetStore.getBody;
import com.kk.AutoFillSystem.EmailInfo.GetToysrus;
import com.kk.AutoFillSystem.EmailInfo.GetWalmart;
import com.kk.AutoFillSystem.utility.Order;
import com.kk.AutoFillSystem.utility.Shipment;
import static com.kk.AutoFillSystem.utility.Tools.showAlert;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.mail.Message;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
    private TextField textFieldAcct;
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
    }    
    

    
    private void sync(ActionEvent e, boolean save) {
        
        //disable once start
        buttonSync.setDisable(true);
        buttonTest.setDisable(true);
        
        
        //check missing info
        LocalDate date = datePicker.getValue();
        if (date == null) {
            
            showAlert("Error", "Start Date Error :" , "Start date is required!");
            return;    
        }
        
        if (comboBoxStore.getValue() == null || comboBoxStore.getValue().isEmpty()) {
            showAlert("Error", "Store Error :" , "Store is required!");
            return;
        }
        
        
        if (textFieldAcct.getText() == null || textFieldAcct.getText().isEmpty()) {
            showAlert("Error", "Account Error :" , "Gmail account is required!");
            return;
        }
        
        if (passwordFieldPwd.getText() == null || passwordFieldPwd.getText().isEmpty()) {
            showAlert("Error", "Password Error :" , "Password is required!");
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
        String account = textFieldAcct.getText().trim();
        
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
            default :{
                showAlert("Error", "Not Supported Error :" , store + " is not supported yet !");
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
                    Message[] emails = query.getMessages(sinceDate);
                    msg += "Total " + emails.length + " emails retrieved ! \n";
                    updateMessage(msg);
                    
                    //extract order info from emails
                    for (Message email : emails) {

                        if (email.getSubject().contains(query.getOrderSubject())) {
                            //this part should be checked depending on stores
                            //kmart using body[0]

                            String[] body = getBody(email);
                            Document doc;
                            if(store.equals("Kmart")) {
                                doc = Jsoup.parse(body[0]);
                            }
                            else
                                doc = Jsoup.parse(body[1]);

                            Order order = query.extractOrder(doc.text());
                            order.orderDate = email.getReceivedDate();
                            order.storeName = store;
                            query.getOrders().add(order);

                            msg += "\n" + order.toString() + "\n";
                            updateMessage(msg);

                        }

                        if (email.getSubject().contains(query.getShipSubject())) {

                            String[] body = getBody(email);
                            Document doc;
                            if(store.equals("Kmart")) {
                                doc = Jsoup.parse(body[0]);
                            }
                            else
                                doc = Jsoup.parse(body[1]);

                            Shipment shipment = query.extractShipment(doc.text());

                            //for toysrus , the shipment email order number is in subject. 
                            if (store.equals("Toysrus")) {
                                String subject = email.getSubject();
                                Pattern orderNum = Pattern.compile("Order # ([0-9]+)");
                                Matcher m = orderNum.matcher(subject);
                                if (m.find()) {
                                    shipment.orderNum = m.group(1);
                                }
                            }

                            shipment.shipDate = email.getReceivedDate();
                            query.getShipments().add(shipment);
                            
                            msg += "\n" + shipment.toString() + "\n";
                            updateMessage(msg);
                    
                        }
                    }
                    
                    if (save) {
                        //finally, persist entries in db
                        for (Order orderInfo : query.getOrders()) {
                            if (dataCenter.createOrder(orderInfo) != null) {
                                msg += "\n" + "New order :" + orderInfo.orderNum + " is created successfully. \n";
                                updateMessage(msg);
                            };
                        }

                        for (Shipment shipInfo : query.getShipments()) {
                            if (dataCenter.createUsTrking(shipInfo) != null) {
                                msg += "\n" + "New shipment :" + shipInfo.trackingNum + " is created successfully. \n";
                                updateMessage(msg);
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
        new Thread(task).start();
        

        
        
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
