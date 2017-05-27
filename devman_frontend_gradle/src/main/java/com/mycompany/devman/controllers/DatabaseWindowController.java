package com.mycompany.devman.controllers;

import com.mycompany.devman.BackendSetup;
import com.mycompany.devman.DatabaseSetup;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by jakub on 15.05.17.
 */
public class DatabaseWindowController implements Initializable {

    @FXML
    private TextField host;

    @FXML
    private TextField port;

    @FXML
    private TextField databaseName;

    @FXML
    private TextField databaseLogin;

    @FXML
    private TextField databasePassword;

    private DatabaseSetup setup;

    public DatabaseWindowController() {
        setup = new DatabaseSetup();
    }

    public void onCloseButtonClick() {
        Stage window = (Stage)host.getScene().getWindow();
        window.close();
    }

    public void onConnectButtonClick() {
        setup.setHost(host.getText());
        setup.setPort(port.getText());
        setup.setName(databaseName.getText());
        setup.setLogin(databaseLogin.getText());
        setup.setPassword(databasePassword.getText());
        try {
            BackendSetup.setupDatabaseConnection(setup);
            BackendSetup.getDatabaseSession();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Błą połączenia!");
            alert.setContentText("Nie udało się połączyć z tą bazą.");
            alert.showAndWait();
            return;
        }
        String system = System.getProperty("os.name");
        Map<String, String> env = System.getenv();
        File file = null;
        if (system.contains("linux") || system.contains("Linux")) {
            file = new File(env.get("HOME") + "/.devman/config.ini");
            System.out.println("Detected Linux OS!");
        } else if (system.contains("Win") || system.contains("win")) {
            file = new File(env.get("PUBLIC") + "\\AppData\\Roaming\\devman\\config.ini");
            System.out.println("Detected Windows OS!");
        }
        Wini ini;
        try {
            file.createNewFile();
            ini = new Wini(file);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Błą pliku konfiguracyjnego!");
            alert.setContentText("Nie udało się utworzyć pliku konfiguracyjnego.");
            alert.showAndWait();
            return;
        }
        ini.put("Database", "database.host", setup.getHost());
        ini.put("Database", "database.port", setup.getPort());
        ini.put("Database", "database.name", setup.getName());
        ini.put("Database", "database.username", setup.getLogin());
        ini.put("Database", "database.password", setup.getPassword());
        try {
            ini.store();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Błą pliku konfiguracyjnego!");
            alert.setContentText("Nie udało się zapisać pliku konfiguracyjnego.");
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja!");
        alert.setHeaderText("Konfiguracja bazy danych zapisana!");
        alert.setContentText("Konfiguracja bazy danych została zapisana, i będzie używana przez program w przyszłości.!");
        alert.showAndWait();
        Stage window = (Stage)host.getScene().getWindow();
        window.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        port.setText("3306");
    }
}
