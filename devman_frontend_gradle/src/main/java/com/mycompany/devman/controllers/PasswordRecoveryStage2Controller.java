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
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kuba3
 */
public class PasswordRecoveryStage2Controller implements Initializable {

    @FXML
    private Button finishButton;
    
    @FXML
    private PasswordField newPassword;
    
    private User currentUser;
    
    private String password;
    
    public PasswordRecoveryStage2Controller(User user, String newPassword) {
        this.currentUser = user;
        this.password = newPassword;
    }
    
    private void close() {
        Stage window = (Stage) finishButton.getScene().getWindow();
        window.close();
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    /**
     *  Concludes password recovery and closes the password window after the user confirms reading the message
     */
    public void onFinishButtonClick() {
        if(!newPassword.getText().equals(password)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Błąd resetowania hasła!");
            alert.setContentText("To nie jest hasło przysłane na skrzynkę e-mail.");
            alert.showAndWait();
            return;
        }
        
        currentUser.setPassword(password);
        try {
            UserRepository.updateUser(currentUser);
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Błąd resetowania hasła!");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText("Przypominanie hasła.");
        alert.setContentText("Podane hasło stanie się twoim hasłem tymczasowym. Możesz je zmienić po zalogowaniu.");
        alert.showAndWait().ifPresent(rs -> {
        if (rs == ButtonType.OK) {
            close();
        }
      });
    }
    /**
     *  Returns to the first stage of password recovery in case the user made a mistake
     *  IO Exception may occur if files /fxml/PasswordRecoveryStage1.fxml 
     *  or /styles/main/passwordrecoverystage1.css are missing
     */
    public void onBackButtonClick() throws IOException {
        Stage window = (Stage)finishButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/PasswordRecoveryStage1.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/passwordrecoverystage1.css");
        
        window.setScene(scene);
    }
    /**
     * Closes the Password recovery window
     */
    public void onCancelButtonClick() {
        close();
    }
}