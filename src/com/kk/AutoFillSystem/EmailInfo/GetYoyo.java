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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
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
        emailSender = "customercare@yoyo.com";
        orderSubject = "Your order at YoYo.com";
        shipSubject = "Your order from YoYo.com has shipped";
        storeName = "Yoyo";
    }
    
    public static void main(String[] args) throws MessagingException, AddressException, ParseException {

        
        GetYoyo query = new GetYoyo("gobetterkx@gmail.com","Bnmrc123");
        //query.connectGmail();
        //query.searchInfoSince("06/13/16");
//        
        String html = "YoYo.com Hi ivy, Thank you for shopping at YoYo.com. Your Order ID is 56042147. Please refer to this number if you need to contact us. NOTE: Items may arrive in multiple boxes, from multiple carriers and can be delivered as late as 8pm.   Here are your order details:   Items ship free and will arrive Tomorrow, Tuesday, Jun. 14 Qty Description Item Price Discount Subtotal 3 LEGO Super Heroes Rhino and Sandman Super Villain Team-up - 76037 $36.49 $21.89 $87.58 3 LEGO Super Heroes Super Heroes Spider-Helicopter Rescue - 76016 $38.48 $23.09 $92.35   Order Summary Subtotal: $179.93 Shipping: $0.00 Total $179.93   Shipping info: 6620 NE 79TH CT STE 4 C/O RSJEP PORTLAND, OR  97218 United States NOTE: If items are shipping directly from the manufacturer, you'll receive a separate email with tracking information once the items ship. Order History, Status & Returns To view your order and track its delivery status, sign in to your account at YoYo.com and go to Order History/Status under My Account. From there, you can also cancel orders, report issues, print receipts, and return items. We want you to be 100% satisfied with your order, and if you're not, simply print out a return label and send the item back to us within a year from purchase. The return shipping is on us. To read more about our 365-day return policy, go to our Help Center. Easy Reorder Want to reorder in a flash? Just go to Easy Reorder to find products you've purchased from us and any of our Familyhood sites. It's the time-saving way to shop for the stuff you love! Refer-a-Friend Remember, your Savings Code is GOBE6254. Learn more about our Refer-a-Friend program and how you can share some savings. Customer Care Our amazing Customer Care team is always here for you, 24/7. Give us a call at 1-866-YOYO-123 (1-866-969-6123) or email us at customercare@yoyo.com. We're dedicated to delivering the best shopping experience possible. Thanks for choosing us! The Team at YoYo.com Items sold by Quidsi Retail LLC are subject to sales tax in select states in accordance with the applicable laws of that state. Depending on the laws of your state, you may have additional tax requirements: state sales/use tax information. This email was sent to you by VineMarket.com. To ensure delivery to your inbox (not bulk or junk folders), you can add yoyo@store.yoyo.com to your address book or safe list. YoYo.com is operated by Quidsi Solutions LLC. Products on YoYo.com are sold by Quidsi Retail LLC. Gift Cards and E-Gift Cards sold on YoYo.com are sold by Quidsi Gift Cards, Inc. © 2005 - 2016, Quidsi, Inc. or its affiliates. All Rights Reserved. 10 Exchange Place, 25th Floor, Jersey City, NJ 07302";
        query.extractOrder(html);
        
       
        
//        ArrayList<Order> newOrders = query.getOrders();
//        for(Order orderInfo : newOrders) {
//            createNewOrder(em, orderInfo);
//        }
//        
//        
//        ArrayList<Shipment> newShips = query.getShipments();
//        
//        for(Shipment shipInfo : newShips) {
//            createUsTrk(em, shipInfo);
//        }
//       
//
//        em.close();
//        emf.close();
//    
//    
        
        

   }
    
    

    @Override
    public Order extractOrder(String text) {
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
        int start = text.indexOf("Qty Description");
        Pattern prods = Pattern.compile("([0-9]{1,2}) ([^\\$]*) \\$[0-9.]+ \\$[0-9.]+ \\$[0-9.]+");
        Matcher m1 = prods.matcher(text.substring(start));
        while(m1.find()) {
            Product temp = new Product();
            temp.name = m1.group(2);
            temp.count = Integer.parseInt(m1.group(1));
            products.add(temp);
            
        }
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
            return found;
        } catch (MessagingException ex) {
            LoggingAspect.addException(ex);
        }
        
        return found;
    }
    
}
