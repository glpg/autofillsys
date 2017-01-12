/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import static com.kk.AutoFillSystem.AutoFillSystem.primaryStage;
import com.kk.AutoFillSystem.Database.Entities.Products;
import com.kk.AutoFillSystem.utility.LegoAttrib;
import com.kk.AutoFillSystem.utility.LoggingAspect;
import static com.kk.AutoFillSystem.utility.LoggingAspect.supportFilePath;
import com.kk.AutoFillSystem.utility.PriceEstimation;
import static com.kk.AutoFillSystem.utility.Tools.createLegoInfo;
import static com.kk.AutoFillSystem.utility.Tools.showAlert;
import static com.kk.AutoFillSystem.utility.Tools.copyToClipboard;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * FXML Controller class
 *
 * @author Yi
 */
public class ViewSetWindowController implements Initializable {
    
    private MainWindowController mainWindow;
    private ObservableList<LegoAttrib> data; 
    private Map<String, ArrayList<String>> sets;
    private String apiKey = "VQDb-QFVh-NM7r";
    private String baseUrl = "http://brickset.com/api/v2.asmx/getSets?apiKey=mykey&"
            + "userHash=&setNumber=prdnum&query=&theme=&subtheme=&year=&owned=&wanted=&orderby=&pagesize=&pagenumber=&username=";
    
