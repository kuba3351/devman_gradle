/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.devman.controllers.managerPanel;

import com.mycompany.devman.repositories.TaskRepository;
import com.mycompany.devman.domain.Task;

import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jakub
 */
public class NewOrEditTaskController extends Observable implements Initializable {

    @FXML
    private Button cancel;
    
    @FXML
    private TextField name;
    
    @FXML
    private DatePicker startDate;
    
    @FXML
    private DatePicker endDate;
    
    @FXML
    private Spinner predictedTime;
    
    private ManagerPanelController controller;

    private Task task;
    
    public NewOrEditTaskController(ManagerPanelController controller) {
        this.controller = controller;
    }

    public NewOrEditTaskController(ManagerPanelController controller, Task task) {
        this.controller = controller;
        this.task = task;
    }


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SpinnerValueFactory factory = new SpinnerValueFactory() {
            @Override
            public void decrement(int i) {
                if((int)this.getValue() > 1)
                    this.setValue((int)this.getValue() - 1);
            }

            @Override
            public void increment(int i) {
                if((int) this.getValue() < 1000) {
                    this.setValue((int)this.getValue() + 1);
                }
            }
        };
        predictedTime.setValueFactory(factory);
        if(task == null) {
            predictedTime.getValueFactory().setValue(1);
        }
        else {
            name.setText(task.getName());
            startDate.setValue(task.getStartDate());
            endDate.setValue(task.getEndDate());
            predictedTime.getValueFactory().setValue(task.getPredictedTime());
        }
    } 
    
    private void close() {
        Stage window = (Stage) cancel.getScene().getWindow();
        window.close();
    }
    
    public void onOkButtonClick() throws Exception {
        boolean newTask = false;
        if(task == null) {
            task = new Task();
            newTask = true;
        }
        task.setName(name.getText());
        task.setStartDate(startDate.valueProperty().get());
        task.setEndDate(endDate.valueProperty().get());
        task.setPredictedTime((Integer)predictedTime.getValue());
        try {
            if(newTask) {
                TaskRepository.addTask(task);
            }
            else {
                TaskRepository.updateTask(task);
            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Błąd dodawania zadania!");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            return;
        }
        if(newTask) {
            controller.addTask(task);
        }
        else  {
            controller.editTask(task);
        }
        setChanged();
        notifyObservers();
        close();
    }
    
    public void onCancelButtonClick() {
        close();
    }
    
}
