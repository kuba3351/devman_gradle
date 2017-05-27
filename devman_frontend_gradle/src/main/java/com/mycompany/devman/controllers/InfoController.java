package com.mycompany.devman.controllers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jakub
 */
public class InfoController implements Initializable {

    @FXML
    Button ok;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    private void close() {
        Stage window = (Stage) ok.getScene().getWindow();
        window.close();
    }
    
    public void okClicked() {
        close();
    }
    
}
