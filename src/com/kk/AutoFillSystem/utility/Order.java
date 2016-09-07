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
 * info class, storing order info from email
 */
public class Order {
    public String orderNum;
    public ArrayList<Product> products;
    public Date orderDate;
    public String storeName;
    
    public Order(){
        
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OrderNumber : ").append(orderNum).append("\n");
        sb.append("OrderDate : ").append(orderDate.toString()).append("\n");
        products.stream().forEach((prd) -> {
            sb.append(prd.name).append(" ").append(prd.count).append("\n");
        });
        return sb.toString();
    }
}
