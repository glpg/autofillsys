/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Services;

import com.kk.AutoFillSystem.Database.Entities.Ustocntrkings;
import javax.persistence.EntityManager;

/**
 *
 * @author Yi
 */
public class IntltrackingService extends Service {

    public IntltrackingService(EntityManager em, Class<Ustocntrkings> classType) {
        super(em, classType);
    }
    
    
}
