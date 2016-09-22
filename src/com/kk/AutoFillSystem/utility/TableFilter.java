/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.utility;

import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

/**
 *
 * @author Yi
 */
public class TableFilter {
    
    private TableView table; 
    private ObservableList<JoinRecord> filteredItems = FXCollections.observableArrayList();
    private ObservableList<JoinRecord> totalItems ;
    private Map<String, String> filters = new HashMap();
    
    public TableFilter(TableView table) {
        this.table = table;
        //full record
        
        initFilters();
       
    }
    
    //default "" means any record will pass the filtering
    private void initFilters() {
        filters.put("store", "");
        filters.put("warehouse", "");
        filters.put("product", "");
        filters.put("unshipped","");
        filters.put("destination","");
    }
    
    public void setStoreFilter(String storeName) {
        filters.put("store", storeName);
    }
    
    public void setProdutFilter(String prodNum) {
        filters.put("product", prodNum);
    }
    
    public void setWarehouseFilter(String warehouse) {
        filters.put("warehouse", warehouse);
    }
    
    public void setUnshippedFilter(String unshipped) {
        filters.put("unshipped", unshipped);
    }
    
    public void setDestinationFilter(String unshipped) {
        filters.put("destination", unshipped);
    }
    
    public void applyFilters() {
        
        filteredItems.clear();
        //always apply all the currently stored filters, so you can change things freely 
        totalItems = table.getItems();
        
        for(JoinRecord record : totalItems) {
            String warehouse = (record.getWarehouse()==null)?"":record.getWarehouse();
            String shipList = (record.getShipList() == null)?"":record.getShipList();
            String destination = (record.getCnTrk() == null)? "":record.getAddress();
            if (warehouse.contains(filters.get("warehouse"))
                    && record.getStore().contains(filters.get("store"))
                    && shipList.contains(filters.get("product"))
                    && destination.contains(filters.get("destination"))) {
                  
                switch(filters.get("unshipped")) {
                    
                    case "US trking" : {
                        if (record.getUsTrk() == null) filteredItems.add(record);
                        break;
                    }
                    case "Intl trking" : {
                        if (record.getUsTrk() != null && record.getIntlTrk() == null) 
                            filteredItems.add(record);
                        break;
                       
                    }
                    
                    case "CN trking" :{
                        if (record.getIntlTrk() != null && record.getCnTrk() == null) 
                            filteredItems.add(record);
                        break;
                        
                    }
                    case "None" : {
                        if (record.getCnTrk() != null) 
                            filteredItems.add(record);
                        break;
                    }
                    
                    default : filteredItems.add(record);
                    
                }
                

            }
            
        }
      
        table.setItems(filteredItems);
        
    }
    
    public void clearFilters() {
        filteredItems.clear();
        initFilters();
    }
    
}
