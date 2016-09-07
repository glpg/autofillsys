/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Yi
 */
public class Tools {
    
    public static int count(final String string, final String substring){
        int count = 0;
        int idx = 0;

        while ((idx = string.indexOf(substring, idx)) != -1)
        {
            idx++;
            count++;
        }

        return count;
    }
    
    
    //read file, return unique set , for lego set numbers
    public static Set<String> readFile(String fileName) {
        BufferedReader br = null;
        
        Set<String> uniqueStr = new HashSet();
        
        //ArrayList<Products> prods = new ArrayList();

        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(fileName));

            while ((sCurrentLine = br.readLine()) != null) {
                uniqueStr.add(sCurrentLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        return uniqueStr;
        
    }

    //read file, name and set to a map
    public static Map<String, String> readMapFile(String fileName) {
        BufferedReader br = null;
        
        Map<String, String> map = new HashMap();
        
        //ArrayList<Products> prods = new ArrayList();

        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(fileName));

            while ((sCurrentLine = br.readLine()) != null) {
                
                String[] parts = sCurrentLine.split("\\|\\|");
                
                map.put(parts[1], parts[0]);
                
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        return map;
        
    }
    
    public static String getWarehouse(String text) {
        //ship to 
        String warehouse = "unknown";
        if (text.contains("ivy KangXue")) {
            warehouse = "ZZ";
        }
       
        if (text.contains("RSJEP HDB")) {
            warehouse = "HDB";
        }
        
        if (text.contains("LTFHN TT")) {
            warehouse = "TT";
        }
        
        return warehouse;
        
    }

}
