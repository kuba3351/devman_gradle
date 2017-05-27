/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.devman.controllers.employeePanel;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.mycompany.devman.domain.Task;
import com.mycompany.devman.domain.User;
import com.mycompany.devman.domain.WorkTime;
import com.mycompany.devman.repositories.WorkTimeRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jakub
 */
public class NewWorkTimeController implements Initializable {

    @FXML
    private Button cancel;

    @FXML
    private DatePicker date;

    @FXML
    private Spinner<Integer> time;

    private Task task;

    private User user;

    private EmployeePanelController employeePanelController;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        date.setValue(LocalDate.now());
        setUpSpinner();
    }

    private void setUpSpinner() {
        SpinnerValueFactory factory = new SpinnerValueFactory() {
            @Override
            public void decrement(int i) {
                if((int)this.getValue() > 1)
                    this.setValue((int)this.getValue() - 1);
            }

            @Override
            public void increment(int i) {
                if((int) this.getValue() < 14) {
                    this.setValue((int)this.getValue() + 1);
                }
            }
        };
        time.setValueFactory(factory);
        time.getValueFactory().setValue(1);
    }

    public NewWorkTimeController(Task task, User user, EmployeePanelController employeePanelController) {
        this.task = task;
        this.user = user;
        this.employeePanelController = employeePanelController;
    }

    private void close() {
        Stage window = (Stage) cancel.getScene().getWindow();
        window.close();
    }
    
    public void onOkButtonClick() throws Exception {
        WorkTime workTime = new WorkTime();
        workTime.setTask(task);
        workTime.setWorkTime(time.getValue());
        workTime.setDate(date.getValue());
        workTime.setUser(user);
        try {
            WorkTimeRepository.addNewWorkTime(workTime);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Błąd logowania czasu!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
        employeePanelController.addNewWorkTime(workTime);
        close();
    }
    
    public void onCancelButtonClick() {
        close();
    }
}
