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
    public Stage primaryStage;
    public Scene mainScene;
    
    private EntityManagerFactory emf;
    private EntityManager em;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    
    @Override
    public void start( Stage st1 ){
	//init database
        initDB();
        
	
	initMainWindow();
	primaryStage = st1; 	
	primaryStage.show();
		
    }
    
    private void initDB(){
        emf = Persistence.createEntityManagerFactory("AutoFillSystemPU");
        em = emf.createEntityManager();
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

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

   
    
}
