/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Operations;

import com.kk.AutoFillSystem.Database.Entities.Orderlines;
import com.kk.AutoFillSystem.Database.Entities.Orders;
import com.kk.AutoFillSystem.Database.Entities.Products;
import com.kk.AutoFillSystem.Database.Entities.Stores;
import static com.kk.AutoFillSystem.Database.Operations.ProductOp.findProduct;
import com.kk.AutoFillSystem.Database.Services.OrderService;
import com.kk.AutoFillSystem.Database.Services.OrderlineService;
import com.kk.AutoFillSystem.Database.Services.StoreService;
import static com.kk.AutoFillSystem.utility.LoggingAspect.addMessage;
import static com.kk.AutoFillSystem.utility.LoggingAspect.addMessageWithDate;
import com.kk.AutoFillSystem.utility.Order;
import com.kk.AutoFillSystem.utility.Product;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author Yi
 */
public class OrderOp {
    
    //create new order  and orderlines
    public static Orders createNewOrder(EntityManager em, Order orderInfo){ 
        // Create new todo
        
        if (orderInfo.orderNum == null) return null;
        
        OrderService orderService = new OrderService(em, Orders.class);
        StoreService storeService = new StoreService(em, Stores.class);
        OrderlineService orderlineService = new OrderlineService(em, Orderlines.class);
        
        //check if order existed already
        
        TypedQuery<Orders> orderQuery = em.createNamedQuery("Orders.findByOrderNum", Orders.class).setParameter("orderNum", orderInfo.orderNum);
        List<Orders> orderResults = orderQuery.getResultList();
        //if existed already, skip
        if (orderResults != null && orderResults.size() > 0) {
            addMessage("Order " + orderInfo.orderNum + " existed already, pass!");
            
            return null;
        }
        //else create new order
        else {
            em.getTransaction().begin();
            //create new orders
            Orders order = new Orders();
            order.setOrderDate(orderInfo.orderDate);
            order.setOrderNum(orderInfo.orderNum);

            //get store 
            TypedQuery<Stores> query = em.createNamedQuery("Stores.findByName", Stores.class).setParameter("name", orderInfo.storeName);
            List<Stores> results = query.getResultList();
            Stores store = null;
            if (results != null && results.size() > 0) {
                store = results.get(0);
            }

            order.setStoreId(store);
            orderService.create(order);
            
            //get orderlines
            for(Product prodInfo : orderInfo.products) {
                
                Products prod = findProduct(em, prodInfo.name);
                
                if (prod != null) {
                    Orderlines orderLine = new Orderlines();
                    //set orderId
                    orderLine.setOrderId(order);
                    //set prod
                    orderLine.setProductId(prod);
                    //set count
                    orderLine.setQuantity(prodInfo.count);
                    
                    //add orderline to order
                    order.getOrderlinesCollection().add(orderLine);
                    //persist orderline
                    orderlineService.create(orderLine);

                }
                else{
                    addMessageWithDate("orderNum : " + orderInfo.orderNum + " failed to add the orderline details.");
                    addMessageWithDate(prodInfo.name + " " + prodInfo.count);
                }
  
                
            }
            em.getTransaction().commit();
            addMessageWithDate("Order : " + orderInfo.orderNum +" is created.");
            return order;

        }
     
    }
    
    
    public static boolean createNewOrderline(EntityManager em, Orderlines orderline) {
        em.getTransaction().begin();
        //persist new intl trk
        em.persist(orderline);
        addMessageWithDate("New orderline : " + orderline.getProductId().getProdNum() +  " for order " + orderline.getOrderId().getOrderNum()+ " is created.");
        
        em.getTransaction().commit();  
        return (em.contains(orderline));
        
    }
    
    public static void delOrderline(EntityManager em, Orderlines orderline) {
        
        em.getTransaction().begin();
        //persist new intl trk
        em.remove(orderline);
        
        em.getTransaction().commit();  
        addMessageWithDate("Orderline : " + orderline.getProductId().getProdNum() +  " for order " + orderline.getOrderId().getOrderNum()+ " is removed.");
      
    }
    
    public static boolean createNewOrderFromEntity(EntityManager em, Orders order) {
        em.getTransaction().begin();
        //persist new intl trk
        em.persist(order);
        addMessageWithDate("New order : " + order.getOrderNum()+ " is created.");
        
        em.getTransaction().commit();   
        
        return (em.contains(order));
        
    }
    
}
