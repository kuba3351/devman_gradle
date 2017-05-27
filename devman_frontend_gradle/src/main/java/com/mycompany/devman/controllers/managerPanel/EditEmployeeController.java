
package com.mycompany.devman.controllers.managerPanel;

import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

import com.mycompany.devman.domain.User;
import com.mycompany.devman.repositories.UserRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jakub
 */
public class EditEmployeeController extends Observable implements Initializable {

    @FXML
    private Button cancel;

    @FXML
    private TextField name;

    @FXML
    private TextField lastName;

    @FXML
    private TextField email;

    private User user;

    private ManagerPanelController controller;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name.setText(user.getName());
        lastName.setText(user.getLastName());
        email.setText(user.getEmail());
    }

    public EditEmployeeController(User user, ManagerPanelController controller) {
        this.user = user;
        this.controller = controller;
    }
    
    private void close() {
        Stage window = (Stage) cancel.getScene().getWindow();
        window.close();
    }
    
    public void onCancelButtonClick() {
        close();
    }
    
    public void onOkButtonClick() throws Exception {
        user.setName(name.getText());
        user.setLastName(lastName.getText());
        user.setEmail(email.getText());
        UserRepository.updateUser(user);
        controller.updateEmployee(user);
        close();
    }   
}