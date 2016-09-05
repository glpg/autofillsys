/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.EmailInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Yi
 * target easily blocked, stopped now 7-24
 */
public class GetTarget {
    public static void main(String[] args) {
        
        Store store;
        Folder inbox;
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");

        try {

            Session session = Session.getDefaultInstance(props, null);

            store = session.getStore("imaps");
            store.connect("smtp.gmail.com", "riceballcat@gmail.com", "bnmrc123");
            System.out.println("Connected successfully");
            inbox = store.getFolder("inbox");
            inbox.open(Folder.READ_ONLY);
            //search term from target
            SearchTerm sender = new FromTerm(new InternetAddress("orders@services.target.com"));
            
            SimpleDateFormat df1 = new SimpleDateFormat( "MM/dd/yy" );
            String dt="06/01/16";
            Date dDate = df1.parse(dt);
            SearchTerm st = new ReceivedDateTerm(ComparisonTerm.GT,dDate);
        
            Message[] messages = inbox.search(new AndTerm(sender, st)); //construct basic search terms and search
            for(Message msg: messages) {
                if (msg.getSubject().contains("We received your Target.com order")) {
                    String[] body = getBody(msg);
                    Document doc = Jsoup.parse(body[1]);
                    String bodyText = doc.text();
                    System.out.println("#######################");
                    extractOrderConfirmationInfo(bodyText);
                    System.out.println("#######################");
                    
                }
            }

      }
      catch (Exception ex) {
          ex.printStackTrace();
      }
       
   }
    
    
    public static void extractOrderConfirmationInfo(String text) {
        Pattern orderNum = Pattern.compile("order #([0-9]+) placed on (.*) have been delivered");
        Matcher m = orderNum.matcher(text);
        if(m.find()) {
            System.out.println(m.group(1));
            System.out.println(m.group(2));
        }
        
        
        
        Pattern trackNum = Pattern.compile("carrier: (.*)tracking number: (.*) item\\(s\\) in your order");
        Matcher m1 = trackNum.matcher(text);
        if(m1.find()) {
            System.out.println(m1.group(1));
            System.out.println(m1.group(2));
        }
       
       
        Pattern shipments = Pattern.compile("delivered item\\(s\\) quantity (.*) carrier");
        Matcher m2 =shipments.matcher(text);
        System.out.println("delivered");
        if(m2.find()) {
            String items = m2.group(1);
            //System.out.println(items);
            splitLego(items);
        }
     
        System.out.println("Ordered");
        Pattern orderitems = Pattern.compile("order item\\(s\\) quantity (.*) For assistance");
        Matcher m3 =orderitems.matcher(text);
        if(m3.find()) {
            String items = m3.group(1);
            //System.out.println(items);
            splitLego(items);
        }
    }
    
    
    
    
    
  //get information for delivery confirm email : We just delivered a shipment on your order
  
    public static void extractDeliveryConfirmationInfo(String text) {
        Pattern orderNum = Pattern.compile("\\(Order #([0-9]+)\\) placed on (.*) have been delivered");
        Matcher m = orderNum.matcher(text);
        if(m.find()) {
            System.out.println(m.group(1));
            System.out.println(m.group(2));
        }
        
        
        
        Pattern trackNum = Pattern.compile("carrier: (.*)tracking number: (.*) item\\(s\\) in your order");
        Matcher m1 = trackNum.matcher(text);
        if(m1.find()) {
            System.out.println(m1.group(1));
            System.out.println(m1.group(2));
        }
       
       
        Pattern shipments = Pattern.compile("delivered item\\(s\\) quantity (.*) carrier");
        Matcher m2 =shipments.matcher(text);
        System.out.println("delivered");
        if(m2.find()) {
            String items = m2.group(1);
            //System.out.println(items);
            splitLego(items);
        }
     
        System.out.println("Ordered");
        Pattern orderitems = Pattern.compile("order item\\(s\\) quantity (.*) For assistance");
        Matcher m3 =orderitems.matcher(text);
        if(m3.find()) {
            String items = m3.group(1);
            //System.out.println(items);
            splitLego(items);
        }
    }
    
    public static void splitLego(String items) {
        
        String[] itemSet = items.split("LEGO®");
        Pattern itemP = Pattern.compile("(.*)([0-9]+)$");
        for(String item : itemSet) {
            
            item = item.trim();
            //System.out.println(item);
            Matcher m = itemP.matcher(item);
            if(m.find()) {
                System.out.println("Lego " + m.group(1));
                System.out.println(m.group(2));
            }
        }
        
       
    }
    
    
    
    
    public static String[] getBody(Message msg){
		
		String body[] = new String[2];
		
		try{
			Object content = msg.getContent();
			if (content instanceof String){
				body[0]=(String)content;
				body[1]="";
				
			}
			else if(content instanceof Multipart) {
				Multipart cont = (Multipart) content; 
				for (int i=0; i < cont.getCount(); ++i){
	       		   BodyPart part=cont.getBodyPart(i);
	       		   //extract order number
	       		 if (part.getContentType().contains("TEXT/PLAIN")){
	       			   body[0]=(String)part.getContent();
	       		 }
	       		 if (part.getContentType().contains("TEXT/HTML")){
	       			   body[1]=(String)part.getContent();
	       			   
	       		   }
				}
				
			}
			
		}
		catch(Exception ex){
			ex.printStackTrace();
			
		}

		return body;
		
	           
		
	}
}



