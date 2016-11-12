/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.utility;

import com.kk.AutoFillSystem.Database.Entities.Orderlines;
import com.kk.AutoFillSystem.Database.Entities.Trklines;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

/**
 *
 * @author Yi
 */
public class Tools {
    
    public static int count(final String string, final String substring){
        int count = 0;
        int idx = 0;

        while ((idx = string.indexOf(substring, idx)) != -1)
        {
            idx++;
            count++;
        }

        return count;
    }
    
    
    //read file, return unique set , for lego set numbers
    public static Set<String> readFile(String fileName) {
        BufferedReader br = null;
        
        Set<String> uniqueStr = new HashSet();
        
        //ArrayList<Products> prods = new ArrayList();

        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(fileName));

            while ((sCurrentLine = br.readLine()) != null) {
                uniqueStr.add(sCurrentLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        return uniqueStr;
        
    }
    
    
     //read file, return unique set , for lego set numbers
    public static List<String> readFileLines(File file) {
        BufferedReader br = null;
        
        ArrayList<String> lines = new ArrayList();
        
        //ArrayList<Products> prods = new ArrayList();

        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(file));

            while ((sCurrentLine = br.readLine()) != null) {
                lines.add(sCurrentLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        return lines;
        
    }
    
    
    
    

    //read file, name and set to a map
    public static Map<String, String> readMapFile(String fileName) {
        BufferedReader br = null;
        
        Map<String, String> map = new HashMap();
        
        //ArrayList<Products> prods = new ArrayList();

        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(fileName));

            while ((sCurrentLine = br.readLine()) != null) {
                
                String[] parts = sCurrentLine.split("\\|\\|");
                
                map.put(parts[1].trim(), parts[0]);
                
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        return map;
        
    }
    
    public static String getWarehouse(String text) {
        //ship to 
        String warehouse = "unknown";
        if (text.toLowerCase().contains("ivy kangxue")) {
            warehouse = "ZZ";
            return warehouse;
        }
       
        if (text.toLowerCase().contains("rsjep hdb")) {
            warehouse = "HDB";
            return warehouse;
        }
        
        if (text.toLowerCase().contains("ltfhn tt")) {
            warehouse = "TT";
            return warehouse;
        }
        
        if (text.toLowerCase().contains("yi xue")) {
            warehouse = "Yi Xue";
            
        }
        
        return warehouse;
        
    }
    
    
    //gui related functions
    public static void showAlert(String title, String header, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
            }
        });
    }
    
    
    
    public static void closeWindow(ActionEvent e) {
        Node source;
        source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
        
    }
    
    public static void expandInfo(JoinRecord record) {
        if (record.getOrder() != null) {
            record.setOrderDate(record.getOrder().getOrderDate());
            record.setOrderNum(record.getOrder().getOrderNum());
            record.setStore(record.getOrder().getStoreId().getName());
            StringBuilder sb = new StringBuilder();
            for (Orderlines prd : record.getOrder().getOrderlinesCollection()) {

                sb.append(prd.getProductId().getProdNum()).append(" : ").append(prd.getQuantity()).append(" || ");

            }
            
            String items = sb.toString();
            if (items.length() > 0) {
                record.setOrderList(items.substring(0, items.length() - 3));
            }
            
        }
        
        if (record.getUsTrk() != null) {
            record.setUsCarrier(record.getUsTrk().getCarrierId().getName());
            record.setUsTrkNum(record.getUsTrk().getTrkingNum());
            record.setWarehouse(record.getUsTrk().getAddressId().getName());

            StringBuilder sb = new StringBuilder();
            for (Trklines prd : record.getUsTrk().getTrklinesCollection()) {

                sb.append(prd.getProductId().getProdNum()).append(" : ").append(prd.getQuantity()).append(" || ");

            }

            String items = sb.toString();
            if (items.length() > 0) {
                record.setShipList(items.substring(0, items.length() - 3));
            }
        }
        
        if (record.getIntlTrk() != null) {
            record.setIntlTrkNum(record.getIntlTrk().getTrkingNum());
            record.setWeight(record.getIntlTrk().getWeight());
            if(record.getIntlTrk().getShippingfee() != null)
                record.setFee(record.getIntlTrk().getShippingfee().doubleValue());
        }
        
        if (record.getCnTrk() != null) {
            record.setCnCarrier(record.getCnTrk().getCarrierId().getName());
            record.setCnTrkNum(record.getCnTrk().getTrkingNum());
            record.setAddress(record.getCnTrk().getAddressId().getName());
        }
        
        if (record.getCnTrk() != null && record.getCnTrk().getDelivered())
            record.setDelivered(true);
        
        
    }
    
    
    public static ObservableList<LegoAttrib> createLegoInfo(ArrayList<String> info) {
        //total 11 attributes used
        ArrayList<LegoAttrib> data = new ArrayList();
        data.add(new LegoAttrib("Set Number", info.get(0)));
        data.add(new LegoAttrib("Set Name", info.get(1)));
        data.add(new LegoAttrib("Year", info.get(2)));
        data.add(new LegoAttrib("Pieces", info.get(3)));
        data.add(new LegoAttrib("Minfigures", info.get(4)));
        data.add(new LegoAttrib("Ratings", info.get(5)));
        data.add(new LegoAttrib("US Release Date", info.get(6)));
        data.add(new LegoAttrib("US Retire Date", info.get(7)));
        data.add(new LegoAttrib("US Price", info.get(8)));
        data.add(new LegoAttrib("UK Price", info.get(9)));
        data.add(new LegoAttrib("EU Price", info.get(10)));
        data.add(new LegoAttrib("Weight (g)", info.get(11)));
        data.add(new LegoAttrib("Dimensions (cm)", info.get(12)));
        
        return FXCollections.observableArrayList(data);
        
    }
    
    public static void copyToClipboard(String value){
        
        ClipboardContent content = new ClipboardContent();
        content.putString(value);
        Clipboard.getSystemClipboard().setContent(content);
    }
    
    

}
