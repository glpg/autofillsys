/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Services;

import com.kk.AutoFillSystem.Database.Entities.Carriers;
import javax.persistence.EntityManager;

/**
 *
 * @author Yi
 */
public class CarrierService extends Service {

    public CarrierService(EntityManager em, Class<Carriers> classType) {
        super(em, classType);
    }
    
}
