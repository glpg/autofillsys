/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Operations;

import com.kk.AutoFillSystem.Database.Entities.Carriers;
import com.kk.AutoFillSystem.Database.Entities.Orderlines;
import com.kk.AutoFillSystem.Database.Entities.Orders;
import com.kk.AutoFillSystem.Database.Entities.Stores;
import com.kk.AutoFillSystem.Database.Entities.Trklines;
import com.kk.AutoFillSystem.Database.Entities.Ustrkings;
import com.kk.AutoFillSystem.Database.Services.CarrierService;
import com.kk.AutoFillSystem.Database.Services.OrderService;
import com.kk.AutoFillSystem.Database.Services.OrderlineService;
import com.kk.AutoFillSystem.Database.Services.StoreService;
import com.kk.AutoFillSystem.Database.Services.TrkinglineService;
import com.kk.AutoFillSystem.Database.Services.UStrackingService;
import static com.kk.AutoFillSystem.utility.LoggingAspect.addMessage;
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
    public static void createUsTrk(EntityManager em, Shipment shipmentInfo){ 
        // Create new todo
        em.getTransaction().begin();
        
        OrderService orderService = new OrderService(em, Orders.class);
        UStrackingService ustrkService = new UStrackingService(em, Ustrkings.class);
        TrkinglineService trklineService = new TrkinglineService(em, Trklines.class);
        CarrierService carrierService = new CarrierService(em, Carriers.class);
        
        //check if order existed already
        
        TypedQuery<Orders> orderQuery = em.createNamedQuery("Orders.findByOrderNum", Orders.class).setParameter("orderNum", shipmentInfo.orderNum);
        List<Orders> orderResults = orderQuery.getResultList();
        //if existed ,add the shipping details
        if (orderResults != null && orderResults.size() > 0) {
            //create new trkings
            Ustrkings ustrking = new Ustrkings();
            //set order
            ustrking.setOrderId(orderResults.get(0));
            //set trking number
            ustrking.setTrkingNum(shipmentInfo.trackingNum);
            //set shipdate
            ustrking.setShipDate(shipmentInfo.shipDate);
            
            
            
            //set carrier
            String carrier = shipmentInfo.carrier; 
            
        }
        
    }
    
}
