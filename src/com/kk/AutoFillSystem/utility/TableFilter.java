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
        totalItems = table.getItems();
        initFilters();
       
    }
    
    //default "" means any record will pass the filtering
    private void initFilters() {
        filters.put("store", "");
        filters.put("warehouse", "");
        filters.put("product", "");
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
    
    public void applyFilters() {
        
        filteredItems.clear();
        //always apply all the currently stored filters, so you can change things freely 
        
        for(JoinRecord record : totalItems) {
            if (record.getWarehouse().contains(filters.get("warehouse"))
                    && record.getStore().contains(filters.get("store"))
                    && record.getShipList().contains(filters.get("product"))) {
                    
                    filteredItems.add(record);
            }
            
        }
      
        table.setItems(filteredItems);
        
    }
    
    
    
    public void clearFilters() {
        filteredItems.clear();
        initFilters();
    }
    
}
