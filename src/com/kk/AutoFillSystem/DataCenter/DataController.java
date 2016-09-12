/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.DataCenter;

import com.kk.AutoFillSystem.Database.Entities.Addresses;
import com.kk.AutoFillSystem.Database.Entities.Carriers;
import com.kk.AutoFillSystem.Database.Entities.Orders;
import com.kk.AutoFillSystem.Database.Entities.Products;
import com.kk.AutoFillSystem.Database.Entities.Stores;
import com.kk.AutoFillSystem.Database.Entities.Ustrkings;
import static com.kk.AutoFillSystem.Database.Operations.OrderOp.createNewOrder;
import static com.kk.AutoFillSystem.Database.Operations.TrackOp.createUsTrk;
import com.kk.AutoFillSystem.Database.Services.AddressService;
import com.kk.AutoFillSystem.Database.Services.CarrierService;
import com.kk.AutoFillSystem.Database.Services.OrderService;
import com.kk.AutoFillSystem.Database.Services.ProductService;
import com.kk.AutoFillSystem.Database.Services.StoreService;
import com.kk.AutoFillSystem.utility.Order;
import com.kk.AutoFillSystem.utility.Shipment;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Yi
 */
public class DataController {
    
    
    //singleton instance 
    private static DataController instance = null;
    
    //db accessing
    private static EntityManagerFactory emf ;
    private static EntityManager em ;
    
    
    public DataController() {
        //initialize db connection
        emf = Persistence.createEntityManagerFactory("AutoFillSystemPU");
        em = emf.createEntityManager();
        
    }
    
   
    
    public static DataController getInstance() {
      if(instance == null) {
         instance = new DataController();
      }
      return instance;
    }

    
    /**
     * Get db data
     **/
    
    public List<Orders> getOrders() {
        OrderService os = new OrderService(em, Orders.class);
        return os.findAll();
    }
    
    public List<Stores> getStores() {
        StoreService ss = new StoreService(em, Stores.class);
        return ss.findAll();
    }
    
    public List<Carriers> getCarriers() {
        CarrierService cs = new CarrierService(em, Carriers.class);
        return cs.findAll();
    }
    
    public List<Addresses> getAddresses() {
        AddressService as = new AddressService(em, Addresses.class);
        return as.findAll();
    }
    
    public List<Products> getProducts() {
        ProductService ps = new ProductService(em, Products.class);
        return ps.findAll();
    }
    
    public Orders createOrder(Order orderInfo) {
        return createNewOrder(em, orderInfo);
    }
    
    public Ustrkings createUsTrking(Shipment shipInfo) {
        return createUsTrk(em, shipInfo);
    }
    
    
    
    /**
     * Getters and Setters
     * @return 
     */
    
    public static EntityManagerFactory getEmf() {
        return emf;
    }

    public static EntityManager getEm() {
        return em;
    }
    
    
    
    
    
    
}
