package com.kk.AutoFillSystem.Windows;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
/**
 *
 * @author Yi
 */
public class ShowWarningWindowController implements Initializable {
    private MainWindowController mainWindow;
    
    
    @FXML
    private TextArea textAreaLog;
    
    @FXML
    private Button btnClear;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //display msg
        textAreaLog.setText(mainWindow.getWarningMsgs().toString());
        btnClear.setOnAction(e->clearMsg());
        
    }    
    
    private void clearMsg(){
        textAreaLog.clear();
        mainWindow.clearWarningMsg();
    }

    public void setMainWindow(MainWindowController mainWindow) {
        this.mainWindow = mainWindow;
    }
    
    
}
