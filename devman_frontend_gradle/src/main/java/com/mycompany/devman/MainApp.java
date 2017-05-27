package com.mycompany.devman;

import com.mycompany.devman.controllers.DatabaseWindowController;
import com.mycompany.devman.controllers.MailWindowController;
import com.mycompany.devman.controllers.ManagerRegisterController;
import com.mycompany.devman.domain.MailConfig;
import com.mycompany.devman.repositories.MailConfigRepository;
import com.mycompany.devman.repositories.UserRepository;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.hibernate.HibernateException;
import org.ini4j.Ini;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;


public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        try {
            String system = System.getProperty("os.name");
            Map<String, String> env = System.getenv();
            File file;
            if(system.contains("linux") || system.contains("Linux")) {
                file = new File(env.get("HOME") + "/.devman/config.ini");
                System.out.println("Detected Linux OS!");
            }
            else if(system.contains("Win") || system.contains("win")) {
                file = new File(env.get("PUBLIC") + "\\AppData\\Roaming\\devman\\config.ini");
                System.out.println("Detected Windows OS!");
            }
            else {
                System.out.println("OS not detected!");
                return;
            }
            if(!file.exists()) {
                Stage recoveryWindow = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/database.fxml"));

                loader.setController(new DatabaseWindowController());
                Parent root = loader.load();
                Scene scene = new Scene(root);
                scene.getStylesheets().add("/styles/main/passwordrecoverystage1.css");

                recoveryWindow.setTitle("DevMan - Konfiguracja");
                recoveryWindow.setResizable(false);
                recoveryWindow.setScene(scene);
                recoveryWindow.setX(20);
                recoveryWindow.setY(20);
                recoveryWindow.showAndWait();
            }
            if(!file.exists()) {
                return;
            }

            Ini config = new Ini(file);
            DatabaseSetup setup = new DatabaseSetup();
            setup.setHost(config.get("Database", "database.host"));
            setup.setName(config.get("Database", "database.name"));
            setup.setPort(config.get("Database", "database.port"));
            setup.setLogin(config.get("Database", "database.username"));
            setup.setPassword(config.get("Database", "database.password"));
            BackendSetup.setupDatabaseConnection(setup);
            BackendSetup.getDatabaseSession();
        }catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Błąd połączenia z bazą danych!");
            alert.setContentText("Sprawdź, czy konfiguracja bazy danych w pliku hibernate.properties jest prawidłowa!");
            e.printStackTrace();
            alert.showAndWait();
            return;
        }
        MailConfig mailConfig = null;
        try {
            mailConfig = MailConfigRepository.getMailCOnfig();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Błąd konfiguracji SMTP!");
            alert.setContentText(e.getMessage());
            e.printStackTrace();
            alert.showAndWait();
            return;
        }
        if(mailConfig == null) {
            Stage recoveryWindow = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mail.fxml"));

            loader.setController(new MailWindowController());
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/styles/main/passwordrecoverystage1.css");

            recoveryWindow.setTitle("DevMan - Konfiguracja");
            recoveryWindow.setResizable(false);
            recoveryWindow.setScene(scene);
            recoveryWindow.setX(20);
            recoveryWindow.setY(20);
            recoveryWindow.showAndWait();
        }
        mailConfig = MailConfigRepository.getMailCOnfig();
        Boolean isMailConfigured = !mailConfig.getMailConfigSkipped();
        if(isMailConfigured) {
            BackendSetup.setupMailSession(mailConfig);
        }
        if(UserRepository.findManagers().isEmpty()) {
            Stage recoveryWindow = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/managerRegister.fxml"));

            loader.setController(new ManagerRegisterController());
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/styles/main/passwordrecoverystage1.css");

            recoveryWindow.setTitle("DevMan - Konfiguracja");
            recoveryWindow.setResizable(false);
            recoveryWindow.setScene(scene);
            recoveryWindow.setX(20);
            recoveryWindow.setY(20);
            recoveryWindow.showAndWait();
        }
        if(UserRepository.findManagers().isEmpty()) {
            return;
        }
        showLoginWindow(stage);
    }

    public void showLoginWindow(Stage stage) throws java.io.IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/login.css");
        stage.setResizable(false);
        stage.setTitle("DevMan - Logowanie");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
