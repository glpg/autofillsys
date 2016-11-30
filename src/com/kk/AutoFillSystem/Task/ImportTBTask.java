/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Task;

import com.kk.AutoFillSystem.DataCenter.DataController;
import com.kk.AutoFillSystem.utility.Order;
import com.kk.AutoFillSystem.utility.Product;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.concurrent.Task;

/**
 *
 * @author Yi
 */
public class ImportTBTask extends Task{
    
    private File orderFile;
    private File detailFile;
    private DataController dt;
    
    public ImportTBTask(File orderFile, File detailFile){
        this.orderFile = orderFile;
        this.detailFile = detailFile;
        this.dt = DataController.getInstance();
    }
    

    @Override
    protected Object call() throws Exception {
        
        //initialize data storage
        Map<String, Order> data = new HashMap();
        
        //read in order files
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(orderFile), "GB2312"));      

        

        String line;
        //10 status, 17 time, 0 orderNum
        updateMessage("Reading in order file...");
        while ((line = in.readLine()) != null) {

            String[] fields = line.replaceAll("\"", "").split(",");

            Order tmp = new Order();

            String tran_num = fields[0].replace("=", "");
            String status = fields[10];
            String date = fields[17];

            if (status.contains("等待买家付款") || status.contains("交易关闭")) {
                continue;
            }

            if (!fields[19].contains("乐高")) {
                continue;
            }

            tmp.orderNum = tran_num;

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date orderDate = (Date) formatter.parse(date.split("\\s")[0]);
            tmp.orderDate = orderDate;
            data.put(tran_num, tmp);

        }        
        in.close();
        
        //reading in details file
        
        BufferedReader inDetails = new BufferedReader(new InputStreamReader(new FileInputStream(detailFile), "GB2312"));

        String lineD;

        Pattern prodNumP = Pattern.compile("(\\d{7}|\\d{5})");

        //0 transnum, 3 quantity, 1 title 5 property
        updateMessage("Reading in detail file...");
        while ((lineD = inDetails.readLine()) != null) {

            
            String[] fields = lineD.replaceAll("\"", "").split(",");
            String tran_num = fields[0].replace("=", "");

            Matcher m1 = prodNumP.matcher(fields[5]);
            String prodNum = "00000";

            if (fields[3].contains("购买数量")) {
                continue;
            }
            int quantity = Integer.parseInt(fields[3]);
            if (m1.find()) {
                prodNum = m1.group(1);

            } else {
                m1 = prodNumP.matcher(fields[1]);
                if (m1.find()) {
                    prodNum = m1.group(1);
                }

            }

            //if the item is not lego, skip and continue to next record
            if (prodNum.equals("00000")) {
                
                continue;
            }

            Order tmp = data.get(tran_num);
            if (tmp == null) {
                continue;
            }

            //Product prd = new Product(prodNum, quantity);    
            if (tmp.products == null) {
                tmp.products = new ArrayList();
            }

            boolean found = false;
            for (Product prd : tmp.products) {
                if (prodNum.equals(prd.name)) {
                    prd.count += quantity;
                    
                    found = true;
                    break;
                }

            }

            if (!found) {
                tmp.products.add(new Product(prodNum, quantity));
                
            }

        }

        inDetails.close();
        
        //start to update db
        updateMessage("Updating database ...");
        int count = data.size();
        int i = 0; 
        for(Order info : data.values()) {
            
            dt.createTransaction(info);
            i++;
            updateProgress(i, count);
        }

    
        return null;

    }

    @Override
    protected void succeeded() {
        super.succeeded();
        updateMessage("Done successfully !");
        updateProgress(100, 100);
                
        
    }
    
}
