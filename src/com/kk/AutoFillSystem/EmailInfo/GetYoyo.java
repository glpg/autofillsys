/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.EmailInfo;

import com.kk.AutoFillSystem.utility.LoggingAspect;
import com.kk.AutoFillSystem.utility.Order;
import com.kk.AutoFillSystem.utility.Product;
import com.kk.AutoFillSystem.utility.Shipment;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Message;
import javax.mail.MessagingException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Yi
 */
public class GetYoyo extends GetStore {
    
    public GetYoyo(String email, String pwd) { 
        super();
        this.email = email;
        this.pwd = pwd;
        emailSenders = new ArrayList();
        emailSenders.add("customercare@yoyo.com");
        orderSubject = "Your order at YoYo.com";
        shipSubject = "Your order from YoYo.com has shipped";
        storeName = "Yoyo";
    }
    
    

    @Override
    protected Order extractOrder(String text) {
        Order order = new Order();
        //get order number
        
        Pattern orderNum = Pattern.compile("Your Order ID is ([0-9]+)");
        Matcher m = orderNum.matcher(text);
        if(m.find()) {
            order.orderNum = m.group(1);
            
        }
      
        
        //get items
        //put item in products
        //also, accumulate same item
        
        ArrayList<Product> products = new ArrayList();
        
        String[] itemsText = text.split("Qty Description Item Price");
        
        for(int i = 1; i < itemsText.length; i++) {
            String current = itemsText[i];
            Pattern prods = Pattern.compile("([0-9]{1,2}) ([^\\$]*) \\$[0-9.]+ \\$[0-9.]+ \\$[0-9.]+");
            Matcher m1 = prods.matcher(current);
            while(m1.find()) {
                Product temp = new Product();
                temp.name = m1.group(2);
                temp.count = Integer.parseInt(m1.group(1));
                products.add(temp);
            
            }
       
        }
        
        
        
        
//        int start = text.indexOf("Qty Description");
//        Pattern prods = Pattern.compile("([0-9]{1,2}) ([^\\$]*) \\$[0-9.]+ \\$[0-9.]+ \\$[0-9.]+");
//        Matcher m1 = prods.matcher(text.substring(start));
//        while(m1.find()) {
//            Product temp = new Product();
//            temp.name = m1.group(2);
//            temp.count = Integer.parseInt(m1.group(1));
//            products.add(temp);
//            
//        }
        order.products = products;
        return order;
    }

    @Override
    protected Shipment extractShipment(String text) {
        Shipment shipment = new Shipment();
        
        //tracking
        Pattern track = Pattern.compile("Carrier: ([a-zA-Z ]+) Tracking number: ([a-zA-Z0-9]+)");
        Matcher m = track.matcher(text);
        if(m.find()) {
            shipment.carrier = m.group(1);
            shipment.trackingNum = m.group(2);
            
        }
        
        //order number
        Pattern order = Pattern.compile("Order #: ([0-9-]+)");
        Matcher m1 = order.matcher(text);
        
        if(m1.find()) {
            shipment.orderNum = m1.group(1);
            
        }
        
        
        //get items
        //put item in products
        
        ArrayList<Product> products = new ArrayList();
        ArrayList<String> prodText = new ArrayList();
        int start = text.indexOf("Qty Description");
        int stop = text.indexOf("To see the details of your order");
        
        String searchText = text.substring(start, stop);
        Pattern prd = Pattern.compile("( [0-9]{1,2} )");
        Matcher m2 = prd.matcher(searchText);
        ArrayList<Integer> indexes = new ArrayList();
        while(m2.find()) {
            indexes.add(m2.start());
        }
        
        //get each prod text section
        if (indexes.size() == 1)
            prodText.add(searchText);
        else {
            for (int i = 0; i < indexes.size() - 1; i++) {
                prodText.add(searchText.substring(indexes.get(i), indexes.get(i+1)));
            }
            
            prodText.add(searchText.substring(indexes.get(indexes.size()-1)));
         
        }
        
        //now get items from prodText
        for(String prodT : prodText) {
            
            Pattern prdP = Pattern.compile("([0-9]{1,2})(.*)");
            Matcher m3 = prdP.matcher(prodT);
            if (m3.find()) {
                Product temp = new Product();
                temp.name = m3.group(2).trim();
                temp.count = Integer.parseInt(m3.group(1));
                products.add(temp);
                
            }      
        }

        shipment.products = products;
        
        return shipment;
        
        
    }

    @Override
    public ArrayList<Shipment> extractShipments(Message email) {
        ArrayList<Shipment> found = new ArrayList();
        String[] body = getBody(email);
        Document doc = Jsoup.parse(body[1]);
        String content = doc.text();
        
        try {
            //yoyo order only shipped to HDB
            Shipment shipment = extractShipment(content);
            shipment.shipDate = email.getReceivedDate();
            //ship to
            shipment.warehouse = "HDB";
            found.add(shipment);
            
        } catch (MessagingException ex) {
            LoggingAspect.addException(ex);
        }
        
        return found;
    }

    @Override
    public Order extractOrder(Message email) {
        String[] body = getBody(email);
        Document doc = Jsoup.parse(body[1]);
        
        Order order = extractOrder(doc.text());
        order.storeName = this.storeName;
        
        try {
            order.orderDate = email.getReceivedDate();
        } catch (MessagingException ex) {
            LoggingAspect.addException(ex);
        }
        
        return order;
        
        
        
    }
        
    
}
