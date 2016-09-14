/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.utility;

import static com.kk.AutoFillSystem.Database.Operations.OrderOp.createNewOrder;
import static com.kk.AutoFillSystem.Database.Operations.TrackOp.createUsTrk;
import com.kk.AutoFillSystem.EmailInfo.GetWalmart;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Yi
 */
public class ReadRecordFromFile {
    
    
    
    public static void main(String[] args) throws MessagingException, AddressException, ParseException {
        
        ArrayList<Entry> entries = new ArrayList();
        //read file
        BufferedReader br = null;
        
        //ArrayList<Products> prods = new ArrayList();

        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader("e:\\Book1.csv"));

            while ((sCurrentLine = br.readLine()) != null) {
                
                Entry entry = new Entry();
                String[] splits = sCurrentLine.split(",");
                
                entry.prodNum = splits[0].trim();
                entry.count = Integer.parseInt(splits[1].trim());
                entry.store = splits[2];
                entry.orderNum = splits[3].trim().replace("#", "");
                
                if (splits[4].contains("-")) {
                    String[] ustrk = splits[4].split("-");
                    entry.carrier = ustrk[0].trim();
                    entry.usTrkNum = ustrk[1].trim();
                 }
                else {
                    entry.carrier = "Amazon Logistics";
                    entry.usTrkNum = splits[4].trim();
                }
                
                entry.warehouse = splits[5].trim();
                
                entry.intlTrkNum = splits[6].trim();
                
                if (!splits[7].isEmpty())  {
                    entry.weight = (int)(Double.parseDouble(splits[7]) *1000.0) ;
                    
                }
                if(!splits[8].isEmpty())
                    entry.fee = Double.parseDouble(splits[8]);
                
                String[] cntrk = splits[9].split("-");
                entry.cnCarrier = cntrk[0].trim();
                entry.cnTrkNum = cntrk[1].trim();
                
                if (splits.length ==11)
                    entry.destination = splits[10].trim();
                
                System.out.println(entry.toString());
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
        
        
        
//        
//        
//        //get entitymanager
//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AutoFillSystemPU");
//        EntityManager em = emf.createEntityManager();
//        
//        //query returned info of orders and shipments
//        //using the orderInfo and shipmentInfo, to create new orders and trackings to db
//        
//        GetWalmart query = new GetWalmart("gobetterkx@gmail.com","Bnmrc123");
//        query.connectGmail();
//        query.searchInfoSince("07/22/16");
//        
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