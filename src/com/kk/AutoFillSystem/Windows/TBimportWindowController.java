/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Windows;

import static com.kk.AutoFillSystem.AutoFillSystem.primaryStage;
import com.kk.AutoFillSystem.Task.ImportTBTask;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author Yi
 */
public class TBimportWindowController implements Initializable {
    
    private File orderFile;
    private File detailFile;
    
    
    @FXML
    private Button btnOrderFile;
    @FXML
    private Button btnDetailFile;
    @FXML
    private Button btnImport;
    @FXML
    private TextField txtFieldOrderFile;
    @FXML
    private TextField txtFieldDetailFile;
    @FXML
    private ProgressBar prgBarStatus;
    @FXML
    private Label labelStatus;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //initialize progress
        prgBarStatus.setProgress(0);
        
        btnOrderFile.setOnAction(e->{
            orderFile = chooseFile(e);
            txtFieldOrderFile.setText(orderFile.getAbsolutePath());
        });
        
        btnDetailFile.setOnAction(e->{
            detailFile = chooseFile(e);
            txtFieldDetailFile.setText(detailFile.getAbsolutePath());
        });
        
        btnImport.setOnAction(e->importData());
    }   
    
    
    private File chooseFile(ActionEvent e){
        
     
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(((Button)e.getSource()).getScene().getWindow());
        
        return file;
        
    }
    
    private void importData(){
        
        // Create a Task.
        ImportTBTask importTask = new ImportTBTask(orderFile, detailFile);
        
        //bind status info
        labelStatus.textProperty().bind(importTask.messageProperty());
        prgBarStatus.progressProperty().bind(importTask.progressProperty());
        
        new Thread(importTask).start();
        
    }
    
    
}
