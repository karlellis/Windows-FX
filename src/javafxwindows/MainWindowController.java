/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxwindows;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 * FXML Controller class
 *
 * @author elli
 */
public class MainWindowController implements Initializable {

    @FXML
    public BorderPane masterPane;
    @FXML
    public Button videoButton;
    @FXML
    public Button imageButton;
    @FXML
    public Button snapButton;
    @FXML
    public Button closeButton;
    @FXML
    public AnchorPane centerPane;
    @FXML
    public ToggleButton captureToggle;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