    private String imageUrl;
    
    
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
    @FXML
    private TextField textFieldSetNum;
    @FXML
    private TextField textFieldPrice;
    @FXML
    private TextField textFieldTBPrice;
    @FXML
    private Button btnCalculateCost;
    @FXML
    private Label labelCost;
   
    
    public ViewSetWindowController(){
        try {
            sets = new HashMap();
//        InputStream input = AutoFillSystem.class.getResourceAsStream("Resources/Sets.xml");
//        String result = new BufferedReader(new InputStreamReader(input))
//                .lines().collect(Collectors.joining("\n"));
//        Document localDoc = Jsoup.parse(result);
            
            //load local file
            String dir = supportFilePath();
            File setFile = new File(dir + "Sets.xml");
            Document localDoc =Jsoup.parse(setFile, "utf-8");
            Elements items = localDoc.select("ITEM");
            for(Element item : items) {
                String key = item.select("ITEMID").first().text().split("-")[0];
                ArrayList<String> value = new ArrayList();
                value.add(item.select("ITEMWEIGHT").text());
                value.add(item.select("ITEMDIMX").text());
                value.add(item.select("ITEMDIMY").text());
                value.add(item.select("ITEMDIMZ").text());
                sets.put(key, value);
            }
            ArrayList<String> values = new ArrayList();
            for(int i = 0; i < 13; i++) {
                values.add("");
            }
            data = createLegoInfo(values);
        } catch (IOException ex) {
            ex.printStackTrace();
            LoggingAspect.addException(ex);
        }
        
        
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
        
        legoList.sort(String::compareToIgnoreCase);
        
        listViewSet.setItems(legoList);
        
        //set up listview double click event
        listViewSet.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent click) {

                if (click.getClickCount() == 2) {
                    
                    //Use ListView's getSelected Item
                    String selection = listViewSet.getSelectionModel()
                            .getSelectedItem();
                    //change textfield
                    textFieldSetNum.setText(selection);
                    fetchInfo(selection);
                    
                }
            }
        });
        
        //populate table
        setUpTable();
        
        //set up btns
        btnCalculateCost.setOnAction(e->calculate());
      
        //set up input 
        //set up enter event for search text
        textFieldSetNum.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                int index = 0;
                int size = listViewSet.getItems().size();
                String prodNum = textFieldSetNum.getText().trim();
                //highlight in listview if exist
                for(; index < size; index ++) {
                    if (listViewSet.getItems().get(index).contains(prodNum)) {
                        listViewSet.scrollTo(index);
                        listViewSet.getSelectionModel().select(index);
                        break;
                    }
                }
                // if not found ,then clear selection
                if (index == size) listViewSet.getSelectionModel().clearSelection();
                
                fetchInfo(prodNum);
                
            }
                
        });
        //set up context menu for image view
        ContextMenu contextMenu = new ContextMenu();
        MenuItem save = new MenuItem("Save to file");
        contextMenu.getItems().addAll(save);
        save.setOnAction(e->saveImage());
       
        imageViewSet.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isSecondaryButtonDown()) {
                    contextMenu.show(imageViewSet, event.getScreenX(), event.getScreenY());
                }
            }
        });
        
        //tableview column width;
        attrib.prefWidthProperty().bind(tableViewSet.widthProperty().multiply(0.35));
        value.prefWidthProperty().bind(tableViewSet.widthProperty().multiply(0.65));
    }    
    
    
    
    private void calculate(){
        double weight;
        //get weight
        try {
            weight = Double.parseDouble(data.get(11).getValue());
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Weight :" , "No valid weight available for estimation !", Alert.AlertType.ERROR);
            return;
        }
        
        double usdPrice; 
        try {
            usdPrice = Double.parseDouble(textFieldPrice.getText());
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Price :" , "No valid price available for estimation !", Alert.AlertType.ERROR);
            return;
        }
        
        PriceEstimation pe = PriceEstimation.getInstance();
       
        double result = pe.calculate(usdPrice, weight);
        DecimalFormat twoDForm = new DecimalFormat("#.00");
        String percentage = "";
        //check if tbprice is provided
        if (textFieldTBPrice.getText() != null && !textFieldTBPrice.getText().isEmpty()) {
            
            try {
                double tbPrice = Double.parseDouble(textFieldTBPrice.getText());
                double perc = tbPrice  / result;
                percentage = " " + twoDForm.format(perc);
                
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid Price :", "No valid TB price available for estimation !", Alert.AlertType.ERROR);
                return;
            }
        }
        
        
        
        
        labelCost.setText("" + twoDForm.format(result) + " " + percentage);
        
    }
    
    private void saveImage() {
        
        if (imageUrl == null || imageUrl.isEmpty()) {
            
            showAlert("Warning", "No Image :" , "No image to save !", Alert.AlertType.WARNING);
            return;
        }
        try {
            //get image
            
            URL url = new URL(imageUrl);
            String[] parts = imageUrl.split("/");
            //get filename and extension
            String filename = parts[parts.length - 1].trim();
            
            String[] exts = filename.split("\\.");
            
            String extension = exts[exts.length -1].trim();
            
            InputStream in = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            //set filter
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image", "*." + extension);
            fileChooser.getExtensionFilters().add(extFilter);
            //set initial name
            fileChooser.setInitialFileName(filename);

            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                FileOutputStream fileOuputStream = new FileOutputStream(file);
                fileOuputStream.write(response);
                fileOuputStream.close();
            }
        } catch (MalformedURLException ex) {
            LoggingAspect.addException(ex);
        } catch (IOException ex) {
            LoggingAspect.addException(ex);
        }
        
    }
    
    //fetch info using brickset api
    private void fetchInfo(String prodNum) {
        try {
            String queryUrl = baseUrl.replace("mykey", apiKey).replace("prdnum", prodNum+"-1");
            
            Document doc = Jsoup.connect(queryUrl).get();
            
            if (doc.select("number").first() == null) return;
            
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
            data.get(9).setValue(ukprice);
            String euprice = doc.select("EURetailPrice").first().text();
            data.get(10).setValue(euprice);
            
            
            //now get dimensions and weight
            ArrayList<String> value = sets.get(setNum);
            if (value != null) {
                data.get(11).setValue(value.get(0));
                String dx = value.get(1);
                String dy = value.get(2);
                String dz = value.get(3);
                
                
                
                if (dx != null && !dx.isEmpty()) {
                    String dimension = dx +" x " + dy + " x " + dz;
                    data.get(12).setValue(dimension);
                }
                
                else {
                    data.get(12).setValue("");
                }
                
            }
            
           
            
            //tableview refresh
            tableViewSet.getColumns().get(0).setVisible(false);
            tableViewSet.getColumns().get(0).setVisible(true);
            
            //set image 
            imageUrl = doc.select("imageURL").first().text();
            Platform.runLater(() -> imageViewSet.setImage(new Image(imageUrl)));
           
            
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
        
        
        tableViewSet.setRowFactory(new Callback<TableView<LegoAttrib>, TableRow<LegoAttrib>>() {
            @Override
            public TableRow<LegoAttrib> call(TableView<LegoAttrib> tableView) {
                final TableRow<LegoAttrib> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {

                        TablePosition pos = tableViewSet.getSelectionModel().getSelectedCells().get(0);
                        int rowIndex = pos.getRow();
                        LegoAttrib item = tableViewSet.getItems().get(rowIndex);
                        TableColumn col = pos.getTableColumn();
                        String data = col.getCellObservableValue(item).getValue().toString();
                        copyToClipboard(data);

                    }

                });

                return row;
            }
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
