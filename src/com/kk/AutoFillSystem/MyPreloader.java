package com.kk.AutoFillSystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javafx.application.Preloader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.JOptionPane;

public class MyPreloader extends Preloader {
    
    Stage stage;
    
    private Scene createPreloaderScene() {
        //loading mf
        Manifest mf = new Manifest();
        Attributes atts;
        String s = "MANIFEST.MF";
        
        String version = "1.0.0";
                
        InputStream inputStream = AutoFillSystem.class.getResourceAsStream(s);
        try {
            mf.read(inputStream);
            atts = mf.getMainAttributes();
            
            version = atts.getValue("version");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        
        Label lblVersion = new Label("Version  "+ version);
        lblVersion.setPadding(new Insets(0, 0, 5, 0));
        Label title = new Label("Loading, please wait...");
        title.setTextAlignment(TextAlignment.CENTER);
        title.setPadding(new Insets(0, 0, 5, 0));
        
        VBox root = new VBox(title);
        root.getChildren().add(lblVersion);
        root.setAlignment(Pos.BOTTOM_CENTER);
    
        String image =  getClass().getResource("splash.jpg").toExternalForm();
        root.setStyle("-fx-background-image: url('" + image + "'); " +
           "-fx-background-position: center center; " +
           "-fx-background-repeat: stretch;");

        Scene scene =  new Scene(root, 450, 350);    
        
        return scene;
    }
    
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setScene(createPreloaderScene());  
        stage.initStyle(StageStyle.TRANSPARENT);
        
        stage.show();
    }
    
   
    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {
        if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
            stage.hide();
        }
    }    


    

   
}
