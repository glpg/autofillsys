/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Services;

import com.kk.AutoFillSystem.Database.Entities.Orderlines;
import javax.persistence.EntityManager;

/**
 *
 * @author Yi
 */
public class OrderlineService extends Service {

    public OrderlineService(EntityManager em, Class<Orderlines> classType) {
        super(em, classType);
    }
    
}
