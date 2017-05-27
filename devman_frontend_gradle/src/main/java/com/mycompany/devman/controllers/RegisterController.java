package com.mycompany.devman.controllers;

import com.mycompany.devman.repositories.UserRepository;
import com.mycompany.devman.domain.AccountType;
import com.mycompany.devman.domain.User;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kuba3
 */
public class RegisterController implements Initializable {
    
    @FXML
    Button registerButton;
    
    @FXML
    TextField name;
    
    @FXML
    TextField lastName;
    
    @FXML
    TextField email;
    
    @FXML
    TextField login;
    
    @FXML
    PasswordField password;
    
    @FXML
    PasswordField repeatPassword;
    
    @FXML
    ChoiceBox manager;
    
    @FXML
    TextField pesel;
    
    private void close() {
        Stage window = (Stage) registerButton.getScene().getWindow();
        window.close();
    }

    /**
     * Concludes developer registation and closes the password window after the
     * user confirms reading the message
     */
    public void OnRegisterButtonClick() throws Exception {
        User user = makeNewUser();

        try {
            if(!password.getText().equals(repeatPassword.getText())) {
                throw new Exception("Hasła się nie zgadzają!");
            }
            UserRepository.addUserToDatabase(user);
        } catch (Exception ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Rejestracja nieudana");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            return;
        }
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText("Rejestracja udana!");
        alert.setContentText("Twoje konto będzie aktywne, gdy zatwierdzi je menadżer.");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                close();
            }
        });
    }

    private User makeNewUser() {
        User user = new User();
        user.setLogin(login.getText());
        user.setPassword(password.getText());
        user.setName(name.getText());
        user.setLastName(lastName.getText());
        user.setEmail(email.getText());
        user.setManager((User) manager.getSelectionModel().getSelectedItem());
        user.setPesel(pesel.getText());
        user.setAccountType(AccountType.EMPLOYEE);
        user.setUserState(User.userState.INACTIVE);
        return user;
    }

    /**
     * Closes the Password recovery window
     */
    public void onCancelButtonClick() {
        close();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            UserRepository.findManagers().forEach(manager.getItems()::add);
        } catch (Exception e) {
            e.printStackTrace();
        }
        manager.getSelectionModel().selectFirst();
    }
    
}
