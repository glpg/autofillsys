/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Operations;

import com.kk.AutoFillSystem.Database.Entities.Addresses;
import com.kk.AutoFillSystem.Database.Entities.Carriers;
import com.kk.AutoFillSystem.Database.Entities.Cntrkings;
import com.kk.AutoFillSystem.Database.Entities.Orders;
import com.kk.AutoFillSystem.Database.Entities.Products;
import com.kk.AutoFillSystem.Database.Entities.Trklines;
import com.kk.AutoFillSystem.Database.Entities.Usanduscntrkings;
import com.kk.AutoFillSystem.Database.Entities.Ustocntrkings;
import com.kk.AutoFillSystem.Database.Entities.Ustrkings;
import static com.kk.AutoFillSystem.Database.Operations.ProductOp.findProduct;
import com.kk.AutoFillSystem.Database.Services.AddressService;
import com.kk.AutoFillSystem.Database.Services.CarrierService;
import com.kk.AutoFillSystem.Database.Services.OrderService;
import com.kk.AutoFillSystem.Database.Services.TrkinglineService;
import com.kk.AutoFillSystem.Database.Services.UStrackingService;
import static com.kk.AutoFillSystem.utility.LoggingAspect.addMessage;
import static com.kk.AutoFillSystem.utility.LoggingAspect.addMessageWithDate;
import com.kk.AutoFillSystem.utility.Product;
import com.kk.AutoFillSystem.utility.Shipment;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author Yi
 */
public class TrackOp {
    
