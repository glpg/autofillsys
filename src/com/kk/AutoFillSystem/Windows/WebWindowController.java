/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.AutoFillSystem;
import com.kk.AutoFillSystem.utility.JoinRecord;
import static com.kk.AutoFillSystem.utility.Tools.closeWindow;
import static com.kk.AutoFillSystem.utility.Tools.showAlert;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
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
    
    
    @FXML
    private WebView webView;
    @FXML
    private TextField textFieldUrl;
    @FXML
    private Button buttonExtractZZ;
    @FXML
    private Button buttonCancel;
    @FXML
    private Button buttonBack;
    @FXML
    private Button buttonForward; 
    @FXML
    private ImageView imageView;

    /**
     * Initializes the controller class.
     */
    
    
  
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        buttonExtractZZ.setOnAction(e->{extractZZ();});
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
            ustrkNums.add(line.split("\\s+")[1]);
           
        }
        
        Set<String> ustrkset = new HashSet();
        for(JoinRecord record : mainWindow.getTableRows()) {
            ustrkset.add(record.getUsTrkNum());
            
        }
        
        
        
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
                showAlert("Failed", "Update Error :" , "ZZ tracking " + intlTrkNum + " already existed !", AlertType.WARNING);
            
    
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
   
}
