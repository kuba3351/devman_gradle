/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.devman.controllers.employeePanel;

import java.net.URL;
import java.util.ResourceBundle;

import com.mycompany.devman.domain.WorkTime;
import com.mycompany.devman.repositories.WorkTimeRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author bloczek
 */
public class EditWorkTimeController implements Initializable {

    @FXML
    private Button cancel;

    private WorkTime workTime;

    @FXML
    private Spinner<Integer> time;

    private EmployeePanelController controller;

    public EditWorkTimeController(WorkTime workTime, EmployeePanelController controller) {
        this.workTime = workTime;
        this.controller = controller;
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
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setUpSpinner();
        time.getValueFactory().setValue(workTime.getWorkTime());
    }    
    
    private void close() {
        Stage window = (Stage) cancel.getScene().getWindow();
        window.close();
    }
    
    public void onCancelButtonClick() {
        close();
    }
    
    public void onOkButtonClick() throws Exception {
        workTime.setWorkTime(time.getValueFactory().getValue());
        WorkTimeRepository.updateWorkTime(workTime);
        controller.editWorkTime(workTime);
        close();
    }
}
