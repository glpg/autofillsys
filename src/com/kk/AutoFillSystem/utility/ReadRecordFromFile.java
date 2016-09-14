/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.utility;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Addresses;
import com.kk.AutoFillSystem.Database.Entities.Carriers;
import com.kk.AutoFillSystem.Database.Entities.Orderlines;
import com.kk.AutoFillSystem.Database.Entities.Orders;
import com.kk.AutoFillSystem.Database.Entities.Products;
import com.kk.AutoFillSystem.Database.Entities.Stores;
import com.kk.AutoFillSystem.Database.Entities.Trklines;
import com.kk.AutoFillSystem.Database.Entities.Ustrkings;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

/**
 *
 * @author Yi
 */
public class ReadRecordFromFile {
    
    public static Stores findStore(List<Stores> stores, String storeName) {
        for(Stores store : stores) {
            if (store.getName().equalsIgnoreCase(storeName))
                return store;
        }
        return null;
    }
    
    public static Carriers findCarrier(List<Carriers> carriers, String carrierName) {
        for(Carriers carrier : carriers) {
            if (carrier.getName().equalsIgnoreCase(carrierName))
                return carrier;
        }
        return null;
    }
    
    public static Addresses findAddress(List<Addresses> addresses, String addrName) {
        for(Addresses addr : addresses) {
            if (addr.getName().equalsIgnoreCase(addrName))
                return addr;
        }
        return null;
    }
    
    
    public static Products findProduct(List<Products> products, String prdName) {
        for(Products prd : products) {
            
            if (prd.getProdNum().equals(prdName.trim()))
                return prd;
        }
        return null;
    }

    
    
    public static void main(String[] args) throws MessagingException, AddressException, ParseException, IOException {
        
        ArrayList<Entry> entries = new ArrayList();
        //read file
        BufferedReader br = null;
        
        //ArrayList<Products> prods = new ArrayList();

        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader("e:\\standard.txt"));

            while ((sCurrentLine = br.readLine()) != null) {
                
                Entry entry = new Entry();
                String[] splits = sCurrentLine.split(",");
                
                
                entry.prodNum = splits[0].trim();
                
                entry.count = Integer.parseInt(splits[1].trim());
                entry.store = splits[2];
                entry.orderNum = splits[3].trim().replace("#", "").replace("?","");
                
                
                if (splits[4].contains("-")) {
                    String[] ustrk = splits[4].split("-");
                    entry.carrier = ustrk[0].trim();
                    entry.usTrkNum = ustrk[1].trim().replace("?","");
                 }
                else {
                    if (!splits[4].equals(" ")) {
                        entry.carrier = "Amazon Logistics";
                        entry.usTrkNum = splits[4].trim().replace("?","");
                    }
                    
                }
                
                entry.warehouse = splits[5].trim();
                
                if (splits[6].length() > 3)
                    entry.intlTrkNum = splits[6].trim();
                
                if (splits[7].length() > 3)  {
                    entry.weight = (int)(Double.parseDouble(splits[7]) *1000.0) ;
                    
                }
                if(splits[8].length() > 3)
                    entry.fee = Double.parseDouble(splits[8]);
                
                if (splits[9].length() > 5) {
                    String[] cntrk = splits[9].split("-");
                    entry.cnCarrier = cntrk[0].trim();
                    entry.cnTrkNum = cntrk[1].trim();
                }
                
                
                if (splits[10].length() > 5)
                    entry.destination = splits[10].trim();
                
                entries.add(entry);
                
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
    
        DataController dataCenter = new DataController();
        List<Stores> stores = dataCenter.getStores();
        List<Products> products = dataCenter.getProducts();
        List<Addresses> addresses = dataCenter.getAddresses();
        List<Carriers> carriers = dataCenter.getCarriers();
        
       
        Map<String, Orders> addedOrders = new HashMap();
        Map<String, Ustrkings> addedUsShip = new HashMap();
        for(Entry entry : entries) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY");
            Date date = sdf.parse("09/01/2016");
            
            
            Orders order;
            Ustrkings ustrk;
            //if order exsited
            if (addedOrders.get(entry.orderNum) != null)
                order = addedOrders.get(entry.orderNum);
            else {
                order = new Orders();
                order.setOrderNum(entry.orderNum);
                
                order.setOrderDate(date);
                switch (entry.store) {
                    case "TRU": {
                        order.setStoreId(findStore(stores, "Toysrus"));
                        break;
                    }
                    default:
                        order.setStoreId(findStore(stores, entry.store));
                }
                
                dataCenter.createOrder(order);
                addedOrders.put(entry.orderNum, order);

            }
            
            //now do items
            Orderlines ol = new Orderlines();
            
            
            ol.setProductId(findProduct(products, entry.prodNum));
            ol.setQuantity(entry.count);
            ol.setOrderId(order);
            dataCenter.createOrderline(ol);
            
            
            
            
            //now do trkings
            
            
            if (entry.usTrkNum != null) {
                if (addedUsShip.get(entry.usTrkNum) != null &&
                        addedUsShip.get(entry.usTrkNum).getOrderId().getOrderNum().equals(entry.orderNum))  {
                    
                    ustrk = addedUsShip.get(entry.usTrkNum);
                    
                }
                else {
                    ustrk = new Ustrkings();
                    ustrk.setTrkingNum(entry.usTrkNum);
                    ustrk.setOrderId(order);
                    ustrk.setShipDate(date);
                    ustrk.setCarrierId(findCarrier(carriers, entry.carrier));
                    ustrk.setAddressId(findAddress(addresses, entry.warehouse));

                    dataCenter.createUsTrking(ustrk);
                    //add to hashmap
                    addedUsShip.put(entry.usTrkNum, ustrk);
                    //create trkline
                    Trklines tl = new Trklines();
                    tl.setProductId(findProduct(products, entry.prodNum));
                    tl.setQuantity(entry.count);
                    tl.setUstrkingId(ustrk);
                    dataCenter.createTrkline(tl);

                }
               
                

            }
            
            
            
            
            

       
        }
//
//
//                
   }
    
    
}


class Entry {
    public String prodNum;
    public int count;
    public String store;
    public String orderNum;
    public String carrier;
    public String usTrkNum;
    public String warehouse;
    public String intlTrkNum;
    public int weight;
    public double fee;
    public String cnCarrier;
    public String cnTrkNum;
    public String destination;
    
    public String toString() {
        return store +" " + orderNum +" " + prodNum + ":" + count + " " + carrier + " " + usTrkNum + " " + warehouse + " " +
                intlTrkNum + " " + weight + " " + fee + " "+cnCarrier + " " + cnTrkNum + " " + destination;
    }
    
    
}