/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Operations;

import com.kk.AutoFillSystem.Database.Entities.Addresses;
import static com.kk.AutoFillSystem.utility.LoggingAspect.addMessageWithDate;
import javax.persistence.EntityManager;

/**
 *
 * @author Yi
 */
public class AddressOp {
    public static void createNewAddress(EntityManager em, Addresses addr) {
        em.getTransaction().begin();
        //persist new intl trk
        em.persist(addr);
        addMessageWithDate("New address : " + addr.getName()+ " is created.");
        
        em.getTransaction().commit();    
    }
    
}
