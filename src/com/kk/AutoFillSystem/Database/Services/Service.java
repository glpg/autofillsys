/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Services;
import java.util.List;
import javax.persistence.EntityManager;
/**
 *
 * @author Yi
 * create, update, read, remove functions
 */
public abstract class Service<T> {
    protected EntityManager em;
    protected final Class<T> classType;
    
    public Service(EntityManager em, Class<T> classType) {
        this.em = em;
        this.classType = classType;
    }
    
    public void create(T item){
        em.persist(item);
        
    }
    public boolean remove(int id){
        T item = find(id);
        if (item != null) {
            em.remove(item);
            return true;
        }
        return false;
    }
    
    public T find(int id){
        return em.find(classType, id);
    }
 
    public T update (T item) {
        return em.merge(item);
    }
    
    public List<T> findAll() {
       return em.createQuery("Select t from " + classType.getSimpleName() + " t").getResultList();
    }

}
