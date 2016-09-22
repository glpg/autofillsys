/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.EmailInfo;

import static com.kk.AutoFillSystem.Database.Operations.TrackOp.createUsTrk;
import static com.kk.AutoFillSystem.EmailInfo.GetStore.getBody;
import com.kk.AutoFillSystem.utility.LoggingAspect;
import com.kk.AutoFillSystem.utility.Order;
import com.kk.AutoFillSystem.utility.Product;
import com.kk.AutoFillSystem.utility.Shipment;
import static com.kk.AutoFillSystem.utility.Tools.getWarehouse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Yi
 */
public class GetToysrus extends GetStore{
    
    public GetToysrus(String email, String pwd) { 
        super();
        this.email = email;
        this.pwd = pwd;
        emailSender = "ToysRUs";
        orderSubject = "Babies R Us Order Confirmation";
        shipSubject = "Babiesrus.com Shipment Confirmation";
        storeName = "Toysrus";
    }
    
    
    public static void main(String[] args) throws MessagingException, AddressException, ParseException {
        //get entitymanager
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AutoFillSystemPU");
        EntityManager em = emf.createEntityManager();
        
        //query returned info of orders and shipments
        //using the orderInfo and shipmentInfo, to create new orders and trackings to db
        
        GetToysrus query = new GetToysrus("fatblackcat2016@gmail.com","bnmrc123");
        query.connectGmail();
        //query.searchInfoSince("09/01/16");
        
//        ArrayList<Order> newOrders = query.getOrders();
//        for(Order orderInfo : newOrders) {
//            createNewOrder(em, orderInfo);
//        }
       
        ArrayList<Shipment> newShips = query.getShipments();
        
        for(Shipment shipInfo : newShips) {
            createUsTrk(em, shipInfo);
            
        }
       
        em.close();
        emf.close();
        
   
       
                
   }
    
   
    @Override
    public Order extractOrder(String text) {
        
        Order order = new Order();
        //get order number
        Pattern orderNum = Pattern.compile("Your order number is ([0-9]+)");
        Matcher m = orderNum.matcher(text);
        if(m.find()) {
            order.orderNum = m.group(1);
            
        }
        
        //get items
        ArrayList<Product> products = new ArrayList();
        
        Pattern orderLine = Pattern.compile("Description: ([^@]*)Quantity: ([0-9]+)");
        Matcher m2 = orderLine.matcher(text);
        while(m2.find()) {
            
            boolean found = false;
   
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
        
        order.products = products;
        
        return order;
    }

    @Override
    public Shipment extractShipment(String text) {
        Shipment shipment = new Shipment();
        //order number is in subject, processed in getStore
        
        //get carrier and tracking number
        Pattern trkline = Pattern.compile("Shipped via: ([^@]*) Tracking Number: ([a-zA-Z0-9]+)");
        Matcher m = trkline.matcher(text);
        if(m.find()){
            if (m.group(1).contains("US Postal Service")) shipment.carrier = "USPS";
            if (m.group(1).contains("FedEx")) shipment.carrier = "FedEx";
            shipment.trackingNum = m.group(2);
        }
        
        
        
        //get items and tracking number
        ArrayList<Product> products = new ArrayList();
        Pattern shipLine = Pattern.compile("Description: ([^@]*)Quantity: ([0-9]+)");
        Matcher m2 = shipLine.matcher(text);
        while(m2.find()) {
            boolean found = false;
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
        
        shipment.products = products;
        
        return shipment;
    }

    @Override
    public ArrayList<Shipment> extractShipments(Message email) {
        String[] body = getBody(email);
        Document doc = Jsoup.parse(body[1]);
        String content = doc.text();
        
        ArrayList<Shipment> found = new ArrayList();
        
        //split track blocks
        String[] shipTexts = content.split("Track @:");
        
        //check if blocks have different tracking number
        Map<String, String> uniqueTrack = new HashMap();
        for(String shiptext : shipTexts) {
            Pattern trkline = Pattern.compile("Shipped via: ([^@]*) Tracking Number: ([a-zA-Z0-9]+)");
            Matcher m = trkline.matcher(shiptext);
            if(m.find()) {
                String trkNum = m.group(2);
                if(uniqueTrack.containsKey(trkNum)) {
                    String newText = uniqueTrack.get(trkNum);
                    newText += shiptext;
                    uniqueTrack.put(trkNum, newText);
                }
                else 
                    uniqueTrack.put(trkNum, shiptext);
            }
        }
        
        //now start to extract shipment
        for(String tmp : uniqueTrack.values()) {
            Shipment temp = extractShipment(tmp);
            try {
                String subject = email.getSubject();
                Pattern orderNum = Pattern.compile("Order # ([0-9]+)");
                Matcher m = orderNum.matcher(subject);
                
        
                if (m.find()) {
                    temp.orderNum = m.group(1);
                }
                
                temp.shipDate = email.getReceivedDate();
                //get warehouse
                temp.warehouse = getWarehouse(content);
            } catch (MessagingException ex) {
                LoggingAspect.addException(ex);
            }
            found.add(temp);
        }
        
        
        return found;
    }
    
}
