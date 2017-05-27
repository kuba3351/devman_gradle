package com.mycompany.devman.controllers.employeePanel;

import com.mycompany.devman.MainApp;
import com.mycompany.devman.domain.*;
import com.mycompany.devman.repositories.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author kuba3
 */
public class EmployeePanelController implements Initializable {

    @FXML
    private MenuBar menuBar;

    @FXML
    private TabPane tabPanel;
    
    private User currentUser;
    
    @FXML
    private TableView<Leave> leaveTable;
    
    @FXML
    private ChoiceBox<Object> projectBox;
    
    @FXML
    private ChoiceBox teamBox;
    
    @FXML
    private ChoiceBox<Object> project2Box;
    
    @FXML
    private ChoiceBox team2Box;
    
    @FXML
    private TableView<User> usersTable;
    
    @FXML
    private TableView<Task> tasksTable;

    @FXML
    private TableView<WorkTime> workTimeTable;

    @FXML
    private Label pendingHours;

    @FXML
    private Label pendingDays;

    @FXML
    private Label completedTasks;

    @FXML
    private Label pendingTasks;

    @FXML
    private Label numberOfProjects;

    @FXML
    private Label nextLeave;

    private void setUpNumbers() throws Exception {
        pendingHours.setText(WorkTimeRepository.calculateReamingWorkHoursToday(currentUser).toString());
        pendingDays.setText(LeaveRepository.getReamingLeaveDays(currentUser).toString());
        pendingTasks.setText(String.valueOf(TaskRepository.findTasksInProgressByUser(currentUser).stream().filter(task -> !LocalDate.now().isAfter(task.getEndDate())).count()));
        numberOfProjects.setText(String.valueOf(ProjectRepository.findProjectsInProgressByUser(currentUser).size()));
        nextLeave.setText(LeaveRepository
                .getNextLeaveDate(currentUser).map(LocalDate::toString).orElse("brak"));
        completedTasks.setText(String.valueOf(TaskRepository.findCompletedTasksByUser(currentUser).stream().filter(task -> LocalDate.now().isAfter(task.getEndDate())).count()));
    }
    
    public EmployeePanelController(User user) {
        this.currentUser = user;
    }
    
    public void addNewLeaveRequest(Leave leave) throws Exception {
        leaveTable.getItems().add(leave);
    }

    private void showInfoWindow() {
        Stage infoWindow = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/employeePanel/Info.fxml"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/register.css");

