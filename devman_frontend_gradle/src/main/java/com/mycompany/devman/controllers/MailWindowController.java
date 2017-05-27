package com.mycompany.devman.controllers;

import com.mycompany.devman.BackendSetup;
import com.mycompany.devman.domain.MailConfig;
import com.mycompany.devman.repositories.MailConfigRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by jakub on 15.05.17.
 */
public class MailWindowController implements Initializable {

    @FXML
    private TextField host;

    @FXML
    private TextField port;

    @FXML
    private CheckBox starttls;

    @FXML
    private CheckBox auth;

    @FXML
    private TextField login;

    @FXML
    private PasswordField password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void onSkipButtonClick() throws Exception {
        MailConfig mailConfig = new MailConfig();
        mailConfig.setMailConfigSkipped(true);
        MailConfigRepository.setMailConfig(mailConfig);
        Stage window = (Stage)port.getScene().getWindow();
        window.close();
    }

    public void onSaveButtonClick() {
        MailConfig mailConfig = new MailConfig();
        mailConfig.setSmtpHost(host.getText());
        mailConfig.setSmtpPort(port.getText());
        mailConfig.setSmtpSecured(starttls.isSelected());
        mailConfig.setSmtpAuthorization(auth.isSelected());
        mailConfig.setSmtpLogin(login.getText());
        mailConfig.setSmtpPassword(password.getText());
        mailConfig.setMailConfigSkipped(false);
        BackendSetup.setupMailSession(mailConfig);
        try {
            Message message = new MimeMessage(BackendSetup.getMailSession());
            message.setFrom(new InternetAddress("devmanmailer@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(mailConfig.getSmtpLogin()));
            message.setSubject("Wiadomość testowa");
            message.setText("To jest test konfiguracji wysyłania wiadomości programu devman2017.");

            Transport.send(message);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Błąd konfiguracji SMTP");
            alert.setContentText("Wystąpił problem z tą konfiguracją serwera SMTP!");
            alert.showAndWait();
            return;
        }
        try {
            MailConfigRepository.setMailConfig(mailConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja!");
        alert.setHeaderText("Konfiguracja SMTP zapisana!");
        alert.setContentText("Konfiguracja SMTP została zapisana, i będzie używana przez program w przyszłości.!");
        alert.showAndWait();
        Stage window = (Stage)port.getScene().getWindow();
        window.close();
    }


}
