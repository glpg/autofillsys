/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Operations;

import com.kk.AutoFillSystem.Database.Entities.Products;
import com.kk.AutoFillSystem.Database.Services.ProductService;
import static com.kk.AutoFillSystem.utility.LoggingAspect.addMessageWithDate;
import static com.kk.AutoFillSystem.utility.Tools.readMapFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author Yi
 */
public class ProductOp {
    //import products from file
    public static void importProducts(EntityManager em, ArrayList<Products> prods){
        
        // Create new product
        em.getTransaction().begin();
        
        ProductService productService = new ProductService(em, Products.class);
        
        for(Products prod : prods) {
            TypedQuery<Products> query = em.createNamedQuery("Products.findByProdNum", Products.class).setParameter("prodNum", prod.getProdNum());
            List<Products> results = query.getResultList();
            if (results == null || results.size() == 0) {
                productService.create(prod);
                System.out.println("Product : " + prod.getProdNum() +" is created.");
            }
            else System.out.println("Product : " + prod.getProdNum() +" was already created.");
            
        }
        
        
        em.getTransaction().commit();
        
        
    }
    
    public static Products findProduct(EntityManager em, String desc) {
        String fileName = "C:\\Users\\" + System.getProperty("user.name") + 
                    "\\Documents\\AutoFillSystem\\itemnames.txt";
        Map<String, String> nameToNum = readMapFile(fileName);
        
        //lego prod number could be 5 or 7 digits
        Pattern prodNumP = Pattern.compile("(\\d{7}|\\d{5})");
        Matcher m1 = prodNumP.matcher(desc);
        String prodNum = "00000";
        if (m1.find()) {
            prodNum = m1.group(1);
        } else {
            if (nameToNum.get(desc) != null) {
                prodNum = nameToNum.get(desc);
            } 
        }
        
        TypedQuery<Products> query = em.createNamedQuery("Products.findByProdNum", Products.class).setParameter("prodNum", prodNum);
        List<Products> results = query.getResultList();
        
        
        if (results != null && results.size() > 0) {
            if (prodNum.equals("00000")) {
                addMessageWithDate(desc + " does not have product number, info is saved as unknown. please revise db.");
            }
            return results.get(0);
        }
        else {
            addMessageWithDate("Product number: " + prodNum + " is not in db ,please add it first, orderline is not saved in db, please add it manually.");
            return null;
        }
        
    }
    
    
    public static void createNewProduct(EntityManager em, Products prod) {
        em.getTransaction().begin();
        //persist new intl trk
        em.persist(prod);
        addMessageWithDate("New poroduct : " + prod.getProdNum()+ " is created.");
        
        em.getTransaction().commit();    
    }
    
    
}
