/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Services;

import com.kk.AutoFillSystem.Database.Entities.Orders;
import java.util.Collection;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;
/**
 *
 * @author Yi
 */
public class OrderService extends Service<Orders> {

    public OrderService(EntityManager em, Class<Orders> classType) {
        super(em, classType);
    }
    
}
