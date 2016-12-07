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
        String email = "Kmart Shipping Confirmation Items in order 996069759 have shipped!   Have Questions? VIEW CUSTOMER SERVICE CLOTHING   SHOES   HOME   ELECTRONICS   TOYS   CLEARANCE Great news, Ivy! The items listed below from Order #996069759, placed on Tue, Nov 29, 2016 have shipped! Since you are a valued Shop Your Way® MAX member, your shipping is FREE! TRACK YOUR SHIPMENT: Please note it may take 24 hours for your tracking number to return any information. If your order involves multiple packages, you will receive a separate tracking number for each package. We hope you enjoy your items! Thank you for choosing us, Your Kmart Team Receipt is required for most returns and exchanges. To accommodate holiday returns, select items purchased between 11/1 - 12/24 will have an extended return period: Items which typically have 30-day return period, can be returned through 1/31/17. View our Return Policy for details and exclusions. Order Details view or manage your order SHIPMENT 6215 NE 92nd Dr , C/O OYC639 STATUS: Shipped Shipping Address ivy KangXue 6215 NE 92nd Dr C/O OYC639 PORTLAND, OR 97220 503-894-8090 Order Date Tue, Nov 29, 2016 Salescheck # 78409996095000 Suborder # 1427209186 Shipping Vendor UPS ITEM DETAILS Super Heroes Batman: Scarecrow Harvest of Fear Size: 2.45 x 18.9 x 11.15  Sold by KMART Kmart Return Policy Mfr# 76054 Part# 004W001229460001 KSN: 1229460 UPC: 673419250450 Tracking Number: 1Z03V2W20300020189 PRICE QTY ITEM SUBTOTAL Sale 47.95 Reg. 59.99 1 47.95 Member Number Ending in 1621 You earned $10.38 in points(10376 points) on this order. Your earned points will be available after your order has been delivered or picked up.see points details Points displayed are estimates only. Points earned vary based on purchase(s), offers, cancelations or subsequent modifications to your order. Expiration dates vary. Visit www.shopyourway.com for details. Order Summary Regular Price Subtotal 59.99 Savings Before Cart   Sale Price Savings 12.04 Merchandise Subtotal 47.95 Shop Your Way Points Savings   Points Redeemed 10.39 Pre-Tax Subtotal 37.56 Shipping & Delivery   Shipping 9.99 Shop Your Way MAX Savings 9.99 Total Due 37.56 You saved 22.03 on this order How You Paid   Total Deducted from GiftCard Consumer ending in ************7706 -4.57 Total Deducted from GiftCard Consumer ending in ************8721 -25.00 Total Deducted from GiftCard Consumer ending in ************3300 -7.99 SAVE MORE NEXT TIME YOU SHOP   Kmart Privacy Policy Store Finder   SHOP YOUR WAY Your Account Offers and Rewards About   SUPPORT Contact Us Questions?                 Please add kmart@account.kmart.com to your address book to ensure our emails reach your inbox. Please do not reply to this email. If you have questions or concerns, please visit customer service. © 2016 Sears Brands, LLC, 3333 Beverly Road, Hoffman Estates, IL 60179. All Rights Reserved. [TransKMTShipConfMAXFreeShip]\n" ;
        Pattern track = Pattern.compile("Tracking Number: ([a-zA-Z0-9]+) ");
        Matcher m1 = track.matcher(email);
        if(m1.find()) {
            
            
            System.out.println(m1.group(1));
        }

        
//        Store store;
//        Folder inbox;
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.socketFactory.port", "465");
//        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        props.put("mail.smtp.host", "smtp.gmail.com");
//        props.put("mail.smtp.port", "465");
//
//        try {
//
//            Session session = Session.getDefaultInstance(props, null);
//
//            store = session.getStore("imaps");
//            store.connect("smtp.gmail.com", "gobetterkx@gmail.com", "Bnmrc123");
//            System.out.println("Connected successfully");
//            inbox = store.getFolder("inbox");
//            inbox.open(Folder.READ_ONLY);
//            //search term from target
//            SearchTerm sender = new FromTerm(new InternetAddress("Kmart"));
//            
//            SimpleDateFormat df1 = new SimpleDateFormat( "MM/dd/yy" );
//            String dt="11/30/16";
//            Date dDate = df1.parse(dt);
//            SearchTerm st = new ReceivedDateTerm(ComparisonTerm.GT,dDate);
//        
//            Message[] messages = inbox.search(new AndTerm(sender, st)); //construct basic search terms and search
//            for(Message msg: messages) {
//                if (msg.getSubject().contains("have shipped")) {
//                    String[] body = getBody(msg);
//                    Document doc = Jsoup.parse(body[0]);
//                    String bodyText = doc.text();
//                    System.out.println(bodyText);
//                    
//                    
//                }
//            }
//
//      }
//      catch (Exception ex) {
//          ex.printStackTrace();
//      }
//       
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



