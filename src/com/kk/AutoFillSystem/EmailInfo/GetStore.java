package com.kk.AutoFillSystem.EmailInfo;

import static com.kk.AutoFillSystem.EmailInfo.GetWalmart.getBody;
import com.kk.AutoFillSystem.utility.Order;
import com.kk.AutoFillSystem.utility.Shipment;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
    protected String emailSender;
    protected String orderSubject;
    protected String shipSubject;
    protected String storeName;
    
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
        
        try {
            Session session = Session.getDefaultInstance(props, null);
            store = session.getStore("imaps");
            store.connect("smtp.gmail.com", email, pwd);
            System.out.println("Connected successfully");
            inbox = store.getFolder("inbox");
            inbox.open(Folder.READ_ONLY);
            
        }
        catch(NoSuchProviderException ex) {
            ex.printStackTrace();
            
        }
        catch(MessagingException ex) {
            ex.printStackTrace();
            
        }

    }
    
    /*
    **Search for both order email and shipment email
    */
    public void searchInfoSince(String fromDate) throws AddressException, ParseException, MessagingException {
        //get searchterm 
        SearchTerm senderTerm = new FromTerm(new InternetAddress(emailSender));
        SimpleDateFormat df1 = new SimpleDateFormat( "MM/dd/yy" );
            
            Date dDate = df1.parse(fromDate);
            SearchTerm dateTerm = new ReceivedDateTerm(ComparisonTerm.GT,dDate);
        
            Message[] messages = inbox.search(new AndTerm(senderTerm, dateTerm)); //construct basic search terms and search
            System.out.println(messages.length);
            for(Message msg: messages) {
                
//                if (msg.getSubject().contains(orderSubject)) {
//                    
//                    String[] body = getBody(msg);
//                    
//                    Document doc = Jsoup.parse(body[1]);
//                    
//                    Order order = extractOrder(doc.text());
//                    order.orderDate = msg.getReceivedDate();
//                    order.storeName = this.storeName;
//                    System.out.println(order.toString());
//                    orders.add(order);
//                    
//                }
                
                if (msg.getSubject().contains(shipSubject)) {
                    
                    String[] body = getBody(msg);
                    Document doc = Jsoup.parse(body[1]);
                    
                    
                    Shipment shipment = extractShipment(doc.text());
                    
                    //for toysrus , the shipment email order number is in subject. 
                    if(this.storeName.equals("Toysrus")) {
                        String subject = msg.getSubject();
                        Pattern orderNum = Pattern.compile("Order # ([0-9]+)");
                        Matcher m = orderNum.matcher(subject);
                        if(m.find()) {
                            shipment.orderNum = m.group(1);
                        }
                    }
                    
                    shipment.shipDate = msg.getReceivedDate(); 
                    shipments.add(shipment);
                    
                }
            }
        
    }
    
    
    
    
    
    public abstract Order extractOrder(String text);
    public abstract Shipment extractShipment(String text);
    
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
    
    
    
}
