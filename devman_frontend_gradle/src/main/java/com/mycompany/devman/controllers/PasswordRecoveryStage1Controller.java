package com.mycompany.devman.controllers;


import com.mycompany.devman.repositories.UserRepository;
import com.mycompany.devman.domain.User;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.mail.MessagingException;

/**
 * FXML Controller class
 *
 * @author kuba3
 */
public class PasswordRecoveryStage1Controller implements Initializable {
    
    @FXML
    private Button backButton;
    
    @FXML
    private Button nextButton;
    
    @FXML
    private TextField email;
    
    @FXML
    private TextField pesel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        backButton.setDisable(true);
    }    
    
    /**
     *  Creates a window used for 2nd stage of password recovery.
     *  IO Exception may occur if files /fxml/PasswordRecoveryStage2.fxml 
     *  or /styles/main/passwordrecoverystage2.css are missing
     */
    public void onNextButtonClick() throws IOException {
        User user = null;
        try {
            user = UserRepository.findByEmailAndPesel(email.getText(), pesel.getText());
        } catch (Exception ex) {
            showErrorMessage(ex);
            return;
        }
        
        String newPassword;
        try {
            newPassword = UserRepository.resetPassword(user);
        } catch (Exception ex) {
            showErrorMessage(ex);
            return;
        }
        
        Stage window = (Stage)nextButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PasswordRecoveryStage2.fxml"));
        PasswordRecoveryStage2Controller vontroller = new PasswordRecoveryStage2Controller(user, newPassword);
        loader.setController(vontroller);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/passwordrecoverystage2.css");
        
        window.setScene(scene);
    }

    private void showErrorMessage(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd!");
        alert.setHeaderText("Błąd resetowania hasła!");
        alert.setContentText(ex.getMessage());
        alert.showAndWait();
    }

    private void close() {
        Stage window = (Stage) backButton.getScene().getWindow();
        window.close();
    }
    
    /**
     * Closes the Password recovery window
     */
    public void onCancelButtonClick() {
        close();
    }
}
