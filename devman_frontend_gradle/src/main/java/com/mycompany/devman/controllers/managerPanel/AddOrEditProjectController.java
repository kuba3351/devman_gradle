/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.devman.controllers.managerPanel;

import com.mycompany.devman.repositories.ProjectRepository;
import com.mycompany.devman.domain.Project;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jakub
 */
public class AddOrEditProjectController extends Observable implements Initializable {

    @FXML
    private Button cancel;
    
    @FXML
    private TextField name;
    
    @FXML
    private DatePicker startDate;
    
    @FXML
    private DatePicker endDate;
    
    private ManagerPanelController controller;

    private Project project;
    
    public AddOrEditProjectController(ManagerPanelController controller) {
        this.controller = controller;
    }

    public AddOrEditProjectController(ManagerPanelController controller, Project project) {
        this.controller = controller;
        this.project = project;
    }


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if(project != null) {
            name.setText(project.getName());
            startDate.setValue(project.getStartDate());
            endDate.setValue(project.getEndDate());
        }
    }    
    
    private void close() {
        Stage window = (Stage) cancel.getScene().getWindow();
        window.close();
    }
    
    public void onOkButtonClick() throws Exception {
        boolean newProject = false;
        if(project == null) {
            project = new Project();
            newProject = true;
        }
        project.setName(name.getText());
        project.setStartDate(startDate.valueProperty().get());
        project.setEndDate(endDate.valueProperty().get());
        try {
            if(!newProject) {
                ProjectRepository.updateProject(project);
            }
            else {
                ProjectRepository.addProject(project);
            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Błąd dodawania projektu!");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            return;
        }
        if(newProject) {
            controller.addProject(project);
        }
        else {
            controller.updateProject(project);
        }
        setChanged();
        notifyObservers();
        close();
    }
    
    public void onCancelButtonClick() {
        close();
    }
}
