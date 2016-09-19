/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.EmailInfo;

import com.kk.AutoFillSystem.utility.Order;
import com.kk.AutoFillSystem.utility.Product;
import com.kk.AutoFillSystem.utility.Shipment;
import static com.kk.AutoFillSystem.utility.Tools.getWarehouse;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Yi
 */
public class GetKmart extends GetStore {
    
    public GetKmart(String email, String pwd) { 
        super();
        this.email = email;
        this.pwd = pwd;
        emailSender = "kmart@value.kmart.com";
        orderSubject = "thank you for your order";
        shipSubject = "have shipped!";
        storeName = "Kmart";
    }
    
    

    @Override
    public Order extractOrder(String text) {
        Order order = new Order();
        //get order number
        
        Pattern orderNum = Pattern.compile("Order #([0-9]+)");
        Matcher m = orderNum.matcher(text);
        if(m.find()) {
            order.orderNum = m.group(1);
        }
      
        
        //get items
        //put item in products
        //also, accumulate same item
        String[] items = text.split("ITEM DETAILS");
        ArrayList<Product> products = new ArrayList();
        for(int i = 1; i < items.length; i++) {
            String orderTextLine = items[i];
            
            Product tmp = new Product();
            Pattern prodNumP = Pattern.compile("Mfr# ([0-9]+)");
            Matcher m1 = prodNumP.matcher(orderTextLine);
            if(m1.find()) {
                tmp.name = m1.group(1);
                
            }
            
            
            Pattern cntP = Pattern.compile("PRICE QTY ITEM SUBTOTAL .* ([1-9]{1}) [0-9.]+");
            Matcher m2 = cntP.matcher(orderTextLine);
            if(m2.find()) {
                tmp.count = Integer.parseInt(m2.group(1));
            }
            
            products.add(tmp);
        }

        order.products = products;
        return order;
    }
    
    

    @Override
    public Shipment extractShipment(String text) {
        Shipment shipment = new Shipment();
        String[] texts = text.split("Order Summary");
        //carrier
        Pattern carrierP = Pattern.compile("Shipping Vendor ([a-zA-Z]+)");
        Matcher m = carrierP.matcher(text);
        if(m.find()) {
            shipment.carrier = m.group(1);
            
        }
        
        //tracking
        Pattern track = Pattern.compile("Tracking number: ([a-zA-Z0-9]+)");
        Matcher m1 = track.matcher(text);
        if(m1.find()) {
            
            shipment.trackingNum = m1.group(1);
            //System.out.println(m.group(2));
        }
        
        //order number
        //get order number
        Pattern orderNum = Pattern.compile("Order #([0-9]+)");
        Matcher m2 = orderNum.matcher(text);
        if(m2.find()) {
            shipment.orderNum = m2.group(1);
        }
        
        //ship to 
        shipment.warehouse = getWarehouse(text);
        
        
        //get items, use texts[0] in case some unshipped
        //put item in products
        String[] items = texts[0].split("ITEM DETAILS");
        ArrayList<Product> products = new ArrayList();
        for(int i = 1; i < items.length; i++) {
            String orderTextLine = items[i];
            
            Product tmp = new Product();
            Pattern prodNumP = Pattern.compile("Mfr# ([0-9]+)");
            Matcher m3 = prodNumP.matcher(orderTextLine);
            if(m3.find()) {
                tmp.name = m3.group(1);
                
            }
            
            
            Pattern cntP = Pattern.compile("PRICE QTY ITEM SUBTOTAL .* ([0-9]+) [0-9.]+");
            Matcher m4 = cntP.matcher(orderTextLine);
            if(m4.find()) {
                tmp.count = Integer.parseInt(m4.group(1));
            }
            
            products.add(tmp);
        }
        
        shipment.products = products;
        
        return shipment;
    }
}
