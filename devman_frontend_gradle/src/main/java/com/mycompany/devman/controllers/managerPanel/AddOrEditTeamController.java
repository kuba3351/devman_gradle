
package com.mycompany.devman.controllers.managerPanel;

import com.mycompany.devman.repositories.ProjectRepository;
import com.mycompany.devman.repositories.TeamRepository;
import com.mycompany.devman.domain.Project;
import com.mycompany.devman.domain.Team;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jakub
 */
public class AddOrEditTeamController extends Observable implements Initializable {

    @FXML
    private Button cancel;
    
    @FXML
    private TextField name;
    
    @FXML
    private ChoiceBox<Project> projects;
   
    private ManagerPanelController controller;

    private Team team;
    
    public AddOrEditTeamController(ManagerPanelController controller) {
        this.controller = controller;
    }

    public AddOrEditTeamController(ManagerPanelController controller, Team team) {
        this.controller = controller;
        this.team = team;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            ProjectRepository.findProjectsInProgress().forEach(projects.getItems()::add);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(team == null) {
            projects.getSelectionModel().selectFirst();
        }
        else {
            name.setText(team.getName());
            projects.getSelectionModel().select(team.getProject());
        }
    }    
    
    private void close() {
        Stage window = (Stage) cancel.getScene().getWindow();
        window.close();
    }
    
    public void onCancelButtonClick() {
        close();
    }
    
    public void onAddButtonClick() throws Exception {
        boolean newTeam = false;
        if(team == null) {
            team = new Team();
            newTeam = true;
        }
        team.setName(name.getText());
        team.setProject(projects.getSelectionModel().getSelectedItem());
        try {
            if(newTeam) {
                TeamRepository.addTeam(team);
            }
            else {
                TeamRepository.updateTeam(team);
            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Błąd dodawania drużyny!");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
        if(newTeam) {
            controller.addTeam(team);
        }
        else {
            controller.editTeam(team);
        }
        setChanged();
        notifyObservers();
        close();
    }
}
