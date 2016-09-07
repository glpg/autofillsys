/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.utility;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Yi
 * Info class, storing shipment info from email
 */
public class Shipment {
    public String orderNum;
    public String trackingNum;
    public String carrier;
    public ArrayList<Product> products;
    public Date shipDate;
    public String warehouse; 
    
    public Shipment(){
        
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OrderNum :").append(orderNum).append("\n");
        sb.append("Carrier :").append(carrier).append("\n");
        sb.append("TrackNumber : ").append(trackingNum).append("\n");
        sb.append("ShipDate : ").append(shipDate.toString()).append("\n");
        sb.append("Warehouse : ").append(warehouse).append("\n");
        
        products.stream().forEach((prd) -> {
            sb.append(prd.name).append(" ").append(prd.count).append("\n");
        });
        return sb.toString();
    }
}
