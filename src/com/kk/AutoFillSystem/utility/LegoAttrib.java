/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.utility;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Yi
 */
public class LegoAttrib {
    private SimpleStringProperty attribName = new SimpleStringProperty();
    private SimpleStringProperty value = new SimpleStringProperty();
    
    public LegoAttrib(String attrName, String value) {
        attribName.set(attrName);
        this.value.set(value);
    }

    public String getAttribName() {
        return attribName.get();
    }

    public void setAttribName(String attribName) {
        this.attribName.set(attribName);
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }
    
    
    
}
