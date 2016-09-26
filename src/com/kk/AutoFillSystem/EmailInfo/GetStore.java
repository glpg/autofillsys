package com.kk.AutoFillSystem.EmailInfo;

import com.kk.AutoFillSystem.utility.Order;
import com.kk.AutoFillSystem.utility.Shipment;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Yi
 */
public abstract class GetStore {
    //parameters for email access
    protected String email;
    protected String pwd;
    protected Folder inbox;
    protected Store store;
    
    //parameters for order specific info
    //protected String emailSender;
    protected String orderSubject;
    protected String shipSubject;
    protected String storeName;
    protected ArrayList<String> emailSenders;
    
    protected ArrayList<Order> orders;
    protected ArrayList<Shipment> shipments;
    
    public GetStore(){
        orders = new ArrayList();
        shipments = new ArrayList();
    }
    
    /*
    **connect to gmail
    ** set up inbox and store here
    */
    public void connectGmail() throws NoSuchProviderException, MessagingException {
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        
        
        Session session = Session.getDefaultInstance(props, null);
        store = session.getStore("imaps");
        store.connect("smtp.gmail.com", email, pwd);
        System.out.println("Connected successfully");
        inbox = store.getFolder("inbox");
        inbox.open(Folder.READ_ONLY);

        
        

    }
    

    
    //step by step operations
    
    public Message[] getMessagesSinceDate(String fromDate) throws MessagingException, ParseException {

        ArrayList<Message> msgs = new ArrayList();
        for (String email : emailSenders) {
            Collections.addAll(msgs, getEmails(fromDate, email));
        }
        return msgs.toArray(new Message[msgs.size()]);
        
        
    }
    
    private Message[] getEmails(String fromDate, String emailSender) throws MessagingException, ParseException {
        //get searchterm 
        SearchTerm senderTerm = new FromTerm(new InternetAddress(emailSender));
        SimpleDateFormat df1 = new SimpleDateFormat("MM/dd/yy");

        Date dDate = df1.parse(fromDate);
        SearchTerm dateTerm = new ReceivedDateTerm(ComparisonTerm.GT, dDate);

        Message[] messages = inbox.search(new AndTerm(dateTerm, senderTerm)); //construct basic search terms and search
        
        return messages;
        
    }
    
    
    protected abstract Order extractOrder(String text);
    public abstract Order extractOrder(Message email);
    protected abstract Shipment extractShipment(String text);
    public abstract ArrayList<Shipment> extractShipments(Message email);
    
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Folder getInbox() {
        return inbox;
    }

    public void setInbox(Folder inbox) {
        this.inbox = inbox;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public ArrayList<Shipment> getShipments() {
        return shipments;
    }

    public void setShipments(ArrayList<Shipment> shipments) {
        this.shipments = shipments;
    }

    public String getOrderSubject() {
        return orderSubject;
    }

    public void setOrderSubject(String orderSubject) {
        this.orderSubject = orderSubject;
    }

    public String getShipSubject() {
        return shipSubject;
    }

    public void setShipSubject(String shipSubject) {
        this.shipSubject = shipSubject;
    }



    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public ArrayList<String> getEmailSenders() {
        return emailSenders;
    }

    public void setEmailSenders(ArrayList<String> emailSenders) {
        this.emailSenders = emailSenders;
    }
    
    
    
    
}
