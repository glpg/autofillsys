/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.utility.Order;
import com.kk.AutoFillSystem.utility.Product;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author Yi
 */
public class SyncAmazonWindowController implements Initializable {
    
    private DataController dataCenter;
    private MainWindowController mainWindow;
    

    @FXML
    private TextArea txtAreaInput;
    @FXML
    private TextArea txtAreaOutput;
    @FXML
    private Button btnImport;
    
    
    
     public SyncAmazonWindowController() {
        dataCenter = DataController.getInstance();
       
    }


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        btnImport.setOnAction(e->importOrders());
        
    }

    
    private void importOrders(){
        String ordersText = txtAreaInput.getText();
        
        if (ordersText == null || ordersText.isEmpty()) return;
        
        
        Task task = new Task<Void>() {
            @Override public Void call() {
                
                String[] lines = ordersText.split("\n");
               
                
                Order newOrder = new Order();
                for (int i = 0; i < lines.length; i++) {
                    if (lines[i].contains("ORDER PLACED")) {
                        //process old order here
                        if(newOrder.orderNum!=null){
                            String msg = "\n" + newOrder.toString();
                            
                            if (dataCenter.createOrder(newOrder) != null) {
                                msg += "\n" + "New order :" + newOrder.orderNum + " is created successfully. \n";
                                updateMessage(msg);
                                mainWindow.getNewOrders().add(newOrder.orderNum);
                            } else{
                                msg += "\n" + "Order :" + newOrder.orderNum + " already existed. \n";
                            }
                           
                            
                            updateMessage(msg);
                            
                        }
                        
                            
                        
                        
                        //new order starts
                        newOrder = new Order();
                        newOrder.storeName = "Amazon";
                        DateFormat dateformat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
                        try {
                            newOrder.orderDate = dateformat.parse(lines[i+1].trim());
                            
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                        i++;
                        continue;

                    }

                    if (lines[i].contains("ORDER #")) {
                        String orderNum = lines[i].split("#")[1].trim();
                        newOrder.orderNum = orderNum;
                       // System.out.println(orderNum);
                        continue;
                    }

                    

                    if (lines[i].contains("Sold by:")) {
                        Product newpd = new Product();
                        newpd.name = lines[i-1];
                        
                        try{
                            newpd.count = Integer.parseInt(lines[i-2].trim());
                        }
                        catch(NumberFormatException ex) {
                            newpd.count = 1;
                        }
                            
                        newOrder.products.add(newpd);
                        continue;

                    }

                }
                
                //display the last order
                if (newOrder.orderNum != null) {
                    String msg = "\n" + newOrder.toString();

                    if (dataCenter.createOrder(newOrder) != null) {
                        msg += "\n" + "New order :" + newOrder.orderNum + " is created successfully. \n";
                        updateMessage(msg);
                        mainWindow.getNewOrders().add(newOrder.orderNum);
                    } else {
                        msg += "\n" + "Order :" + newOrder.orderNum + " already existed. \n";
                    }

                    updateMessage(msg);

                }


                return null;
            }
        
            @Override
            protected void succeeded() {
                super.succeeded();
                String msg = "Amazon orders sync is finished !";
                updateMessage(msg);
                mainWindow.reloadTable();
            }
        
        };
         
        
        task.messageProperty().addListener((obs, oldMessage, newMessage) -> {
            txtAreaOutput.appendText(newMessage);
        });
        
        
        new Thread(task).start();
   
        
    }

    
    //getter and setter

    public MainWindowController getMainWindow() {
        return mainWindow;
    }

    public void setMainWindow(MainWindowController mainWindow) {
        this.mainWindow = mainWindow;
    }
    
    
}
