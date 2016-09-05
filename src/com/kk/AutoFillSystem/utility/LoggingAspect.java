
package com.kk.AutoFillSystem.utility;

import static com.sun.javafx.PlatformUtil.isWindows;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * LoggingAspect
 * @author Carlos Igreja
 * @since  Feb 23, 2016
 */
public class LoggingAspect {
    
    public static String supportFilePath(){
        String supportFilePath = "";

        if (isWindows()) {
            supportFilePath = "C:\\Users\\" + System.getProperty("user.name") + 
                    "\\Documents\\AutoFillSystem\\";
        } else {
            supportFilePath = "/Users/" + System.getProperty("user.name") + 
                    "/Library/Application Support/AutoFillSystem/";
        }
        File dir = new File(supportFilePath);
        if (!dir.exists())
            dir.mkdir();
        return supportFilePath;
    }
    
    public static void addMessageWithDate(String str) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd hh:mm:ss a");
        String storeStr = str;
        String dateStr = dateFormat.format(date);

        
        addMessage(dateStr + ": " + storeStr);
    }
    
    
    public static void addMessage(String str) {
        System.out.println(str);
        String fileName = supportFilePath() + "Log.txt";
        File file = new File(fileName);
        try {
            FileWriter fileWriter = new FileWriter(fileName, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            
            bufferedWriter.write(str);
            bufferedWriter.newLine();
            bufferedWriter.close();
            
        } catch (IOException ex) {
            LoggingAspect.addException(ex);
        } catch (Exception ex) {
            LoggingAspect.addException(ex);
        }
    }
    
   
    
    public static void addException(Exception e){
        
        
        addMessageWithDate("An exception was thrown: ");

        StackTraceElement[] elements = e.getStackTrace();
        for (StackTraceElement element : elements) {

            addMessage("Package.Class: " + element.getClassName());
            addMessage("Method: " + element.getMethodName());
            addMessage("Line: " + element.getLineNumber());
            
        }
    }
    
}
