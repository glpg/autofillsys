/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import com.kk.AutoFillSystem.Database.Entities.Products;
import com.kk.AutoFillSystem.utility.LegoAttrib;
import com.kk.AutoFillSystem.utility.LoggingAspect;
import static com.kk.AutoFillSystem.utility.Tools.createLegoInfo;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * FXML Controller class
 *
 * @author Yi
 */
public class ViewSetWindowController implements Initializable {
    
    private MainWindowController mainWindow;
    private ObservableList<LegoAttrib> data; 
    private String apiKey = "VQDb-QFVh-NM7r";
    private String baseUrl = "http://brickset.com/api/v2.asmx/getSets?apiKey=mykey&"
            + "userHash=&setNumber=prdnum&query=&theme=&subtheme=&year=&owned=&wanted=&orderby=&pagesize=&pagenumber=&username=";
    
    
    @FXML
    private ListView<String> listViewSet;
    @FXML
    private TableView<LegoAttrib> tableViewSet;
    @FXML
    private ImageView imageViewSet;
    @FXML
    private TableColumn<LegoAttrib, String> attrib;
    @FXML
    private TableColumn<LegoAttrib, String> value;
    
    
    public ViewSetWindowController(){
        //initialize data
        //populate table with empty values
        ArrayList<String> values = new ArrayList();
        for(int i = 0; i < 11; i++) {
            values.add("");
        }
        
        data = createLegoInfo(values);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // populate listview
        ObservableList<String> legoList = FXCollections.observableArrayList();
        for(Products prd: mainWindow.getProducts()) {
            String prodNum = prd.getProdNum();
            if (prodNum.matches("[0-9]+") && !prodNum.equals("00000"))
                legoList.add(prd.getProdNum());
        }
        
        listViewSet.setItems(legoList);
        
        //set up listview double click event
        listViewSet.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent click) {

                if (click.getClickCount() == 2) {
                    //Use ListView's getSelected Item
                    String selection = listViewSet.getSelectionModel()
                            .getSelectedItem();
                    fetchInfo(selection);
                }
            }
        });
        
        //populate table
        setUpTable();
        
        
        
    }    
    
    //fetch info using brickset api
    private void fetchInfo(String prodNum) {
        try {
            String queryUrl = baseUrl.replace("mykey", apiKey).replace("prdnum", prodNum+"-1");
            
            Document doc = Jsoup.connect(queryUrl).get();
            
            String setNum = doc.select("number").first().text();
            data.get(0).setValue(setNum);
            String name = doc.select("name").first().text();
            data.get(1).setValue(name);
            String year= doc.select("year").first().text();
            data.get(2).setValue(year);
            String piece = doc.select("pieces").first().text();
            data.get(3).setValue(piece);
            String minifig = doc.select("minifigs").first().text();
            data.get(4).setValue(minifig);
            String ratings = doc.select("rating").first().text();
            data.get(5).setValue(ratings);
            String release = doc.select("USDateAddedToSAH").first().text();
            data.get(6).setValue(release);
            String retire = doc.select("USDateRemovedFromSAH").first().text();
            data.get(7).setValue(retire);
            String usprice = doc.select("USRetailPrice").first().text();
            data.get(8).setValue(usprice);
            String ukprice = doc.select("UKRetailPrice").first().text();
            data.get(9).setValue(usprice);
            String euprice = doc.select("EURetailPrice").first().text();
            data.get(10).setValue(usprice);
            
            ArrayList<String> values = new ArrayList();
            values.add(setNum);
            values.add(name);
            values.add(year);
            values.add(piece);
            values.add(minifig);
            values.add(ratings);
            values.add(release);
            values.add(retire);
            values.add(usprice);
            values.add(ukprice);
            values.add(euprice);
            
            tableViewSet.setItems(createLegoInfo(values));
            
            
            String imageUrl = doc.select("imageURL").first().text();
            imageViewSet.setImage(new Image(imageUrl));
            
        } catch (IOException ex) {
            LoggingAspect.addException(ex);
        }
        
        
    }
    
    private void setUpTable(){
        
         //set up cellvalue factory
        attrib.setCellValueFactory(new PropertyValueFactory("attribName"));
        value.setCellValueFactory(new PropertyValueFactory("value"));
        
        //format attrib column
        attrib.setCellFactory(column -> {
            return new TableCell<LegoAttrib, String>(){

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            
                            
                            setText(item);
                            setStyle("-fx-font-weight: bold; ");
                        }
                    }
                };
           
        });
        
        
        tableViewSet.setItems(data);
        
    }

    public MainWindowController getMainWindow() {
        return mainWindow;
    }

    public void setMainWindow(MainWindowController mainWindow) {
        this.mainWindow = mainWindow;
    }
    
    
}
