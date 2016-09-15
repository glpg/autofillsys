/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.utility;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.Database.Entities.Addresses;
import com.kk.AutoFillSystem.Database.Entities.Carriers;
import com.kk.AutoFillSystem.Database.Entities.Cntrkings;
import com.kk.AutoFillSystem.Database.Entities.Orderlines;
import com.kk.AutoFillSystem.Database.Entities.Orders;
import com.kk.AutoFillSystem.Database.Entities.Products;
import com.kk.AutoFillSystem.Database.Entities.Stores;
import com.kk.AutoFillSystem.Database.Entities.Trklines;
import com.kk.AutoFillSystem.Database.Entities.Ustocntrkings;
import com.kk.AutoFillSystem.Database.Entities.Ustrkings;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
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
    
    public static Ustrkings findUsTrk(List<Ustrkings> shipments, String[] info){
        for(Ustrkings ustrk : shipments) {
            if (ustrk.getTrkingNum().equalsIgnoreCase(info[0]) &&
                    ustrk.getOrderId().getOrderNum().equals(info[1]))
                return ustrk;
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

            br = new BufferedReader(new FileReader("e:\\standard.csv"));

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
                
                if (splits[7].length() >= 3)  {
                    entry.weight = (int)(Double.parseDouble(splits[7]) *1000.0) ;
                    
                }
                if(splits[8].length() >= 3)
                    entry.fee = Double.parseDouble(splits[8]);
                
                if (splits.length >=10 && splits[9].length() > 5) {
                    String[] cntrk = splits[9].replace("?", "").split("-");
                    entry.cnCarrier = cntrk[0].trim();
                    entry.cnTrkNum = cntrk[1].trim();
                }
                
                
                if (splits.length == 11 && splits[10].length() >= 2)
                    entry.destination = splits[10].trim();
                
                entries.add(entry);
                System.out.println(entry.toString());
                
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
        
        List<Ustrkings> ustrkings = dataCenter.getUstrkings();
        
 //create intl and cn trkings
        Map<String, Ustocntrkings> addedIntlTrks = new HashMap();
        for(Ustocntrkings tmp : dataCenter.getIntlTrkings()) {
            addedIntlTrks.put(tmp.getTrkingNum(), tmp);
        }
        Map<String, Cntrkings> addedCnTrks = new HashMap();
        for(Cntrkings tmp : dataCenter.getCnTrkings()) {
            addedCnTrks.put(tmp.getTrkingNum(), tmp);
        }
        
        for(Entry entry : entries) {
            Ustocntrkings intltrk;
            Cntrkings cntrk;
            
            if (entry.intlTrkNum != null) {
                if(addedIntlTrks.get(entry.intlTrkNum) != null) 
                    intltrk = addedIntlTrks.get(entry.intlTrkNum);
                else {
                    //create new intl trk
                    intltrk = new Ustocntrkings();
                    intltrk.setAddressId(findAddress(addresses, entry.warehouse));
                    intltrk.setTrkingNum(entry.intlTrkNum);
                    if (entry.weight != 0)
                        intltrk.setWeight(entry.weight);
                    if (entry.fee != 0.0)
                        intltrk.setShippingfee(BigDecimal.valueOf(entry.fee));
                    
                    String[] info = new String[]{entry.usTrkNum, entry.orderNum};
                    
                    Ustrkings ustrk = findUsTrk(ustrkings, info);
                    //create data and put in map
                    dataCenter.createIntlTrking(intltrk, ustrk);
                    addedIntlTrks.put(entry.intlTrkNum, intltrk);
                }

                //now check if cntrk exsits
                if (entry.cnTrkNum == null) {
                    continue;
                } else {
                    if (addedCnTrks.get(entry.cnTrkNum) != null) {
                        continue;
                    }
                    else{
                        cntrk = new Cntrkings();
                        cntrk.setCarrierId(findCarrier(carriers, entry.cnCarrier));
                        cntrk.setTrkingNum(entry.cnTrkNum);
                        cntrk.setUstocntrkingId(intltrk);
                        if (entry.destination.contains("kang") || entry.destination.contains("xue")) {
                            cntrk.setAddressId(dataCenter.getAddress(8));
                            
                        }
                        else {
                            if (entry.destination.contains("yi"))
                                cntrk.setAddressId(dataCenter.getAddress(4));
                            else 
                                cntrk.setAddressId(dataCenter.getAddress(7));
                        }
                        
                        dataCenter.createCnTrking(cntrk);
                        addedCnTrks.put(entry.cnTrkNum, cntrk);
                        
                    }


                    
                    if (entry.destination != null) {
                        if (entry.destination.contains("kang") || entry.destination.contains("xue") 
                                || entry.destination.contains("yi") || entry.destination.contains("song")){
                            
                        }
                        else{
                            System.out.println(entry.orderNum + " desitnation error");
                        }
                        
                            
                    }
                    
                    
                    
                }
                
                
                
            }
            else continue;
            
        }
        
        
        
 /**
  * create entries for orders and tracks
  */      
//        Map<String, Orders> addedOrders = new HashMap();
//        Map<String, Ustrkings> addedUsShip = new HashMap();
//        for(Entry entry : entries) {
//            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY");
//            Date date = sdf.parse("09/01/2016");
//            
//            
//            Orders order;
//            Ustrkings ustrk;
//            //if order exsited
//            if (addedOrders.get(entry.orderNum) != null)
//                order = addedOrders.get(entry.orderNum);
//            else {
//                order = new Orders();
//                order.setOrderNum(entry.orderNum);
//                
//                order.setOrderDate(date);
//                switch (entry.store) {
//                    case "TRU": {
//                        order.setStoreId(findStore(stores, "Toysrus"));
//                        break;
//                    }
//                    default:
//                        order.setStoreId(findStore(stores, entry.store));
//                }
//                
//                dataCenter.createOrder(order);
//                addedOrders.put(entry.orderNum, order);
//
//            }
//            
//            //now do items
//            Orderlines ol = new Orderlines();
//            
//            
//            ol.setProductId(findProduct(products, entry.prodNum));
//            ol.setQuantity(entry.count);
//            ol.setOrderId(order);
//            dataCenter.createOrderline(ol);
//            
//            
//            
//            
//            //now do trkings
//            
//            
//            if (entry.usTrkNum != null) {
//                if (addedUsShip.get(entry.usTrkNum) != null &&
//                        addedUsShip.get(entry.usTrkNum).getOrderId().getOrderNum().equals(entry.orderNum))  {
//                    
//                    ustrk = addedUsShip.get(entry.usTrkNum);
//                    
//                }
//                else {
//                    ustrk = new Ustrkings();
//                    ustrk.setTrkingNum(entry.usTrkNum);
//                    ustrk.setOrderId(order);
//                    ustrk.setShipDate(date);
//                    ustrk.setCarrierId(findCarrier(carriers, entry.carrier));
//                    ustrk.setAddressId(findAddress(addresses, entry.warehouse));
//
//                    dataCenter.createUsTrking(ustrk);
//                    //add to hashmap
//                    addedUsShip.put(entry.usTrkNum, ustrk);
//                    //create trkline
//                    Trklines tl = new Trklines();
//                    tl.setProductId(findProduct(products, entry.prodNum));
//                    tl.setQuantity(entry.count);
//                    tl.setUstrkingId(ustrk);
//                    dataCenter.createTrkline(tl);
//
//                }
//               
//                
//
//            }
//            
//            
//            
//            
//            
//
//       
//        }
     //end of creating orders and tracks
        
        
        
        
        
        
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