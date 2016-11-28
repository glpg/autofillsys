/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Operations;

import com.kk.AutoFillSystem.Database.Entities.Products;
import com.kk.AutoFillSystem.Database.Entities.Transactionlines;
import com.kk.AutoFillSystem.Database.Entities.Transactions;
import static com.kk.AutoFillSystem.utility.LoggingAspect.addMessage;
import static com.kk.AutoFillSystem.utility.LoggingAspect.addMessageWithDate;
import com.kk.AutoFillSystem.utility.Order;
import com.kk.AutoFillSystem.utility.Product;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author Yi
 */
public class TransactionOp {
    
    //create new order  and orderlines
    public static Transactions createNewTransaction(EntityManager em, Order info){ 
        
        
        //check if transaction already existed already
        
        TypedQuery<Transactions> transactionQuery = em.createNamedQuery("Transactions.findBySellNum", Transactions.class).setParameter("sellNum", info.orderNum);
        List<Transactions> results = transactionQuery.getResultList();
        //if existed already, skip
        if (results != null && results.size() > 0) {
            addMessage("Transaction " + info.orderNum + " existed already, pass!");            
            return null;
        
        }
        //else create new order
        else {
            em.getTransaction().begin();
            //create new orders
            Transactions trans = new Transactions();
            trans.setSellNum(info.orderNum);
            trans.setSellDate(info.orderDate);
            em.persist(trans);
            
            
            //get translines
            if (info.products != null) {
                for (Product prodInfo : info.products) {

                    TypedQuery<Products> prdQuery = em.createNamedQuery("Products.findByProdNum", Products.class).setParameter("prodNum", prodInfo.name);
                    List<Products> prods = prdQuery.getResultList();

                    if (prods != null && prods.size() >= 1) {
                        Transactionlines transLine = new Transactionlines();
                        //set sellId
                        transLine.setSellId(trans);
                        //set prodId
                        transLine.setProdId(prods.get(0));
                        //set count
                        transLine.setQuantity(prodInfo.count);

                        em.persist(transLine);

                        addMessageWithDate(prodInfo.name + " " + prodInfo.count);

                    } else {
                        addMessageWithDate("Could not find product " + prodInfo.name + "(" + prodInfo.count + ")" + " in database, please add it first.");
                    }

                }

            }
            
            em.getTransaction().commit();
            addMessageWithDate("Transaction : " + info.orderNum +" is created.");
            return trans;

        }
     
    }
    
    
    
    
    public static Transactions createNewTransactionFromEntity(EntityManager em, Transactions transaction){ 
        // Create new todo
        
        if (transaction.getSellNum()== null) return null;
        
      
        //check if order existed already
        
        TypedQuery<Transactions> transactionQuery = em.createNamedQuery("Transactions.findBySellNum", Transactions.class).setParameter("sellNum", transaction.getSellNum());
        List<Transactions> results = transactionQuery.getResultList();
        //if existed already, skip
        if (results != null && results.size() > 0) {
            addMessage("Transaction " + transaction.getSellNum() + " existed already, pass!");            
            return null;
        }
        //else create new order
        else {
            em.getTransaction().begin();
            em.persist(transaction);

            em.getTransaction().commit();
            addMessageWithDate("Transaction : " + transaction.getSellNum() +" is created.");
            return transaction;

        }
    }
}
