package com.mycompany.devman.controllers.managerPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.mycompany.devman.repositories.UserRepository;
import com.mycompany.devman.domain.Team;
import com.mycompany.devman.domain.User;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jakub
 */
public class EmployeeAssignController extends Observable implements Initializable {

    @FXML
    private Button cancel;

    @FXML
    private ListView allEmployees;
    
    @FXML
    private ListView selectedEmployees;
    
    private Team team;
    
    public EmployeeAssignController(Team team) {
        this.team = team;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            placeEmployeesOnSides();
        } catch (Exception e) {
            e.printStackTrace();
        }

        allEmployees.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectedEmployees.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void placeEmployeesOnSides() throws Exception {
        List<User> users = UserRepository.findEmployees();
        users.forEach(user -> {
            if(user.getTeams().contains(team))
                selectedEmployees.getItems().add(user);
            else
                allEmployees.getItems().add(user);
        });
    }

    private void close() {
        Stage window = (Stage) cancel.getScene().getWindow();
        window.close();
    }
    
    public void onOkButtonClick() {
        updateSelectedEmployees();
        updateNonSelectedEmployees();
        setChanged();
        notifyObservers();
        close();
    }

    private void updateNonSelectedEmployees() {
        for(Object o : selectedEmployees.getItems()) {
            User user = (User)o;
            if(!user.getTeams().contains(team)) {
                user.getTeams().add(team);
                try {
                    UserRepository.updateUser(user);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void updateSelectedEmployees() {
        for(Object o : allEmployees.getItems()) {
            User user = (User)o;
            if(user.getTeams().contains(team)) {
                user.getTeams().remove(team);
                try {
                    UserRepository.updateUser(user);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void onCancelButtonClick() {
        close();
    }
    
    public void onRightButtonClick() {
        List<User> selected = new ArrayList<>(allEmployees.getSelectionModel().getSelectedItems());
        selected.forEach(selectedEmployees.getItems()::add);
        selected.forEach(allEmployees.getItems()::remove);
    }
    
    public void onLeftButtonClick() {
        List<User> selected = new ArrayList<>(selectedEmployees.getSelectionModel().getSelectedItems());
        selected.forEach(allEmployees.getItems()::add);
        selected.forEach(selectedEmployees.getItems()::remove);
    }
    
    public void onAllRightButtonClick() {
        allEmployees.getItems().forEach(selectedEmployees.getItems()::add);
        allEmployees.getItems().clear();
    }
    
    public void onAllLeftButtonClick() {
        selectedEmployees.getItems().forEach(allEmployees.getItems()::add);
        selectedEmployees.getItems().clear();
    }
    
}
