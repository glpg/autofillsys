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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
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
 * FXML Controller class
 *
 * @author Yi
 */
public class WebWindowController implements Initializable {
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
            buttonZZ.setText("ExtractZZ");
            buttonZZ.setOnAction(e->{extractZZ();});
            labelCnt.setVisible(false);
        }
        else {
            //if upload mode, then change the button text, and initialize uploads list
            buttonZZ.setText("UploadZZ");
            buttonZZ.setOnAction(e->{uploadZZ();});
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
                new ChangeListener<State>() {
                    public void changed(ObservableValue ov, State oldState, State newState) {
                        if (newState == State.SUCCEEDED) {
                            imageView.setVisible(false);

                        }

                    }
                });
        
        textFieldUrl.setText("https://www.uszcn.com");
        webView.getEngine().load("https://www.uszcn.com");
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
    
    
    private void uploadZZ() {
        
        if (index >= uploads.size()) {
            showAlert("Warning", "All uploaded :" , "You uploaded all records, total " + index + " !", AlertType.WARNING);
            return;
        }
        
        
        WebEngine webEngine = webView.getEngine();
        //set parameters
        
        JoinRecord currentRecord = uploads.get(index);
        
        Ustrkings ustrk = currentRecord.getUsTrk();
        
        String trkNum = ustrk.getTrkingNum();
        
        int carrierIndex ; 
        int carrier = ustrk.getCarrierId().getId();
        switch (carrier) {
            case 1 : { //ups
                carrierIndex = 1;
                break;
            }
            case 2 : {//fedex
                carrierIndex = 4;
                break;
            }
            case 3 : {//usps
                carrierIndex = 3;
                break;
                
            }
            case 9 : { //ontrac
                carrierIndex = 8;
                break;
            }
            default : {
                carrierIndex = 9;
            }
        }
        
        int count = 0; 
        for(Trklines temp : ustrk.getTrklinesCollection()) {
            count += temp.getQuantity();
        }
        
        String setTrkNum =  "document.getElementById('inbound_express_tracking_number').value='" + trkNum + "';";
        
        String setName = "document.getElementById('inbound_name').value = \"儿童塑料无电机拼图玩具\";";
        String setDesc = "document.getElementsByClassName('textbox item_name')[0].value = \"儿童塑料无电机拼图玩具\";";
        
        String setBrand = "document.getElementsByClassName('textbox item_brand')[0].value = \"lego\";";
        
        String setQuantity = "document.getElementsByClassName('textbox item_amount')[0].value = '"+ count + "';";
        String setUnitPrice = "document.getElementsByClassName('textbox item_unit_price')[0].value = 15;";
        
        //carrier
        String shipcarrier = "sele = document.getElementsByClassName('select express_select')[0];";
        String chooseCarrier = "sele.value = \"" + carrierIndex + "\"; ";
        
        
        
        //category
        String mainCategory = "sele = document.getElementsByClassName('select parent_category')[0];" ;
        String chooseCategory =  "sele.value = 13; ";
        String trigger = "sele.dispatchEvent(new Event('change'))";
        
        //subcategory
        String subCategory = "sele = document.getElementsByClassName('select category')[0];" ;
        String chooseSubcategory =  "sele.value = 125; ";
        
        //size 
        String size ="sele = document.getElementsByClassName('select item_model_select')[0];";
        String chooseSize = "sele.value =\"30厘米\"; ";
        
        
        webEngine.executeScript(setTrkNum);
        //set carrier
        webEngine.executeScript(shipcarrier);
        webEngine.executeScript(chooseCarrier);
        webEngine.executeScript(trigger);
        
        webEngine.executeScript(setName);
        webEngine.executeScript(setDesc);
        webEngine.executeScript(setBrand);
        webEngine.executeScript(setQuantity);
        webEngine.executeScript(setUnitPrice);
        //set parent category
        webEngine.executeScript(mainCategory);
        webEngine.executeScript(chooseCategory);
        webEngine.executeScript(trigger);
        //set sub category
        webEngine.executeScript(subCategory);
        webEngine.executeScript(chooseSubcategory);
        webEngine.executeScript(trigger);
        //set size 
        webEngine.executeScript(size);
        webEngine.executeScript(chooseSize);
        webEngine.executeScript(trigger);
        index ++; 
        
        labelCnt.setText(index + " / " + uploads.size());
    }
    
    
    private void extractZZ(){
        WebEngine webEngine = webView.getEngine();
        String html =(String)(webEngine.executeScript("document.documentElement.outerHTML"));
        
        //parse html
        Document doc = Jsoup.parse(html);
        
        Elements values = doc.select(".value");
        
        //get intl trkNum and weight and fee
        String intlTrkNum = values.get(1).text();
        int weight = 0;
        double fee = 0.0;
        Pattern doubleP = Pattern.compile("([0-9.]+)");
        Matcher mWeight = doubleP.matcher(values.get(5).text());
        Matcher mFee = doubleP.matcher(values.get(6).text());
        if(mWeight.find()) {
            weight = (int)(Double.parseDouble(mWeight.group(1)) * 1000.0);
        }
        if(mFee.find()) {
            fee = Double.parseDouble(mFee.group(1));
        }
        
        //get us trking
        ArrayList<String> ustrkNums = new ArrayList();
        Element ustrkTable = doc.select("tbody").get(1);
        
        Elements ustrks = ustrkTable.select("tr");
        
        for(Element ustrk : ustrks){
            
            String line = ustrk.select("td").first().text().trim();
            ustrkNums.add(line.split("\\s+")[1].trim());
           
        }
        
//        Set<String> ustrkset = new HashSet();
//        for(JoinRecord record : mainWindow.getTableRows()) {
//            ustrkset.add(record.getUsTrkNum());
//            
//        }
//        
//        
        
        StringBuilder sb = new StringBuilder();
        sb.append("Intl trk: ").append(intlTrkNum).append("\n").append("Weight(g) :").append(weight).
                append("\n").append("Fee : ").append(fee).append("\n").append("Us trkings : \n");
        
        for(String ustrk: ustrkNums) {
            sb.append(ustrk).append("\n ");
            
        }
        
        
        //dialog
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Check Info Dialog");
        alert.setHeaderText("Please Inspect Retreived Info :");
        alert.setContentText(sb.toString());
        
        ButtonType buttonConfirm = new ButtonType("Confirm");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonConfirm, buttonTypeCancel);

        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonConfirm) {
            if (mainWindow.addIntlTrk(ustrkNums, intlTrkNum, weight, fee, "ZZ")) {
                showAlert("Success", "Update Finished :" , "ZZ tracking " + intlTrkNum + " is updated successfully !", AlertType.INFORMATION);
            }
            else
                showAlert("Warning", "Update Warning :" , "ZZ tracking " + intlTrkNum + " already existed or could not correlate with us trking !", AlertType.WARNING);
            
    
        } 
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