    //create new order  and orderlines
    public static Ustrkings createUsTrk(EntityManager em, Shipment shipmentInfo){ 
        // Create new todo
        
        Ustrkings ustrking = null;
        
        OrderService orderService = new OrderService(em, Orders.class);
        UStrackingService ustrkService = new UStrackingService(em, Ustrkings.class);
        TrkinglineService trklineService = new TrkinglineService(em, Trklines.class);
        CarrierService carrierService = new CarrierService(em, Carriers.class);
        AddressService addrService = new AddressService(em, Addresses.class);
        
        //check if trking existed already
        TypedQuery<Ustrkings> trkQuery = em.createNamedQuery("Ustrkings.findByTrkingNum", Ustrkings.class).setParameter("trkingNum", shipmentInfo.trackingNum);
        List<Ustrkings> trkResults = trkQuery.getResultList();
        
        //if existed already, skip
        if (trkResults != null && trkResults.size() > 0) {
            addMessage("Shipment " + shipmentInfo.trackingNum + " existed already, pass!");
            
        }
        //else create new shipment
        else {
            //find the order
            TypedQuery<Orders> orderQuery = em.createNamedQuery("Orders.findByOrderNum", Orders.class).setParameter("orderNum", shipmentInfo.orderNum);
            List<Orders> orderResults = orderQuery.getResultList();

            //if existed ,add the shipping details
            if (orderResults != null && orderResults.size() > 0) {
                em.getTransaction().begin();
                
                /* create new trkings */
                ustrking = new Ustrkings();
                //set order
                Orders order = orderResults.get(0);
                ustrking.setOrderId(order);
                //set trking number
                ustrking.setTrkingNum(shipmentInfo.trackingNum);
                //set shipdate
                ustrking.setShipDate(shipmentInfo.shipDate);

                //set warehouse
                String warehouse = shipmentInfo.warehouse;
                TypedQuery<Addresses> addrQuery = em.createNamedQuery("Addresses.findByName", Addresses.class).setParameter("name", warehouse);
                List<Addresses> addrResults = addrQuery.getResultList();

                if (addrResults != null && addrResults.size() > 0) {
                    ustrking.setAddressId(addrResults.get(0));
                } else {
                    ustrking.setAddressId((Addresses) addrService.find(0));
                    addMessageWithDate(shipmentInfo.trackingNum + " failed to autofill warehouse, please add manually.");
                }

                //set carrier
                String carrier = shipmentInfo.carrier;
                if (carrier.contains("FedEx")) {
                    carrier = "fedex";
                }
                if (carrier.contains("UPS")) {
                    carrier = "ups";
                }
                TypedQuery<Carriers> carrierQuery = em.createNamedQuery("Carriers.findByName", Carriers.class).setParameter("name", carrier);
                List<Carriers> carrierResults = carrierQuery.getResultList();
                if (carrierResults != null && carrierResults.size() > 0) {
                    ustrking.setCarrierId(carrierResults.get(0));
                } else {
                    ustrking.setCarrierId((Carriers) carrierService.find(0));
                    addMessageWithDate(shipmentInfo.trackingNum + " failed to autofill carrier, please add manually.");
                }

                //set trking to order
                order.getUstrkingsCollection().add(ustrking);
                ustrkService.create(ustrking);

                /*end of creating new trking for order*/
                /*create trking lines */
                for (Product prodInfo : shipmentInfo.products) {
                    Products prod = findProduct(em, prodInfo.name);

                    if (prod != null) {
                        Trklines trkLine = new Trklines();
                        //set trkId
                        trkLine.setUstrkingId(ustrking);
                        //set prod
                        trkLine.setProductId(prod);
                        //set count
                        trkLine.setQuantity(prodInfo.count);

                        //add trkline to ustrking
                        ustrking.getTrklinesCollection().add(trkLine);

                        //persist trkline
                        trklineService.create(trkLine);

                    } else {
                        addMessageWithDate("trkingNum : " + shipmentInfo.trackingNum + " failed to add the trkline details.");
                        addMessageWithDate(prodInfo.name + " " + prodInfo.count);
                    }

                }

                /*end of trking lines creation*/
                em.getTransaction().commit();
                addMessageWithDate("Shipment : " + shipmentInfo.trackingNum + " is created.");
            } else {
                addMessage("trkingNum : " + shipmentInfo.trackingNum + " could not match orders in db.");
            }

        }
        
        return ustrking;

        
    }
    
    
    public static void createIntlTrk(EntityManager em, Ustocntrkings intlTrk, Ustrkings ustrk){
        em.getTransaction().begin();
        //persist new intl trk
        em.persist(intlTrk);
        addMessageWithDate("Intl shipment : " + intlTrk.getTrkingNum() + " is created.");
        //create new relationrecord
        Usanduscntrkings relation = new Usanduscntrkings();
        relation.setUstocntrkingId(intlTrk);
        relation.setUstrkingId(ustrk);
        em.persist(relation);
        addMessageWithDate("Intl shipment : " + intlTrk.getTrkingNum() + " is correlated with Us trking : " + ustrk.getTrkingNum() + " .");
        //add relation to both intlTrk and usTrk
        ustrk.getUsanduscntrkingsCollection().add(relation);
        intlTrk.getUsanduscntrkingsCollection().add(relation);
        
        em.getTransaction().commit();
        
    }
    
    public static void createCnTrk(EntityManager em, Cntrkings cnTrk){
        em.getTransaction().begin();
        //persist new intl trk
        em.persist(cnTrk);
        addMessageWithDate("China shipment : " + cnTrk.getTrkingNum() + " is created.");
        
        cnTrk.getUstocntrkingId().getCntrkingsCollection().add(cnTrk);
        
        em.getTransaction().commit();
        
    }
    
    public static Ustrkings updateUsTrk(EntityManager em, Ustrkings ustrk) {
        em.getTransaction().begin();
        //persist new intl trk
        Ustrkings updated  = em.merge(ustrk);
        addMessageWithDate("US shipment : " + ustrk.getTrkingNum() + " is updated.");
        
        em.getTransaction().commit();
        return updated;
    }
    
    public static void createNewTrkline(EntityManager em, Trklines trkline) {
        em.getTransaction().begin();
        //persist new intl trk
        em.persist(trkline);
        addMessageWithDate("New trkline : " + trkline.getProductId().getProdNum() +  " for Us trking " + trkline.getUstrkingId().getTrkingNum()+ " is created.");
        
        em.getTransaction().commit();    
    }
    
}

