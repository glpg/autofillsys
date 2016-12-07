/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.utility;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

/**
 *
 * @author Yi
 */
public class ComboBoxListener implements ChangeListener<String> {
    
    private ComboBox comboBox;
    
    public ComboBoxListener(ComboBox box){
        this.comboBox = box;
        
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        //scroll to ith lines
        ListView lv = ((ComboBoxListViewSkin) comboBox.getSkin()).getListView();
        lv.scrollTo(startIndex(newValue));
        comboBox.show();
    }
    
    private int startIndex(String prd) {
        int i = 0;
        ObservableList<String> productList = comboBox.getItems();
        
        for(; i < productList.size(); i ++) {
            if (productList.get(i).startsWith(prd)) {
                return i;
            }
        }
        
        
        return i;
    }
    
}
