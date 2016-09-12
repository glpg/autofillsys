/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Operations;

import com.kk.AutoFillSystem.Database.Entities.Stores;
import static com.kk.AutoFillSystem.utility.LoggingAspect.addMessageWithDate;
import javax.persistence.EntityManager;

/**
 *
 * @author Yi
 */
public class StoreOp {
    public static void createNewStore(EntityManager em, Stores store) {
        em.getTransaction().begin();
        //persist new intl trk
        em.persist(store);
        addMessageWithDate("New store : " + store.getName()+ " is created.");
        
        em.getTransaction().commit();    
    }
    
}
