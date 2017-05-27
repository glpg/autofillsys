/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Ustocntrkings;
import com.kk.AutoFillSystem.utility.JoinRecord;
import static com.kk.AutoFillSystem.utility.Tools.showAlert;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

/**
 *
 * @author Yi
 */
public class SyncZHcntrkController implements Initializable {
    private DataController dataCenter;
    private MainWindowController mainWindow;
    private ArrayList<JoinRecord> records;
    private String msg;
    

    @FXML
    private TextArea txtAreaInput;
    @FXML
    private TextArea txtAreaOutput;
    @FXML
    private Button btnImport;
    
    
     public SyncZHcntrkController() {
        dataCenter = DataController.getInstance();
        records = new ArrayList();
        msg = "";
        
    }


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        btnImport.setOnAction(e->highLightZHcntrk());
        
    }

    
    private void highLightZHcntrk(){
        
        String info = txtAreaInput.getText();
        
        if (info == null || info.isEmpty()){
            showAlert("Warning", "No info :" , "You did not input any china trackings info. ", Alert.AlertType.WARNING);
            return;
        }
        ArrayList<String> intransit = new ArrayList();
        
        //get all in trasit intl trkings
        String[] trkings = info.split("\n");
        for(String tmp: trkings) {
            if (tmp.contains("国内转运中") || tmp.contains("清关完毕")) {
                String[] infoparts = tmp.split("\t");
                String intltrk = infoparts[0].trim();
                intransit.add(intltrk);
            }
        }
        
        
        
        Task task = new Task<Void>() {
            
            @Override public Void call() {
                
                
                int i = 0;
                for(String intltrk : intransit) {
                    i++;
                    msg += "\n" +i +". "+ "Intl Trking : " + intltrk; 
                    updateMessage(msg);
                    
                    List<Ustocntrkings> result =  dataCenter.getIntlTrking(intltrk);
                    if (result == null || result.size()== 0) {
                       msg += "\n" + "Status :  error ! Intl tracking - " + intltrk + " could not be found ! \n";
                       updateMessage(msg);
                        
                       continue;
                    }
                    
                    Ustocntrkings intl = result.get(0);
                    //if cntrkings already input
                    if (intl.getCntrkingsCollection().size() > 0) {
                        msg += "\n" + "Status :  Intl tracking - " + intltrk + " already has cn trkings !\n ";
                        updateMessage(msg);
                        continue;
                        
                    }
                    else {
                        msg += "\n" + "Status :  Intl tracking - " + intltrk + " HAS NEW CHINA TRKING ! ";
                        updateMessage(msg);
                        JoinRecord record = mainWindow.findRecordWithIntlTrk(intltrk);
                        if (record == null) {
                            msg += "\n" + "Warning :  Intl tracking - " + intltrk + " could not be found in the table ! pls check manually \n";
                            updateMessage(msg);
                        }
                        else {
                            msg += "\n" + "Intl tracking - " + intltrk + " is selected in the main table. \n";
                            updateMessage(msg);
                            records.add(record);
                            ObservableList<JoinRecord> rows = FXCollections.observableArrayList(records);
                            mainWindow.getOrderTable().setItems(rows);
                        }
                        
                        
                    }
       
                }
                
                
                
                return null;
            }
            
            @Override
            protected void succeeded() {
                super.succeeded();
                msg += "\n ------The page is processed ------- !";
                msg += "\n Total : " + records.size() ;
                updateMessage(msg);
                
            }
        };
        txtAreaOutput.textProperty().bind(task.messageProperty());
        new Thread(task).start();
        
        
        
        
        
    }     

    public MainWindowController getMainWindow() {
        return mainWindow;
    }

    public void setMainWindow(MainWindowController mainWindow) {
        this.mainWindow = mainWindow;
    }
    
    
    
}
