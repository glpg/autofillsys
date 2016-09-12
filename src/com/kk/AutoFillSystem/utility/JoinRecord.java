/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.utility;

import com.kk.AutoFillSystem.Database.Entities.Cntrkings;
import com.kk.AutoFillSystem.Database.Entities.Orders;
import com.kk.AutoFillSystem.Database.Entities.Ustocntrkings;
import com.kk.AutoFillSystem.Database.Entities.Ustrkings;
import java.util.Date;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Yi
 */
public class JoinRecord {
    //order part
    private SimpleStringProperty orderNum = new SimpleStringProperty();
    private SimpleStringProperty store = new SimpleStringProperty();
    private SimpleObjectProperty<Date> orderDate = new SimpleObjectProperty();
    //us ship part
    private SimpleStringProperty usCarrier = new SimpleStringProperty();
    private SimpleStringProperty usTrkNum = new SimpleStringProperty();
    private SimpleStringProperty warehouse = new SimpleStringProperty();
    private SimpleStringProperty shipList = new SimpleStringProperty();
    //intl ship part
    private SimpleStringProperty intlTrkNum = new SimpleStringProperty();
    private SimpleIntegerProperty weight = new SimpleIntegerProperty();
    private SimpleDoubleProperty fee = new SimpleDoubleProperty();
    
    //cn ship part
    private SimpleStringProperty cnCarrier = new SimpleStringProperty();
    private SimpleStringProperty cnTrkNum = new SimpleStringProperty();
    private SimpleStringProperty address = new SimpleStringProperty();
   
    private Orders order;
    private Ustrkings usTrk;
    
    private Ustocntrkings intlTrk;
    private Cntrkings cnTrk;
    
    public JoinRecord() {
        
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public Ustrkings getUsTrk() {
        return usTrk;
    }

    public void setUsTrk(Ustrkings usTrk) {
        this.usTrk = usTrk;
    }

    public Ustocntrkings getIntlTrk() {
        return intlTrk;
    }

    public void setIntlTrk(Ustocntrkings intlTrk) {
        this.intlTrk = intlTrk;
    }

    public Cntrkings getCnTrk() {
        return cnTrk;
    }

    public void setCnTrk(Cntrkings cnTrk) {
        this.cnTrk = cnTrk;
    }

    

    public String getOrderNum() {
        return orderNum.get();
    }

    public void setOrderNum(String orderNum) {
       this.orderNum.set(orderNum);
    }

    public String getStore() {
        return store.get();
    }

    public void setStore(String store) {
        this.store.set(store);
    }

    public Date getOrderDate() {
        return orderDate.get();
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate.set(orderDate);
    }

    public String getUsCarrier() {
        return usCarrier.get();
    }

    public void setUsCarrier(String usCarrier) {
        this.usCarrier.set(usCarrier);
    }

    public String getUsTrkNum() {
        return usTrkNum.get();
    }

    public void setUsTrkNum(String usTrkNum) {
        this.usTrkNum.set(usTrkNum);
    }

    public String getWarehouse() {
        return warehouse.get();
    }

    public void setWarehouse(String warehouse) {
        this.warehouse.set(warehouse);
    }

    public String getShipList() {
        return shipList.get();
    }

    public void setShipList(String shipList) {
        this.shipList.set(shipList);
    }

    public String getIntlTrkNum() {
        return intlTrkNum.get();
    }
    
    public void setIntlTrkNum(String intlTrkNum) {
        this.intlTrkNum.set(intlTrkNum);
    }
  
    public int getWeight() {
        return weight.get();
    }
    
    public void setWeight(int weight){
        this.weight.set(weight);
    }
    
    public double getFee() {
        return this.fee.get();
    }
    
    public void setFee(double fee) {
        this.fee.set(fee);
    }
    
    
    public String getCnCarrier() {
        return cnCarrier.get();
    }

    public void setCnCarrier(String cnCarrier) {
        this.cnCarrier.set(cnCarrier);
    }

    public String getCnTrkNum() {
        return cnTrkNum.get();
    }

    public void setCnTrkNum(String cnTrkNum) {
        this.cnTrkNum.set(cnTrkNum);
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String addr) {
        this.address.set(addr);
    }
    
    
    
}
