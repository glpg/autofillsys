/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.utility.JoinRecord;
import java.net.URL;
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
public class SyncDeliveryInfoController implements Initializable {
    
    private DataController dataCenter;
    private MainWindowController mainWindow;
    private JoinRecord record;
    
    
    @FXML
    private TextArea textAreaSyncData;
    @FXML
    private TextArea textAreaUpdateInfo;
    @FXML
    private Button btnConfirm;
    
    
    public SyncDeliveryInfoController(){
        
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
       
        btnConfirm.setOnAction(e->{batchConfirm();});

    }    

    
    
    private void batchConfirm(){
        
        String input = textAreaSyncData.getText();
        System.out.println(input);
        String[] deliveries = input.split("\n");
        
        Task task = new Task<Void>() {
            @Override public Void call() {
                String msg = "";
                
                //connect gmail
                msg = "Start to confirm deliveries in batch mode ... \n";
                updateMessage(msg);
                for (int i = 0; i < deliveries.length; i++) {
                    String trkNum = deliveries[i].trim();
                    if (trkNum != null && !trkNum.isEmpty()) {
                        JoinRecord record = mainWindow.findRecordWithCnTrk(trkNum);
                        if (record == null) {
                            msg += trkNum + ": NOT FOUND !  NO UPDATE! \n";
                            updateMessage(msg);

                        } else {
                            msg += trkNum + ":  found ,";
                            updateMessage(msg);
                            if (mainWindow.confirmDelivery(record)) {
                                msg += " and updated .\n";
                                updateMessage(msg);
                            } else {
                                msg += " UPDATED ERROR ! \n";
                                updateMessage(msg);
                            }
                        }
                    }
            
                }
                
                return null;
            }
            
            @Override
            protected void succeeded() {
                super.succeeded();
                mainWindow.getOrderTable().getColumns().get(0).setVisible(false);
                mainWindow.getOrderTable().getColumns().get(0).setVisible(true);
                
            }
        };
        textAreaUpdateInfo.textProperty().bind(task.messageProperty());
        new Thread(task).start();
        
       
        
        
//        String input = textAreaSyncData.getText();
//        System.out.println(input);
//        String[] deliveries = input.split("\n");
//        
//        for (int i = 0; i < deliveries.length; i++) {
//            String trkNum = deliveries[i].trim();
//            if (trkNum != null && !trkNum.isEmpty()) {
//                JoinRecord record = mainWindow.findRecordWithCnTrk(trkNum);
//                if (record == null) {
//                    textAreaUpdateInfo.appendText(trkNum + ": NOT FOUND !  NO UPDATE! \n");
//
//                }
//                else {
//                    textAreaUpdateInfo.appendText(trkNum + ":  found ,");
//                    if (mainWindow.confirmDelivery(record)) {
//                        textAreaUpdateInfo.appendText(" and updated .\n");
//                    } else {
//                        textAreaUpdateInfo.appendText(" UPDATED ERROR ! \n");
//                    }
//                }
//            }
//            
//        }
//        
//        mainWindow.getOrderTable().getColumns().get(0).setVisible(false);
//        mainWindow.getOrderTable().getColumns().get(0).setVisible(true);

    }
    
    
    
    public MainWindowController getMainWindow() {
        return mainWindow;
    }
    
    
    

    public void setMainWindow(MainWindowController mainWindow) {
        this.mainWindow = mainWindow;
    }
    
    
    
    
}
