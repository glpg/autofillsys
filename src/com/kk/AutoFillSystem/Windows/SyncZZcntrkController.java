package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Addresses;
import com.kk.AutoFillSystem.Database.Entities.Carriers;
import com.kk.AutoFillSystem.Database.Entities.Cntrkings;
import com.kk.AutoFillSystem.Database.Entities.Ustocntrkings;
import static com.kk.AutoFillSystem.utility.Tools.showAlert;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public class SyncZZcntrkController implements Initializable {
    private DataController dataCenter;
    private MainWindowController mainWindow;
    

    @FXML
    private TextArea txtAreaInput;
    @FXML
    private TextArea txtAreaOutput;
    @FXML
    private Button btnImport;
    
    
    
     public SyncZZcntrkController() {
        dataCenter = DataController.getInstance();
       
    }


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        btnImport.setOnAction(e->importZZcntrk());
        
    }

    
    private void importZZcntrk(){
        txtAreaOutput.clear();
        String info = txtAreaInput.getText();
        
        if (info == null || info.isEmpty()){
            showAlert("Warning", "No info :" , "You did not input any china trackings info. ", Alert.AlertType.WARNING);
            return;
        }
        
        List<Cntrk> entries = getInfo(info);
        
      
        
        Task task = new Task<Void>() {
            String msg ="";
            @Override public Void call() {
                int i = 0;
                
                
                for(Cntrk cntrk : entries) {
                    i++;
                    msg += "\n" +i +". "+ cntrk.toString();
                    updateMessage(msg);
                    
                    List<Ustocntrkings> result =  dataCenter.getIntlTrking(cntrk.intltrknum);
                    if (result == null || result.size()== 0) {
                       msg += "\n" + "Status :  error ! Intl tracking - " + cntrk.intltrknum + " could not be found ! \n";
                        updateMessage(msg);
                        
                        continue;
                    }
                    
                    Ustocntrkings intl = result.get(0);
                    //if cntrkings already input
                    if (intl.getCntrkingsCollection().size() > 0) {
                        msg += "\n" + "Status :  Intl tracking - " + cntrk.intltrknum + " already has cn trkings ! \n";
                        
                        updateMessage(msg);
                        continue;
                        
                    }
                    
                    
                    
                    //if cntrkings empty, create cntrkings
                    Cntrkings cnTrk = new Cntrkings();
                    cnTrk.setUstocntrkingId(intl);
                    cnTrk.setTrkingNum(cntrk.cntrknum);
                    cnTrk.setDelivered(false);
                    
                    //get all carriers and find the exact carrier
                    List<Carriers> carriers =  mainWindow.getCarriers();
                    for(Carriers carrier : carriers) {
                        if (carrier.getName().contains(cntrk.carrier)){
                            cnTrk.setCarrierId(carrier);
                            break;
                        }
                    }
        
                    //get all address and find song
                    List<Addresses> addresses = mainWindow.getAddresses();
                    
                    for(Addresses addr: addresses) {
                        if (addr.getName().contains("Song-159")) {
                            cnTrk.setAddressId(addr);
                            break;
                        }
                    }
                    
                    if(dataCenter.createCnTrking(cnTrk)) {
                        msg  += "\n" + "New Cn Trking :" +cnTrk.getTrkingNum() + " is created successfully. \n";
                        
                        updateMessage(msg);
                        //now add to mainWindow, and change color
                        mainWindow.getNewCnShipments().add(cnTrk.getTrkingNum());
                        
                    }
                    
                    else{
                        msg += "\n" + "New Cn Trking :" +cnTrk.getTrkingNum() + " could not be created, please check database. \n";
                        updateMessage(msg);
                    }
                    
                   
                    
                    
                }
               
              
                return null;
            }
        
            @Override
            protected void succeeded() {
                super.succeeded();
                msg += "ZZ cntrkings updated !";
                updateMessage(msg);
                mainWindow.reloadTable();
                
            }
        
        };
         
        
        task.messageProperty().addListener((obs, oldMessage, newMessage) -> {
            txtAreaOutput.setText(newMessage);
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
    
    //extract cntrks info here
    private List<Cntrk> getInfo(String info) {
        String[] trkings = info.split("Us 32 20");
        ArrayList<Cntrk> results = new ArrayList();
        for(String tmp: trkings) {
            if (tmp.contains("(")) {
               
                String[] parts = tmp.split("\t");
                String intltrknum = parts[1].trim();
                String cntrkingsStr = parts[3].trim();
                Pattern regex = Pattern.compile("\\((.*?)\\)");
                Matcher matcher = regex.matcher(cntrkingsStr);
                if (matcher.find()) {
                    String[]  cntrkings = matcher.group(1).split(",");
                    for(String temp : cntrkings) {
                        Cntrk newtrk = new Cntrk();
                        newtrk.intltrknum = intltrknum;
                        String cntrking = temp.trim();
                        
                        if (cntrking.contains("中通")) {
                            newtrk.carrier = "ZTO";
                            newtrk.cntrknum = cntrking.replace("中通", "").trim();
                        }
                        if (cntrking.contains("申通")) {
                            newtrk.carrier = "STO";
                            newtrk.cntrknum = cntrking.replace("申通", "").trim();
                        }
                        if (cntrking.contains("圆通")) {
                            newtrk.carrier = "YTO";
                            newtrk.cntrknum = cntrking.replace("圆通", "").trim();
                        }

                        if (cntrking.contains("德邦")) {
                            newtrk.carrier = "DB";
                            newtrk.cntrknum = cntrking.replace("德邦", "").trim();
                        }
                        if (cntrking.contains("顺丰")) {
                            newtrk.carrier = "SF";
                            newtrk.cntrknum = cntrking.replace("顺丰", "").trim();
                        }
                        if (cntrking.contains("EMS")) {
                            newtrk.carrier = "EMS";
                            newtrk.cntrknum = cntrking.replace("EMS", "").trim();
                        }
                        results.add(newtrk);
                        
                    }
                }
                
                

            }
        }
        return results;
    }
    
    private class Cntrk {
        public String intltrknum;
        public String cntrknum;
        public String carrier;
        
        public String toString(){
            String result = "Intl Trknum : " + intltrknum + "\n" + "Cn Trknum : " + carrier + " " + cntrknum;
            return result; 
        }
       
    }
   
    
}

