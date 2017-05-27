package com.mycompany.devman.controllers.managerPanel;

import com.mycompany.devman.domain.Task;
import com.mycompany.devman.domain.WorkTime;
import com.mycompany.devman.repositories.WorkTimeRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by jakub on 12.05.17.
 */
public class WorkTimeController implements Initializable {

    @FXML
    private TableView<WorkTime> workTimeTable;

    private Task task;

    public WorkTimeController(Task task) {
        this.task = task;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TableColumn user = new TableColumn<>("Użytkownik");
        user.setMinWidth(150);
        user.setCellValueFactory(
                new PropertyValueFactory<>("user"));

        TableColumn date = new TableColumn("Data");
        date.setMinWidth(150);
        date.setCellValueFactory(
                new PropertyValueFactory<>("date"));

        TableColumn time = new TableColumn("Czas");
        time.setMinWidth(150);
        time.setCellValueFactory(
                new PropertyValueFactory<>("workTime"));

        try {
            workTimeTable.getItems().addAll(WorkTimeRepository.findWorkTimeByTask(task));
        } catch (Exception e) {
            e.printStackTrace();
        }
        workTimeTable.getColumns().clear();
        workTimeTable.getColumns().addAll(user, date, time);
    }
}