        infoWindow.setTitle("DevMan - Informacje");
        infoWindow.setResizable(false);
        infoWindow.setScene(scene);
        infoWindow.setX(20);
        infoWindow.setY(20);
        infoWindow.show();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        menuBar.getMenus().filtered(menu -> menu.getText().equals("Plik")).get(0).getItems().filtered(item -> item.getText().equals("Zamknij")).get(0).addEventHandler(EventType.ROOT, new EventHandler() {
            @Override
            public void handle(Event t) {
                close();
            }
        });
        menuBar.getMenus().filtered(menu -> menu.getText().equals("Pomoc")).get(0).getItems().filtered(item -> item.getText().equals("Informacje")).get(0).addEventHandler(EventType.ROOT, new EventHandler() {
            @Override
            public void handle(Event t) {
                showInfoWindow();
            }


        });
        menuBar.getMenus().filtered(menu ->
                menu.getText().equals("Plik")).get(0)
                .getItems().filtered(item ->
                item.getText().equals("Wyloguj"))
                .get(0).addEventHandler(EventType.ROOT, t -> {
            close();
            try {
                new MainApp().showLoginWindow(new Stage(StageStyle.DECORATED));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        try {
            setUpLeavesTable();
            setUpUsersTable();
            setUpTasksTable();
            setUpProjectFiltering();
            setUpTeamFiltering();
            setUpWorkTimeTable();
            setUpNumbers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addNewWorkTime(WorkTime workTime) throws Exception {
        workTimeTable.getItems().add(workTime);
        setUpNumbers();
    }

    private void setUpTeamFiltering() throws Exception {
        teamBox.getItems().clear();
        teamBox.getItems().add("Wszystkie");
        teamBox.getItems().addAll(TeamRepository.findTeamsByUser(currentUser));
        teamBox.getSelectionModel().selectFirst();


        team2Box.setItems(teamBox.getItems());
        team2Box.setSelectionModel(teamBox.getSelectionModel());
    }

    public void editWorkTime(WorkTime workTime) throws Exception {
        workTimeTable.getItems().replaceAll(new UnaryOperator<WorkTime>() {
            @Override
            public WorkTime apply(WorkTime w) {
                if(workTime.getId().equals(w.getId())) {
                    return w;
                }
                else {
                    return workTime;
                }
            }
        });
        setUpNumbers();
    }

    private void setUpProjectFiltering() throws Exception {
        projectBox.getItems().add("Wszystkie");
        projectBox.getItems().addAll(ProjectRepository.findProjectsInProgressByUser(currentUser));
        projectBox.getSelectionModel().selectFirst();

        projectBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                Object o = projectBox.getItems().get((Integer)t1);
                if(o instanceof String && o.equals("Wszystkie")) {
                    teamBox.getItems().clear();
                    teamBox.getItems().add("Wszystkie");
                    try {
                        teamBox.getItems().addAll(TeamRepository.findTeamsByUser(currentUser));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    teamBox.getSelectionModel().selectFirst();
                }
                else if(o instanceof Project) {
                    teamBox.getItems().clear();
                    teamBox.getItems().add("Wszystkie");
                    try {
                        teamBox.getItems().addAll(TeamRepository.findTeamsByProjectAndUser((Project)o, currentUser));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    teamBox.getSelectionModel().selectFirst();
                }
            }
        });
        project2Box.setItems(projectBox.getItems());
        project2Box.setSelectionModel(projectBox.getSelectionModel());
    }

    private void setUpTasksTable() throws Exception {
        TableColumn nameColumn = new TableColumn("Nazwa");
        nameColumn.setMinWidth(150);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn startDate = new TableColumn("Data rozpoczęcia");
        startDate.setMinWidth(150);
        startDate.setCellValueFactory(
                new PropertyValueFactory<>("startDate"));

        TableColumn endDate = new TableColumn("Data zakończenia");
        endDate.setMinWidth(200);
        endDate.setCellValueFactory(
                new PropertyValueFactory<>("endDate"));

        TableColumn predictedTime = new TableColumn("Przewidywany czas");
        predictedTime.setMinWidth(200);
        predictedTime.setCellValueFactory(
                new PropertyValueFactory<>("predictedTime"));

        tasksTable.getItems().addAll(TaskRepository.findTasksInProgressByUser(currentUser));
        tasksTable.getColumns().clear();
        tasksTable.getColumns().addAll(nameColumn, startDate, endDate, predictedTime);
    }

    private void setUpWorkTimeTable() throws Exception {
        TableColumn dateColumn = new TableColumn("Data");
        dateColumn.setMinWidth(150);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn task = new TableColumn("Zadanie");
        task.setMinWidth(150);
        task.setCellValueFactory(
                new PropertyValueFactory<>("task"));

        TableColumn time = new TableColumn("Czas");
        time.setMinWidth(200);
        time.setCellValueFactory(
                new PropertyValueFactory<>("workTime"));

        workTimeTable.getItems().addAll(WorkTimeRepository.findByUser(currentUser));
        workTimeTable.getColumns().clear();
        workTimeTable.getColumns().addAll(dateColumn, task, time);
    }

    private void setUpUsersTable() throws Exception {
        TableColumn name = new TableColumn("Imię");
        name.setMinWidth(150);
        name.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn lastName = new TableColumn("Nazwisko");
        lastName.setMinWidth(150);
        lastName.setCellValueFactory(
                new PropertyValueFactory<>("lastName"));

        TableColumn email = new TableColumn("e-mail");
        email.setMinWidth(200);
        email.setCellValueFactory(
                new PropertyValueFactory<>("email"));

        usersTable.getItems().addAll(UserRepository.findAnotherUsersInTeam(currentUser));
        usersTable.getColumns().clear();
        usersTable.getColumns().addAll(name, lastName, email);
    }

    private void setUpLeavesTable() throws Exception {
        TableColumn status = new TableColumn("Status");
        status.setMinWidth(150);
        status.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn startDate = new TableColumn("Data rozpoczęcia");
        startDate.setMinWidth(150);
        startDate.setCellValueFactory(
                new PropertyValueFactory<>("startDate"));

        TableColumn numberOfDays = new TableColumn("ilość dni");
        numberOfDays.setMinWidth(200);
        numberOfDays.setCellValueFactory(
                new PropertyValueFactory<>("numberOfDays"));

        leaveTable.getItems().addAll(LeaveRepository.findLeavesByUser(currentUser));
        leaveTable.getColumns().clear();
        leaveTable.getColumns().addAll(status, startDate, numberOfDays);
    }

    private void close() {
        Stage window = (Stage) menuBar.getScene().getWindow();
        window.close();
    }
    
    public void onTeamFilterClick() throws Exception {
        Object o = teamBox.getSelectionModel().getSelectedItem();
        if(o instanceof Team) {
            usersTable.getItems().clear();
            usersTable.getItems().addAll(UserRepository.findUsersByTeam((Team)o));
        }
        else if(o instanceof String && o.equals("Wszystkie")) {
            Object p = projectBox.getSelectionModel().getSelectedItem();
            if(p instanceof Project) {
                usersTable.getItems().clear();
                usersTable.getItems().addAll(UserRepository.findUsersByProject((Project)p));
            }
            else if(p instanceof String && p.equals("Wszystkie")) {
                usersTable.getItems().clear();
                usersTable.getItems().addAll(UserRepository.findAnotherUsersInTeam(currentUser));
            }
        }
    }
    
    public void onTaskFilterButtonClick() throws Exception {
        Object o = teamBox.getSelectionModel().getSelectedItem();
        if(o instanceof Team) {
            tasksTable.getItems().clear();
            tasksTable.getItems().addAll(TaskRepository.findTasksByTeam((Team)o));
        }
        else if(o instanceof String && o.equals("Wszystkie")) {
            Object p = projectBox.getSelectionModel().getSelectedItem();
            if(p instanceof Project) {
                tasksTable.getItems().clear();
                tasksTable.getItems().addAll(TaskRepository.findTasksByProject((Project)p));
            }
            else if(p instanceof String && p.equals("Wszystkie")) {
                tasksTable.getItems().clear();
                tasksTable.getItems().addAll(TaskRepository.findTasksInProgressByUser(currentUser));
            }
        }
    }
    
    public void onTaskManagementButtonClick() {
        tabPanel.getSelectionModel().select(tabPanel.getTabs().filtered(tab -> tab.getText().equals("Zadania")).get(0));
    }
   
    
    public void onTeamInfoButtonClick() {
        tabPanel.getSelectionModel().select(tabPanel.getTabs().filtered(tab -> tab.getText().equals("Zespół")).get(0));
    }
    
    public void onWorkTimeButtonClick() {
        tabPanel.getSelectionModel().select(tabPanel.getTabs().filtered(tab -> tab.getText().equals("Czas pracy")).get(0));
    }
    
    public void onLeaveRequestsButtonClick() {
        tabPanel.getSelectionModel().select(tabPanel.getTabs().filtered(tab -> tab.getText().equals("Urlopy")).get(0));
    }
    
    public void onNewWorkTimeButtonClick() throws IOException {
        if(tasksTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Błąd logowania czasu!");
            alert.setContentText("Nie wybrano zadania!");
            alert.showAndWait();
            return;
        }
        Stage newWorkTimeWindow = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/employeePanel/NewWorkTime.fxml"));
        NewWorkTimeController controller = new NewWorkTimeController(tasksTable
                .getSelectionModel().getSelectedItem(), currentUser, this);
        loader.setController(controller);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/register.css");

        newWorkTimeWindow.setTitle("DevMan - Logowanie czasu pracy");
        newWorkTimeWindow.setResizable(false);
        newWorkTimeWindow.setScene(scene);
        newWorkTimeWindow.setX(20);
        newWorkTimeWindow.setY(20);
        newWorkTimeWindow.show();
    }
    
    public void onWorkTimeEditButtonClick() throws IOException {
        if(workTimeTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Błąd korygowania czasu!");
            alert.setContentText("Nie zaznaczono czasu do skorygowania!");
            alert.showAndWait();
            return;
        }
        Stage workTimeEditWindow = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/employeePanel/editWorkTime.fxml"));

        EditWorkTimeController controller = new EditWorkTimeController(workTimeTable.getSelectionModel().getSelectedItem(), this);
        loader.setController(controller);

        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/register.css");

        workTimeEditWindow.setTitle("DevMan - Edycja czasu pracy");
        workTimeEditWindow.setResizable(false);
        workTimeEditWindow.setScene(scene);
        workTimeEditWindow.setX(20);
        workTimeEditWindow.setY(20);
        workTimeEditWindow.show();
    }

    public void onAddLeaveRequestButtonClick() throws IOException {
        Stage leaveRequest = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/employeePanel/AddLeaveRequest.fxml"));
        AddLeaveRequestController controller = new AddLeaveRequestController(currentUser, this);
        loader.setController(controller);
        Parent root = (Parent) loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/register.css");

        leaveRequest.setTitle("DevMan - Dodawanie wniosku o urlop");
        leaveRequest.setResizable(false);
        leaveRequest.setScene(scene);
        leaveRequest.setX(20);
        leaveRequest.setY(20);
        leaveRequest.show();
    }
    
}
