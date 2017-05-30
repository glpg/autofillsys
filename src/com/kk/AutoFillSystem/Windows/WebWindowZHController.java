/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.AutoFillSystem;
import com.kk.AutoFillSystem.Database.Entities.Trklines;
import com.kk.AutoFillSystem.Database.Entities.Ustrkings;
import com.kk.AutoFillSystem.utility.JoinRecord;
import static com.kk.AutoFillSystem.utility.Tools.closeWindow;
import static com.kk.AutoFillSystem.utility.Tools.showAlert;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Yi
 */
public class WebWindowZHController implements Initializable{
    
    private MainWindowController mainWindow;
    private Mode mode;
    private ArrayList<JoinRecord> uploads; 
    private int index = 0; 
    
    @FXML
    private WebView webView;
    @FXML
    private TextField textFieldUrl;
    @FXML
    private Button buttonZZ;
    @FXML
    private Button buttonCancel;
    @FXML
    private Button buttonBack;
    @FXML
    private Button buttonForward; 
    @FXML
    private ImageView imageView;
    @FXML
    private Label labelCnt;

    /**
     * Initializes the controller class.
     */
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (mode == Mode.EXTRACT) {
            buttonZZ.setText("ExtractZH");
            buttonZZ.setOnAction(e->{extractZH();});
            labelCnt.setVisible(false);
        }
        else {
            //if upload mode, then change the button text, and initialize uploads list
            buttonZZ.setText("UploadZH");
            buttonZZ.setOnAction(e->{uploadZH();});
            labelCnt.setVisible(true);
            labelCnt.setText(index + " / " + uploads.size());
        }
        
        
        textFieldUrl.setOnKeyPressed((e) -> {goToPage(e);});
        buttonCancel.setOnAction(e->{closeWindow(e);});
        
        buttonBack.setOnAction(e->{goBack();});
        buttonForward.setOnAction(e->{goForward();});
        //imageview setup
        imageView.setFitHeight(25);
        imageView.setFitWidth(25);
        Image loading = new Image(AutoFillSystem.class.getResourceAsStream("Resources/loading.gif"));
        imageView.setImage(loading);
        imageView.setVisible(false);
        
