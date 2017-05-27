package com.mycompany.devman.controllers;

import com.mycompany.devman.repositories.MailConfigRepository;
import com.mycompany.devman.repositories.UserRepository;
import com.mycompany.devman.controllers.employeePanel.EmployeePanelController;
import com.mycompany.devman.controllers.managerPanel.ManagerPanelController;
import com.mycompany.devman.domain.AccountType;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kuba3
 */
public class LoginController implements Initializable {

    @FXML
    TextField login;

    @FXML
    PasswordField password;

    /**
     * Creates a window used for a new developer to register into the system. IO
     * Exception may occur if files /fxml/Register.fxml or /styles/main/register.css
     * are missing
     */
    public void onRegisterButtonClick() throws IOException {
        Stage registerWindow = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Register.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/register.css");

        registerWindow.setTitle("DevMan - Rejestracja");
        registerWindow.setResizable(false);
        registerWindow.setScene(scene);
        registerWindow.setX(20);
        registerWindow.setY(20);
        registerWindow.show();
    }

    private void close() {
        Stage window = (Stage) login.getScene().getWindow();
        window.close();
    }

    /**
     * Creates a window used for logging into the application. IO Exception may
     * occur if files /fxml/ManagerPanel.fxml, /styles/managerPanel/managerpanel.css
     * /fxml/EmployeePanel.fxml or /styles/employeePanel/employeepanel.css are missing
     */
    public void onLoginButtonClick() throws IOException {
        User user = null;
        try {
            user = UserRepository.findByLoginAndPassword(login.getText(), password.getText());
            close();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Błąd logowania");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            return;
        }

        if (user.getAccountType().equals(AccountType.MANAGER)) {
            showManagerPanel(user);
        }

        if (user.getAccountType().equals(AccountType.EMPLOYEE)) {
            showEmployeePanel(user);
        }
    }

    private void showEmployeePanel(User user) throws IOException {
        Stage employeeWindow = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/employeePanel/EmployeePanel.fxml"));
        EmployeePanelController controller = new EmployeePanelController(user);
        loader.setController(controller);
        Parent root2 = (Parent)loader.load();

        Scene scene2 = new Scene(root2);
        scene2.getStylesheets().add("/styles/employeePanel/employeepanel.css");

        employeeWindow.setTitle("DevMan - Panel Pracownika");
        employeeWindow.setResizable(false);
        employeeWindow.setScene(scene2);
        employeeWindow.setX(300);
        employeeWindow.setY(300);
        employeeWindow.show();
    }

    private void showManagerPanel(User user) throws IOException {
        Stage managerWindow = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/managerPanel/ManagerPanel.fxml"));
        ManagerPanelController controller = new ManagerPanelController(user);
        loader.setController(controller);
        Parent root = (Parent)loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/managerPanel/managerpanel.css");

        managerWindow.setTitle("DevMan - Panel Menadżera");
        managerWindow.setResizable(false);
        managerWindow.setScene(scene);
        managerWindow.setX(20);
        managerWindow.setY(20);
        managerWindow.show();
    }

    /**
     * Creates a window used for recovering forgotten passwords. IO Exception
     * may occur if files /fxml/PasswordRecoveryStage1.fxml or
     * /styles/main/passwordrecoverystage1.css are missing
     */
    public void onRecoveryButtonClick() throws IOException {
        try {
            if(MailConfigRepository.getMailCOnfig().getMailConfigSkipped()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd!");
                alert.setHeaderText("Funkcja niedostępna!");
                alert.setContentText("Funkcjonalność resetowania hasła została wyłączona, ponieważ administrator nie skonfigurował danych serwera SMTP!");
                alert.showAndWait();
                return;
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Funkcja niedostępna!");
            alert.setContentText("Wystąpił problem z konfiguracją serwera SMTP!");
            alert.showAndWait();
            return;
        }
        Stage recoveryWindow = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/PasswordRecoveryStage1.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/passwordrecoverystage1.css");

        recoveryWindow.setTitle("DevMan - Przypominanie hasła");
        recoveryWindow.setResizable(false);
        recoveryWindow.setScene(scene);
        recoveryWindow.setX(20);
        recoveryWindow.setY(20);
        recoveryWindow.show();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
