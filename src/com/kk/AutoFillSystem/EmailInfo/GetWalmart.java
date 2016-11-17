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
import static com.kk.AutoFillSystem.utility.Tools.getWarehouse;
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
public class GetWalmart extends GetStore {
    
    
    public GetWalmart(String email, String pwd) { 
        super();
        this.email = email;
        this.pwd = pwd;
        emailSenders = new ArrayList();
        emailSenders.add("help@walmart.com");
        orderSubject = "Order received";
        shipSubject = "shipped";
        storeName = "Walmart";
    }
    
    
    
    @Override
    protected Order extractOrder(String text) {
        
        Order order = new Order();
        //get order number
        Pattern orderNum = Pattern.compile("Order number: ([0-9-]+)");
        Matcher m = orderNum.matcher(text);
        if(m.find()) {
            order.orderNum = m.group(1);
        }
        
        
        
        
        //get items
        //put item in products
        //also, accumulate same item
        ArrayList<String> items = getItems(text, "Items may arrive");
        ArrayList<Product> products = new ArrayList();
        for(String item : items) {
            
            Pattern orderLine = Pattern.compile("([^$]*) ([1-9][0-9]?) \\$[0-9.]+\\s\\$[0-9.]+\\s");
            Matcher m2 = orderLine.matcher(item);
            boolean found = false;
            while(m2.find()) {
                String name = m2.group(1);
                int count = Integer.parseInt(m2.group(2));
                
                for (Product prod : products) {
                    if (name.equals(prod.name)) {
                        prod.count += count;
                        found = true;
                        break;
                    }
                }
                
                if(!found) {
                    products.add(new Product(name, count));
                }
            }
        }
        
        order.products = products;
        return order;
    
    }
    
    
    //get the text string for items
    public static ArrayList<String> getItems(String text, String endText) {
        ArrayList<String> items = new ArrayList();
        String str1 = "Qty Price Total ";
        String str2 = endText;
        int index = 0;
        while(index < text.length()) {
            String searchText = text.substring(index);
            int startIndex = searchText.indexOf(str1);
            int stopIndex = searchText.indexOf(str2);
            
            if (startIndex != -1 && stopIndex != -1){
                items.add(searchText.substring(startIndex + str1.length(), stopIndex));
                index += stopIndex + str2.length();
            }
            
            else break;
                
            
        }
        return items;    
    }
    
    
    
    
    
  //get information for delivery confirm email : We just delivered a shipment on your order
  
    @Override
    protected Shipment extractShipment(String text) {
     
        Shipment shipment = new Shipment();
        
        //tracking
        Pattern track = Pattern.compile("[0-9]+ ([-a-zA-Z ]*) tracking number:\\s.{1}?([a-zA-Z0-9]*)");
        Matcher m = track.matcher(text);
        if(m.find()) {
            shipment.carrier = m.group(1);
            shipment.trackingNum = m.group(2);
            //System.out.println(m.group(2));
        }
        
        //order number
        Pattern order = Pattern.compile("Order #: ([0-9-]+)");
        Matcher m1 = order.matcher(text);
        if(m1.find()) {
            shipment.orderNum = m1.group(1);
        }
        
        
        //get items
        //put item in products
        
        ArrayList<String> items = getItems(text, "Return Code:");
        ArrayList<Product> products = new ArrayList();
        for(String item : items) {
            
            Pattern orderLine = Pattern.compile("([^$]*) ([1-9][0-9]?) \\$[0-9.]+\\s\\$[0-9.]+\\s");
            Matcher m2 = orderLine.matcher(item);
            boolean found = false;
            while(m2.find()) {
                String name = m2.group(1);
                int count = Integer.parseInt(m2.group(2));
                
                for (Product prod : products) {
                    if (name.equals(prod.name)) {
                        prod.count += count;
                        found = true;
                        break;
                    }
                }
                
                if(!found) {
                    products.add(new Product(name, count));
                }
            }
        }
        
        shipment.products = products;
        
        return shipment;
    }
    
    public static void splitLego(String items) {
        
        String[] itemSet = items.split("LEGO®");
        Pattern itemP = Pattern.compile("(.*)([0-9]+)$");
        for(String item : itemSet) {
            
            item = item.trim();
            //System.out.println(item);
            Matcher m = itemP.matcher(item);
            if(m.find()) {
                System.out.println("Lego " + m.group(1));
                System.out.println(m.group(2));
            }
        }
        
       
    }

    @Override
    public ArrayList<Shipment> extractShipments(Message email) {
        
        String[] body = getBody(email);
        Document doc = Jsoup.parse(body[1]);
        String content = doc.text();
        
        ArrayList<Shipment> found = new ArrayList();
        String[] shipTexts = content.split("Return Code");
        if (shipTexts.length > 2) {
            for (int i = 0; i < shipTexts.length - 1; i++) {
                try {
                    String tmpText = shipTexts[i] + "Return Code" + shipTexts[shipTexts.length - 1];
                    Shipment shipment = extractShipment(tmpText);
                    shipment.shipDate = email.getReceivedDate();
                    //ship to 
                    shipment.warehouse = getWarehouse(content);
                    found.add(shipment);
                } catch (MessagingException ex) {
                    LoggingAspect.addException(ex);
                }

            }
        }
        
        else {
            try {
                Shipment shipment = extractShipment(content);
                shipment.shipDate = email.getReceivedDate();
                //ship to 
                shipment.warehouse = getWarehouse(content);
                found.add(shipment);
            } catch (MessagingException ex) {
                LoggingAspect.addException(ex);
            }
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
