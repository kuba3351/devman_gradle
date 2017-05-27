package com.mycompany.devman.controllers.managerPanel;

import com.mycompany.devman.domain.Project;
import com.mycompany.devman.domain.Task;
import com.mycompany.devman.domain.Team;
import com.mycompany.devman.repositories.ProjectRepository;
import com.mycompany.devman.repositories.TaskRepository;
import com.mycompany.devman.repositories.TeamRepository;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by jakub on 17.05.17.
 */
public class ArchivedTasksController implements Initializable {

    @FXML
    private ChoiceBox<Object> project2Box;

    @FXML
    private ChoiceBox team2Box;

    @FXML
    private TableView<Task> taskTable;

    public void onTaskFilterButtonClick() throws Exception {
        Object o = team2Box.getSelectionModel().getSelectedItem();
        if(o instanceof Team) {
            taskTable.getItems().clear();
            taskTable.getItems().addAll(TaskRepository.findCompletedTasksByTeam((Team)o));
        }
        else if(o instanceof String && o.equals("Wszystkie")) {
            Object p = project2Box.getSelectionModel().getSelectedItem();
            if(p instanceof Project) {
                taskTable.getItems().clear();
                taskTable.getItems().addAll(TaskRepository.findCompletedTasksByProject((Project)p));
            }
            else if(p instanceof String && p.equals("Wszystkie")) {
                taskTable.getItems().clear();
                taskTable.getItems().addAll(TaskRepository.findCompletedTasks());
            }
        }
    }

    private void setUpProjectFiltering() throws Exception {
        project2Box.getItems().add("Wszystkie");
        project2Box.getItems().addAll(ProjectRepository.findCompletedProjects());
        project2Box.getSelectionModel().selectFirst();

        project2Box.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                Object o = project2Box.getItems().get((Integer)t1);
                if(o instanceof String && o.equals("Wszystkie")) {
                    team2Box.getItems().clear();
                    team2Box.getItems().add("Wszystkie");
                    try {
                        team2Box.getItems().addAll(TeamRepository.findAllTeams());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    team2Box.getSelectionModel().selectFirst();
                }
                else if(o instanceof Project) {
                    team2Box.getItems().clear();
                    team2Box.getItems().add("Wszystkie");
                    try {
                        team2Box.getItems().addAll(TeamRepository.findTeamsByProjrct((Project)o));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    team2Box.getSelectionModel().selectFirst();
                }
            }
        });
    }

    private void setUpTaskTable() throws Exception {
        taskTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        TableColumn name = new TableColumn("Nazwa");
        name.setMinWidth(150);
        name.setCellValueFactory(
                new PropertyValueFactory<>("name"));

        TableColumn startDate = new TableColumn("Data rozpoczęcia");
        startDate.setMinWidth(150);
        startDate.setCellValueFactory(
                new PropertyValueFactory<>("startDate"));

        TableColumn endDate = new TableColumn("Data zakończenia");
        endDate.setMinWidth(150);
        endDate.setCellValueFactory(
                new PropertyValueFactory<>("endDate"));

        TableColumn predictedTime = new TableColumn("Przewidywany czas");
        predictedTime.setMinWidth(150);
        predictedTime.setCellValueFactory(
                new PropertyValueFactory<>("predictedTime"));

        TableColumn taskState = new TableColumn("Status");
        taskState.setMinWidth(150);
        taskState.setCellValueFactory(
                new PropertyValueFactory<>("taskState"));

        taskTable.getItems().addAll(TaskRepository.findCompletedTasks());
        taskTable.getColumns().clear();
        taskTable.getColumns().addAll(name, startDate, endDate, predictedTime, taskState);
    }

    private void setUpTeamFiltering() throws Exception {
        team2Box.getItems().clear();
        team2Box.getItems().add("Wszystkie");
        team2Box.getItems().addAll(TeamRepository.findAllTeams());
        team2Box.getSelectionModel().selectFirst();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            setUpProjectFiltering();
            setUpTeamFiltering();
            setUpTaskTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
