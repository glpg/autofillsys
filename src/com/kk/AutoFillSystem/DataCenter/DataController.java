/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.DataCenter;

import com.kk.AutoFillSystem.Database.Entities.Orders;
import com.kk.AutoFillSystem.Database.Services.OrderService;
import java.util.List;
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
