/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Services;

import com.kk.AutoFillSystem.Database.Entities.Trklines;
import javax.persistence.EntityManager;

/**
 *
 * @author Yi
 */
public class TrkinglineService extends Service{

    public TrkinglineService(EntityManager em, Class<Trklines> classType) {
        super(em, classType);
    }
    
}