        //set up webviewer
        webView.getEngine().getLoadWorker().stateProperty().addListener(
                new ChangeListener<Worker.State>() {
                    public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                        if (newState == Worker.State.SUCCEEDED) {
                            imageView.setVisible(false);

                        }

                    }
                });
        
        textFieldUrl.setText("http://www.zhonghuanus.com/authorization/dologin.action");
        webView.getEngine().load("http://www.zhonghuanus.com/authorization/dologin.action");
        imageView.setVisible(true);
                
    }   
    
    
    public void goBack() {
        WebEngine webEngine = webView.getEngine();
        WebHistory history = webEngine.getHistory();
        ObservableList<WebHistory.Entry> entryList = history.getEntries();
        int currentIndex = history.getCurrentIndex();

        history.go(entryList.size() > 1&& currentIndex > 0? -1: 0);
        
    }
    
    public void goForward() {
        WebEngine webEngine = webView.getEngine();
        WebHistory history = webEngine.getHistory();
        ObservableList<WebHistory.Entry> entryList = history.getEntries();
        int currentIndex = history.getCurrentIndex();

        history.go(entryList.size() > 1 && currentIndex < entryList.size() - 1? 1: 0); 
        
    }
    
    
    private void uploadZH() {
        
        if (index >= uploads.size()) {
            showAlert("Warning", "All uploaded :" , "You uploaded all records, total " + index + " !", Alert.AlertType.WARNING);
            return;
        }
        
        
        WebEngine webEngine = webView.getEngine();
        //set parameters
        
        JoinRecord currentRecord = uploads.get(index);
        
        Ustrkings ustrk = currentRecord.getUsTrk();
        
        String trkNum = ustrk.getTrkingNum();
        
        String carrierName ; 
        int carrier = ustrk.getCarrierId().getId();
        switch (carrier) {
            case 1 : { //ups
                carrierName = "UPS-美国联合包裹";
                break;
            }
            case 2 : {//fedex
                carrierName = "Fedex-联邦快递";
                break;
            }
            case 3 : {//usps
                carrierName = "USPS-美国邮局";
                break;
                
            }
            case 9 : { //ontrac
                carrierName = "ONTRAC";
                break;
            }
            default : {
                carrierName = "其他快递公司";
            }
        }
        
        int count = 0; 
        for(Trklines temp : ustrk.getTrklinesCollection()) {
            count += temp.getQuantity();
        }
        
        String setTrkNum =  "document.getElementById('postalparceladdexpressNo').value='" + trkNum + "';";
        
        String setInsurance = "document.getElementsByName('postalParcel.safeAmount')[0].value = \"0\";";
        
        String setName = "document.getElementById('inbound_name').value = \"儿童塑料无电机拼图玩具\";";
        String setDesc = "document.getElementById('goods').value = \"儿童塑料无电机拼图玩具\";";
        
        String setBrand = "document.getElementsByClassName('item_brand input_search')[0].value = \"lego\";";
        
        String setQuantity = "document.getElementsByClassName('item_amount input_search')[0].value = '"+ count + "';";
        String setUnitPrice = "document.getElementsByClassName('item_unit_price input_search')[0].value = 15;";
        
        //carrier
        String shipcarrier = "sele = document.getElementsByClassName('input_kdusa')[0];";
        String chooseCarrier = "sele.value = '" + carrierName + "';";
        
        
        
        //category
        String mainCategory = "sele = document.getElementsByClassName(' parent_category input_search')[0];" ;
        //String chooseCategory =  "sele.value = 40; ";
        String trigger = "sele.dispatchEvent(new Event('change'))";
        String chooseCategory = "sele.selectedIndex = 3";
        
        //subcategory 
        /*here is a trigger ajax event, js cannot trigger the event, turn around is to manually change the option, then choose it*/
        String subCategory = "sele = document.getElementsByClassName('category input_search')[0];" ;
        //String chooseSubcategory =  "sele.value = 42; ";
        String newOp = "newop = document.createElement(\"option\")";
        String newOpSetAttr = "newop.setAttribute(\"value\",\"42\")";
        String newOpSetText = "newop.text = \"儿童玩具\"";
        String addToMenu = "sele.add(newop)";
        String chooseSubcategory = "sele.selectedIndex = 1";
        
        /* size is not needed for zh 
        String size ="sele = document.getElementsByClassName('select item_model_select')[0];";
        String chooseSize = "sele.value =\"30厘米\"; ";
        
        */
        webEngine.executeScript(setTrkNum);
        //set carrier
        webEngine.executeScript(shipcarrier);
        webEngine.executeScript(chooseCarrier);
        webEngine.executeScript(trigger);
        
        
        
        
        //set insurance
        webEngine.executeScript(setInsurance);
        
        webEngine.executeScript(setName);
        webEngine.executeScript(setDesc);
        webEngine.executeScript(setBrand);
        webEngine.executeScript(setQuantity);
        webEngine.executeScript(setUnitPrice);
        
//        //set parent category
//        webEngine.executeScript(mainCategory);
//        webEngine.executeScript(chooseCategory);
//        
//        //set sub category
//        webEngine.executeScript(subCategory);
//        webEngine.executeScript(newOp);
//        webEngine.executeScript(newOpSetAttr);
//        webEngine.executeScript(newOpSetText);
//        webEngine.executeScript(addToMenu);
//        webEngine.executeScript(chooseSubcategory);
//        //set size 
//        webEngine.executeScript(size);
//        webEngine.executeScript(chooseSize);
//        webEngine.executeScript(trigger);
        index ++; 
        
        labelCnt.setText(index + " / " + uploads.size());
    }
    
    
    private void extractZH(){
        WebEngine webEngine = webView.getEngine();
        String html =(String)(webEngine.executeScript("document.documentElement.outerHTML"));
        
        //parse html
        Document doc = Jsoup.parse(html);
        Element table = doc.select("table.uk-table").first();
        Elements entries = table.select("tbody>tr");
        int count = entries.size();
        int index = 1;
        for(Element entry : entries) {
            Elements tds = entry.select("td");
            String intlTrkNum = tds.get(0).text();
            String ustrkNum = tds.get(1).text();
            int weight = (int)(Double.parseDouble(tds.get(5).text()) * 1000.0);
            double fee = Double.parseDouble(tds.get(6).text());
            
            
            //now start to write the intl trk
            
            ArrayList<String> ustrkNums = new ArrayList();
            ustrkNums.add(ustrkNum);
            int outcome = mainWindow.addIntlTrk(ustrkNums, intlTrkNum, weight, fee, "ZH");
            if (outcome == 0) {
                showAlert("Success", index + " / " + count + " : ", "ZZ tracking " + intlTrkNum + " is updated successfully !", Alert.AlertType.INFORMATION);
            } else {
                if (outcome == 1)
                    showAlert("Warning", index + " / " + count + " : ", "ZZ tracking " + intlTrkNum + " could not correlate with us trking !", Alert.AlertType.WARNING);
                else
                    showAlert("Warning", index + " / " + count + " : ", "ZZ tracking " + intlTrkNum + " already existed !", Alert.AlertType.WARNING);
            }
            
            
            index++;
        }
        
        
//      
    }
    
    
    
    private void goToPage(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            WebEngine webEngine = webView.getEngine();
            webEngine.load(textFieldUrl.getText());
            imageView.setVisible(true);
        }
    }
    
    //getter and setter

    public MainWindowController getMainWindow() {
        return mainWindow;
    }

    public void setMainWindow(MainWindowController mainWindow) {
        this.mainWindow = mainWindow;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public ArrayList<JoinRecord> getUploads() {
        return uploads;
    }

    public void setUploads(ArrayList<JoinRecord> uploads) {
        this.uploads = uploads;
    }
    
    
    
}
