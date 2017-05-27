package com.mycompany.devman.controllers.managerPanel;

import com.mycompany.devman.domain.Project;
import com.mycompany.devman.domain.User;
import com.mycompany.devman.repositories.ProjectRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by jakub on 17.05.17.
 */
public class ArchivedProjectsController implements Initializable {

    @FXML
    private TableView<Project> projectsTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TableColumn idCol = new TableColumn("ID");
        idCol.setMinWidth(100);
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn nameCol = new TableColumn("Nazwa");
        nameCol.setMinWidth(150);
        nameCol.setCellValueFactory(
                new PropertyValueFactory<>("name"));

        TableColumn startDateCol = new TableColumn("Data rozpoczęcia");
        startDateCol.setMinWidth(150);
        startDateCol.setCellValueFactory(
                new PropertyValueFactory<>("startDate"));

        TableColumn endDateCol = new TableColumn("Data zakończenia");
        endDateCol.setMinWidth(150);
        endDateCol.setCellValueFactory(
                new PropertyValueFactory<>("endDate"));

        try {
            projectsTable.getItems().addAll(ProjectRepository.findCompletedProjects());
        } catch (Exception e) {
            e.printStackTrace();
        }
        projectsTable.getColumns().clear();
        projectsTable.getColumns().addAll(idCol, nameCol, startDateCol, endDateCol);
        System.out.print("test");
    }
}
