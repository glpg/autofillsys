/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.DataCenter;

import com.kk.AutoFillSystem.Database.Entities.Addresses;
import com.kk.AutoFillSystem.Database.Entities.Carriers;
import com.kk.AutoFillSystem.Database.Entities.Cntrkings;
import com.kk.AutoFillSystem.Database.Entities.Orderlines;
import com.kk.AutoFillSystem.Database.Entities.Orders;
import com.kk.AutoFillSystem.Database.Entities.Products;
import com.kk.AutoFillSystem.Database.Entities.Stores;
import com.kk.AutoFillSystem.Database.Entities.Transactions;
import com.kk.AutoFillSystem.Database.Entities.Trklines;
import com.kk.AutoFillSystem.Database.Entities.Ustocntrkings;
import com.kk.AutoFillSystem.Database.Entities.Ustrkings;
import static com.kk.AutoFillSystem.Database.Operations.AddressOp.createNewAddress;
import static com.kk.AutoFillSystem.Database.Operations.CarrierOp.createNewCarrier;
import static com.kk.AutoFillSystem.Database.Operations.OrderOp.createNewOrder;
import static com.kk.AutoFillSystem.Database.Operations.OrderOp.createNewOrderFromEntity;
import static com.kk.AutoFillSystem.Database.Operations.OrderOp.createNewOrderline;
import static com.kk.AutoFillSystem.Database.Operations.OrderOp.delOrderline;
import static com.kk.AutoFillSystem.Database.Operations.ProductOp.createNewProduct;
import static com.kk.AutoFillSystem.Database.Operations.StoreOp.createNewStore;
import static com.kk.AutoFillSystem.Database.Operations.TrackOp.createCnTrk;
import static com.kk.AutoFillSystem.Database.Operations.TrackOp.createIntlTrk;
import static com.kk.AutoFillSystem.Database.Operations.TrackOp.createNewTrkline;
import static com.kk.AutoFillSystem.Database.Operations.TrackOp.createNewUsTrkFromEntity;
import static com.kk.AutoFillSystem.Database.Operations.TrackOp.createUsTrk;
import static com.kk.AutoFillSystem.Database.Operations.TrackOp.delTrkline;
import static com.kk.AutoFillSystem.Database.Operations.TrackOp.findIntlTrking;
import static com.kk.AutoFillSystem.Database.Operations.TrackOp.relateUsandIntlTrk;
import static com.kk.AutoFillSystem.Database.Operations.TrackOp.updateCnDelivery;
import static com.kk.AutoFillSystem.Database.Operations.TrackOp.updateUsTrk;
import static com.kk.AutoFillSystem.Database.Operations.TransactionOp.createNewTransaction;
import static com.kk.AutoFillSystem.Database.Operations.TransactionOp.createNewTransactionFromEntity;
import com.kk.AutoFillSystem.Database.Services.AddressService;
import com.kk.AutoFillSystem.Database.Services.CarrierService;
import com.kk.AutoFillSystem.Database.Services.CntrackingService;
import com.kk.AutoFillSystem.Database.Services.IntltrackingService;
import com.kk.AutoFillSystem.Database.Services.OrderService;
import com.kk.AutoFillSystem.Database.Services.ProductService;
import com.kk.AutoFillSystem.Database.Services.StoreService;
import com.kk.AutoFillSystem.Database.Services.UStrackingService;
import com.kk.AutoFillSystem.utility.Order;
import com.kk.AutoFillSystem.utility.Shipment;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author Yi
 */
public class DataController {
    
    
    //singleton instance 
    private static DataController instance = null;
    
    //db accessing
    private static EntityManagerFactory emf ;
    private static EntityManager em ;
    
    private List<Orders> orders;
    
    public DataController() {
        //initialize db connection
        emf = Persistence.createEntityManagerFactory("AutoFillSystemPU");
        em = emf.createEntityManager();
        
    }
    
   
    
    public static DataController getInstance() {
      if(instance == null) {
         instance = new DataController();
      }
      return instance;
    }

    
    /**
     * Get db data
     **/
    
    public List<Orders> getOrders() {
        OrderService os = new OrderService(em, Orders.class);
        return os.findAll();
    }
    
    public List<Orders> getLastNOrders(int count, int offset) {
        String sql = "SELECT o FROM Orders o ORDER BY o.id DESC" ;
        Query q = em.createQuery(sql, Orders.class);
        q.setFirstResult(offset); 
        q.setMaxResults(count);
        List<Orders> result = q.getResultList();
        return result;   
    }
    
    public List<Ustrkings> getUstrkings() {
        UStrackingService us = new UStrackingService(em, Ustrkings.class);
        return us.findAll();
    }
    
    public List<Stores> getStores() {
        StoreService ss = new StoreService(em, Stores.class);
        return ss.findAll();
    }
    
