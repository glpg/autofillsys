package com.kk.AutoFillSystem;

import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MyPreloader extends Preloader {
    
    Stage stage;
    
    private Scene createPreloaderScene() {
        Label title = new Label("Loading, please wait...");
        title.setTextAlignment(TextAlignment.CENTER);
        title.setPadding(new Insets(0, 0, 5, 0));
        
        VBox root = new VBox(title);
        root.setAlignment(Pos.BOTTOM_CENTER);
//        root.setBackground(new Background(new BackgroundFill(
//            Color.LIGHTGREY, CornerRadii.EMPTY,Insets.EMPTY)));
        
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
