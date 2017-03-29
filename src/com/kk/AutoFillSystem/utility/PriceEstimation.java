/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.utility;

/**
 *
 * @author Yi
 */
public class PriceEstimation {
    
    private static PriceEstimation instance = null;
    
    
    private int extraBoxWeight = 350; 
    private double discount = 0.92;
    private double currencyRatio =  7.0; 
    private double initalShipFee = 29.75; 
    private double extraShipFee = 5.52; 
    private double vipDiscount = 1.00; 
    private int count = 5; 
    
    
    public PriceEstimation(){
        
    }

   /* Static 'instance' method */
   public static PriceEstimation getInstance( ) {
      if(instance == null) {
         instance = new PriceEstimation();
      }
      return instance;
   }

   public double calculate(double usdPrice, double weight){
       double cnyCost = usdPrice * discount * currencyRatio;
       int extraCnt = (int)(weight*count + extraBoxWeight - 500) / 100 + 1;
       double shipCost = (49 + 7* extraCnt)*vipDiscount / count; 
       return cnyCost + shipCost;
   }
   
   //getters and setters
   

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getCurrencyRatio() {
        return currencyRatio;
    }

    public void setCurrencyRatio(double currencyRatio) {
        this.currencyRatio = currencyRatio;
    }

    public double getInitalShipFee() {
        return initalShipFee;
    }

    public void setInitalShipFee(double initalShipFee) {
        this.initalShipFee = initalShipFee;
    }

    public double getExtraShipFee() {
        return extraShipFee;
    }

    public void setExtraShipFee(double extraShipFee) {
        this.extraShipFee = extraShipFee;
    }

    public double getVipDiscount() {
        return vipDiscount;
    }

    public void setVipDiscount(double vipDiscount) {
        this.vipDiscount = vipDiscount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    

    public int getExtraBoxWeight() {
        return extraBoxWeight;
    }

    public void setExtraBoxWeight(int extraBoxWeight) {
        this.extraBoxWeight = extraBoxWeight;
    }

   
    
}