    public List<Carriers> getCarriers() {
        CarrierService cs = new CarrierService(em, Carriers.class);
        return cs.findAll();
    }
    
    public List<Addresses> getAddresses() {
        AddressService as = new AddressService(em, Addresses.class);
        return as.findAll();
    }
    
    public Addresses getAddress(int i) {
        AddressService as = new AddressService(em, Addresses.class);
        return (Addresses) as.find(i);
    }
    
    public List<Products> getProducts() {
        ProductService ps = new ProductService(em, Products.class);
        return ps.findAll();
    }
    

    public List<Ustocntrkings> getIntlTrkings() {
        IntltrackingService is = new IntltrackingService(em, Ustocntrkings.class);
        return is.findAll();
    }
    
    public List<Ustocntrkings> getIntlTrking(String trkNum) {
        return findIntlTrking(em, trkNum);
    }
    
    public List<Cntrkings> getCnTrkings() {
        CntrackingService cs = new CntrackingService(em, Cntrkings.class);
        return cs.findAll();
    }
    
    
    
    
    
    public Orders createOrder(Order orderInfo) {
        return createNewOrder(em, orderInfo);
    }
    
   public boolean createOrder(Orders order) {
       return createNewOrderFromEntity(em, order);
   }
    
    public boolean createOrderline(Orderlines orderline) {
        return createNewOrderline(em, orderline);
    }
    
    public void deleteOrderline(Orderlines orderline){
        
        delOrderline(em, orderline);
    }
    
    public Transactions createTransaction(Transactions transaction) {
        return createNewTransactionFromEntity(em, transaction);
    }
    
    public Transactions createTransaction(Order info) {
        return createNewTransaction(em, info);
    }
    
    public Ustrkings createUsTrking(Shipment shipInfo) {
        return createUsTrk(em, shipInfo);
    }
    
    public boolean createUsTrking(Ustrkings ustrk) {
        return createNewUsTrkFromEntity(em, ustrk);
    }
    
    public Ustrkings updateUsTrking(Ustrkings ustrk) {
        return updateUsTrk(em, ustrk);
    }
    
    public void createTrkline(Trklines trkline) {
        createNewTrkline(em, trkline);
    }
    
    public void deleteTrkline(Trklines trkline){
        delTrkline(em,trkline);
    }
    
    public void createIntlTrking(Ustocntrkings intlTrk, Ustrkings usTrk) {
        createIntlTrk(em,intlTrk, usTrk);
    }
    
    public void createUsAndIntlRelation(Ustocntrkings intlTrk, Ustrkings usTrk) {
        relateUsandIntlTrk(em, intlTrk, usTrk);
    }
    
    public void createIntlTrking(Ustocntrkings intlTrk) {
        createIntlTrk(em,intlTrk);
    }
    
    public void createCnTrking(Cntrkings cnTrk) {
        createCnTrk(em, cnTrk);
    }
    
    public void createProduct(Products prd) {
        createNewProduct(em, prd);
    }
    
    public void createStore(Stores store) {
        createNewStore(em, store);
    }
    
    public void createCarrier(Carriers carrier) {
        createNewCarrier(em, carrier);
    }
    
    public void createAddress(Addresses addr) {
        createNewAddress(em, addr);
    }
    
    public void updateDelivery(Cntrkings cntrk) {
        updateCnDelivery(em, cntrk);
    }
    
    public void clearCache(){
        em.getEntityManagerFactory().getCache().evictAll();
        em.close();
        em = emf.createEntityManager();
    }

    /**
     * Getters and Setters
     * @return 
     */
    
    public static EntityManagerFactory getEmf() {
        return emf;
    }

    public static EntityManager getEm() {
        return em;
    }
    
    
    public Object querySoldSum(String prdNum) {
        String sql = "SELECT SUM(t.quantity) FROM Transactionlines t , Products p WHERE t.prodId.id = p.id AND p.prodNum = :prodNum" ;
        Query q = em.createQuery(sql);
        q.setParameter("prodNum", prdNum);
        Object result = (Object) q.getSingleResult();
        return result;

    }
    
    public Object queryBoughtSum(String prdNum) {
        String sql = "SELECT SUM(t.quantity) FROM Orderlines t , Products p WHERE t.productId.id = p.id AND p.prodNum = :prodNum" ;
        Query q = em.createQuery(sql);
        q.setParameter("prodNum", prdNum);
        Object result = (Object) q.getSingleResult();
        return result;

    }
    
    public Object queryShippedSum(String prdNum) {
        String sql = "SELECT SUM(t.quantity) FROM Trklines t , Products p WHERE t.productId.id = p.id AND p.prodNum = :prodNum" ;
        Query q = em.createQuery(sql);
        q.setParameter("prodNum", prdNum);
        Object result = (Object) q.getSingleResult();
        return result;

    }

}
