/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Services;

import com.kk.AutoFillSystem.Database.Entities.Cntrkings;
import javax.persistence.EntityManager;

/**
 *
 * @author Yi
 */
public class CntrackingService extends Service {

    public CntrackingService(EntityManager em, Class<Cntrkings> classType) {
        super(em, classType);
    }
    
}
