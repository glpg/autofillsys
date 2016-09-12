/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Operations;

import com.kk.AutoFillSystem.Database.Entities.Carriers;
import static com.kk.AutoFillSystem.utility.LoggingAspect.addMessageWithDate;
import javax.persistence.EntityManager;

/**
 *
 * @author Yi
 */
public class CarrierOp {
    public static void createNewCarrier(EntityManager em, Carriers carrier) {
        em.getTransaction().begin();
        //persist new intl trk
        em.persist(carrier);
        addMessageWithDate("New carrier : " + carrier.getName()+ " is created.");
        
        em.getTransaction().commit();    
    }
    
}
