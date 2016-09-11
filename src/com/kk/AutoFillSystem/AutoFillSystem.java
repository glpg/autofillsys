/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem;

import com.kk.AutoFillSystem.Windows.MainWindowController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Yi
 */
public class AutoFillSystem extends Application {
    public static Stage primaryStage;
    public Scene mainScene;
    
    public static EntityManagerFactory emf = Persistence.createEntityManagerFactory("AutoFillSystemPU");
    public static EntityManager em = emf.createEntityManager();
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    
    @Override
    public void start( Stage st1 ){
	
	primaryStage = st1; 
	initMainWindow();
		
	primaryStage.show();
		
    }
    
    
    
    public void initMainWindow() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AutoFillSystem.class.getResource("Windows/MainWindow.fxml"));
            AnchorPane rootLayout = (AnchorPane) loader.load();

            MainWindowController controller = loader.getController();
            //controller.setMainApp(this);
            mainScene = new Scene(rootLayout);
            primaryStage.setScene(mainScene);
            primaryStage.setTitle("Tracking System 1.0.0");
            

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

   
    
}
