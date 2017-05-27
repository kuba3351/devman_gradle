package com.mycompany.devman.controllers.managerPanel;

import com.mycompany.devman.MainApp;
import com.mycompany.devman.repositories.*;
import com.mycompany.devman.domain.Project;
import com.mycompany.devman.domain.Task;
import com.mycompany.devman.domain.Team;
import com.mycompany.devman.domain.User;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
public class ManagerPanelController implements Initializable, Observer {

    @FXML
    private TabPane tabPanel;

    @FXML
    private MenuBar menuBar;
    
    @FXML
    private TableView<Project> projectsTable;
    
    @FXML
    private TableView<Team> teamsTable;
    
    @FXML
    private TableView<Task> taskTable;

    @FXML
    private TableView<User> employeeTable;

    @FXML
    private Label pendingEmployees;

    @FXML
    private Label pendingLeaveRequests;

    @FXML
    private Label numberOfProjects;

    @FXML
    private Label numberOfTeams;

    @FXML
    private Label tasksInProgress;

    @FXML
    private Label completedTasks;

    @FXML
    private ChoiceBox<Object> projectBox;

    @FXML
    private ChoiceBox teamBox;

    @FXML
    private ChoiceBox<Object> project2Box;

    @FXML
    private ChoiceBox team2Box;
    
    private User currentUser;

    public ManagerPanelController(User user) {
        this.currentUser = user;
    }

    private void initializeNumbersOnMainPage() throws Exception {
        pendingEmployees.setText(Integer
                .valueOf(UserRepository.findInactiveUsers().size()).toString());
        pendingLeaveRequests.setText(Integer
                .valueOf(LeaveRepository.findPendingLeavesByManager(currentUser)
                        .size()).toString());
        numberOfProjects.setText(Long
                .valueOf(ProjectRepository.findProjectsInProgress()
                        .stream().filter(project -> !LocalDate.now()
                                .isBefore(project.getStartDate()) && !LocalDate.now()
                                .isAfter(project.getEndDate())).count()).toString());
        numberOfTeams.setText(Integer
                .valueOf(TeamRepository.findAllTeams().size()).toString());
        tasksInProgress.setText(Long
                .valueOf(TaskRepository.findTasksInProgress()
                        .stream().filter(task -> !LocalDate.now()
                                .isBefore(task.getStartDate()) && !LocalDate.now()
                                .isAfter(task.getEndDate())).count()).toString());
        completedTasks.setText(Integer.valueOf(Integer.sum(TaskRepository.findCompletedTasks()
                .size(), Long.valueOf(TaskRepository.findTasksInProgress()
                .stream().filter(task -> LocalDate.now()
                        .isAfter(task.getEndDate())).count()).intValue())).toString());
    }

    private void showInfoWindow() {
        Stage infoWindow = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/Info.fxml"));
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
        menuBar.getMenus().filtered(menu ->
                menu.getText().equals("Plik")).get(0)
                .getItems().filtered(item ->
                    item.getText().equals("Zamknij"))
                    .get(0).addEventHandler(EventType.ROOT, t -> close());
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
        menuBar.getMenus().filtered(menu ->
                menu.getText().equals("Pomoc")).get(0)
                .getItems().filtered(item ->
                    item.getText().equals("Informacje"))
                    .get(0).addEventHandler(EventType.ROOT, t -> showInfoWindow());
        menuBar.getMenus().filtered(menu ->
                menu.getText().equals("Edycja")).get(0)
                .getItems().filtered(item ->
                item.getText().equals("Zaznacz wszystko"))
                .get(0).addEventHandler(EventType.ROOT, t -> {
                    if(tabPanel.getSelectionModel().getSelectedItem().getText().equals("Pracownicy")) {
                        employeeTable.getSelectionModel().selectAll();
                    }
                    if(tabPanel.getSelectionModel().getSelectedItem().getText().equals("Zespoły")) {
                        teamsTable.getSelectionModel().selectAll();
                    }
                    if(tabPanel.getSelectionModel().getSelectedItem().getText().equals("Projekty")) {
                        projectsTable.getSelectionModel().selectAll();
                    }
                    if(tabPanel.getSelectionModel().getSelectedItem().getText().equals("Zadania")) {
                        taskTable.getSelectionModel().selectAll();
                    }

                });


        try {
            setUpProjectTable();
            setUpEmployeeTable();
            setUpTeamTable();
            setUpTaskTable();
            setUpTeamFiltering();
            setUpProjectFiltering();
            initializeNumbersOnMainPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onTeamFilterClick() throws Exception {
        Object o = teamBox.getSelectionModel().getSelectedItem();
        if(o instanceof Team) {
            employeeTable.getItems().clear();
            employeeTable.getItems().addAll(UserRepository.findUsersByTeam((Team)o));
        }
        else if(o instanceof String && o.equals("Wszystkie")) {
            Object p = projectBox.getSelectionModel().getSelectedItem();
            if(p instanceof Project) {
                employeeTable.getItems().clear();
                employeeTable.getItems().addAll(UserRepository.findUsersByProject((Project)p));
            }
            else if(p instanceof String && p.equals("Wszystkie")) {
                employeeTable.getItems().clear();
                employeeTable.getItems().addAll(UserRepository.findEmployees());
            }
        }
    }

    public void onTaskFilterButtonClick() throws Exception {
        Object o = teamBox.getSelectionModel().getSelectedItem();
        if(o instanceof Team) {
            taskTable.getItems().clear();
            taskTable.getItems().addAll(TaskRepository.findTasksByTeam((Team)o));
        }
        else if(o instanceof String && o.equals("Wszystkie")) {
            Object p = projectBox.getSelectionModel().getSelectedItem();
            if(p instanceof Project) {
                taskTable.getItems().clear();
                taskTable.getItems().addAll(TaskRepository.findTasksByProject((Project)p));
            }
            else if(p instanceof String && p.equals("Wszystkie")) {
                taskTable.getItems().clear();
                taskTable.getItems().addAll(TaskRepository.findTasksInProgress());
            }
        }
    }

    private void setUpProjectFiltering() throws Exception {
        projectBox.getItems().add("Wszystkie");
        projectBox.getItems().addAll(ProjectRepository.findProjectsInProgress());
        projectBox.getSelectionModel().selectFirst();

        projectBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                Object o = projectBox.getItems().get((Integer)t1);
                if(o instanceof String && o.equals("Wszystkie")) {
                    teamBox.getItems().clear();
                    teamBox.getItems().add("Wszystkie");
                    try {
                        teamBox.getItems().addAll(TeamRepository.findAllTeams());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    teamBox.getSelectionModel().selectFirst();
                }
                else if(o instanceof Project) {
                    teamBox.getItems().clear();
                    teamBox.getItems().add("Wszystkie");
                    try {
                        teamBox.getItems().addAll(TeamRepository.findTeamsByProjrct((Project)o));
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

    private void setUpTeamFiltering() throws Exception {
        teamBox.getItems().clear();
        teamBox.getItems().add("Wszystkie");
        teamBox.getItems().addAll(TeamRepository.findAllTeams());
        teamBox.getSelectionModel().selectFirst();


        team2Box.setItems(teamBox.getItems());
        team2Box.setSelectionModel(teamBox.getSelectionModel());
    }

    private void setUpEmployeeTable() throws Exception {
        employeeTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        TableColumn id = new TableColumn("ID");
        id.setMinWidth(150);
        id.setCellValueFactory(
                new PropertyValueFactory<>("id"));

        TableColumn name = new TableColumn("Imię");
        name.setMinWidth(150);
        name.setCellValueFactory(
                new PropertyValueFactory<>("name"));

        TableColumn lastName = new TableColumn("Nazwisko");
        lastName.setMinWidth(150);
        lastName.setCellValueFactory(
                new PropertyValueFactory<>("lastName"));

        TableColumn email = new TableColumn("E-mail");
        email.setMinWidth(150);
        email.setCellValueFactory(
                new PropertyValueFactory<>("email"));

        employeeTable.getItems().addAll(UserRepository.findUsersByManager(currentUser));
        employeeTable.getColumns().clear();
        employeeTable.getColumns().addAll(id, name, lastName, email);
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

        taskTable.getItems().addAll(TaskRepository.findTasksInProgress());
        taskTable.getColumns().clear();
        taskTable.getColumns().addAll(name, startDate, endDate, predictedTime, taskState);
    }

    private void setUpTeamTable() throws Exception {
        teamsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        TableColumn idCol = new TableColumn("ID");
        idCol.setMinWidth(100);
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn nameCol = new TableColumn("Nazwa");
        nameCol.setMinWidth(150);
        nameCol.setCellValueFactory(
                new PropertyValueFactory<>("name"));

        TableColumn projectCol = new TableColumn("Projekt");
        projectCol.setMinWidth(100);
        projectCol.setCellValueFactory(new PropertyValueFactory<>("project"));

        teamsTable.getItems().addAll(TeamRepository.findAllTeams());
        teamsTable.getColumns().clear();
        teamsTable.getColumns().addAll(idCol, nameCol, projectCol);
    }

    private void setUpProjectTable() throws Exception {
        projectsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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

        projectsTable.getItems().addAll(ProjectRepository.findProjectsInProgress());
        projectsTable.getColumns().clear();
        projectsTable.getColumns().addAll(idCol, nameCol, startDateCol, endDateCol);
    }

    public void updateEmployee(User user) {
        employeeTable.getItems().replaceAll(new UnaryOperator<User>() {
            @Override
            public User apply(User u) {
                if(user.getId().equals(u.getId())) {
                    return user;
                }
                else {
                    return u;
                }
            }
        });
    }

    public void addTask(Task task) throws Exception {
        taskTable.getItems().add(task);
        initializeNumbersOnMainPage();
    }
    
    public void addTeam(Team team) throws Exception {
        teamsTable.getItems().add(team);
        initializeNumbersOnMainPage();
    }
    
    public void addProject(Project project) throws Exception {
        projectsTable.getItems().add(project);
        initializeNumbersOnMainPage();
    }

    private void close() {
        Stage window = (Stage) tabPanel.getScene().getWindow();
        window.close();
    }

    public void onTasksInProgressClick() {
        tabPanel.getSelectionModel().select(tabPanel.getTabs().filtered(tab -> tab.getText().equals("Zadania")).get(0));
    }

    public void onProjectsInProgressClick() {
        tabPanel.getSelectionModel().select(tabPanel.getTabs().filtered(tab -> tab.getText().equals("Projekty")).get(0));
    }

    public void onEmployeeVerifyClick() throws IOException {
        Stage employeeVerifyWindow = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/managerPanel/EmployeeVerify.fxml"));
        Parent root = loader.load();
        EmployeeVerifyController controller = loader.getController();
        controller.addObserver(this);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/register.css");

        employeeVerifyWindow.setTitle("DevMan - Weryfikacja pracowników");
        employeeVerifyWindow.setResizable(false);
        employeeVerifyWindow.setScene(scene);
        employeeVerifyWindow.setX(20);
        employeeVerifyWindow.setY(20);
        employeeVerifyWindow.show();
    }

    public void onArchiveTasksButtonClick() throws IOException {
        Stage employeeVerifyWindow = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/managerPanel/ArchivedTasks.fxml"));
        ArchivedTasksController controller = new ArchivedTasksController();
        loader.setController(controller);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/register.css");

        employeeVerifyWindow.setTitle("DevMan - Archiwalne zadania");
        employeeVerifyWindow.setResizable(false);
        employeeVerifyWindow.setScene(scene);
        employeeVerifyWindow.setX(20);
        employeeVerifyWindow.setY(20);
        employeeVerifyWindow.show();
    }

    public void onWorkTimeButtonClick() throws IOException {
        if(taskTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Błąd!");
            alert.setContentText("Nie wybrano zadania");
            alert.showAndWait();
            return;
        }
        Stage employeeVerifyWindow = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/managerPanel/workTime.fxml"));
        WorkTimeController controller = new WorkTimeController(taskTable.getSelectionModel().getSelectedItem());
        loader.setController(controller);
        Parent root = loader.load();

        Scene scene = new Scene(root);

        employeeVerifyWindow.setTitle("DevMan - Czas pracy");
        employeeVerifyWindow.setResizable(false);
        employeeVerifyWindow.setScene(scene);
        employeeVerifyWindow.setX(20);
        employeeVerifyWindow.setY(20);
        employeeVerifyWindow.show();
    }

    public void onArchivedProjectsButtonClick() throws IOException {
        Stage employeeVerifyWindow = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/managerPanel/ArchivedProjects.fxml"));
        ArchivedProjectsController controller = new ArchivedProjectsController();
        loader.setController(controller);
        Parent root = loader.load();

        Scene scene = new Scene(root);

        employeeVerifyWindow.setTitle("DevMan - Archiwalne projekty");
        employeeVerifyWindow.setResizable(false);
        employeeVerifyWindow.setScene(scene);
        employeeVerifyWindow.setX(20);
        employeeVerifyWindow.setY(20);
        employeeVerifyWindow.show();
    }

    public void onArchiveProjectButtonClick() throws Exception {
        for(Project project : projectsTable.getSelectionModel().getSelectedItems()) {
            project.setProjectState(Project.ProjectState.FINISHED);
            ProjectRepository.updateProject(project);
        }
        projectsTable.getItems().removeAll(projectsTable.getSelectionModel().getSelectedItems());
        initializeNumbersOnMainPage();
    }

    public void onLeaveVerifyClick() throws IOException {
        Stage leaveRequestVerifyWindow = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/managerPanel/LeaveRequest.fxml"));
        LeaveRequestController controller = new LeaveRequestController(currentUser);
        controller.addObserver(this);
        loader.setController(controller);
        Parent root = (Parent)loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/register.css");

        leaveRequestVerifyWindow.setTitle("DevMan - Weryfikacja wniosków o urlop");
        leaveRequestVerifyWindow.setResizable(false);
        leaveRequestVerifyWindow.setScene(scene);
        leaveRequestVerifyWindow.setX(20);
        leaveRequestVerifyWindow.setY(20);
        leaveRequestVerifyWindow.show();
    }

    private void showEditEmployeeWindow() throws IOException {
        showAddOrEditEmployeeWindow();
    }

    private void showAddOrEditEmployeeWindow() throws IOException {
        User selected = employeeTable.getSelectionModel().getSelectedItem();
        if(selected == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Błąd!");
            alert.setContentText("Nie wybrano pracownika!");
            alert.showAndWait();
            return;
        }
        Stage employeeAddWindow = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/managerPanel/EditEmployee.fxml"));
        EditEmployeeController controller = new EditEmployeeController(selected, this);
        loader.setController(controller);
        Parent root = loader.load();
        controller.addObserver(this);
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/register.css");

        employeeAddWindow.setTitle("DevMan - Weryfikacja pracowników");
        employeeAddWindow.setResizable(false);
        employeeAddWindow.setScene(scene);
        employeeAddWindow.setX(20);
        employeeAddWindow.setY(20);
        employeeAddWindow.show();
    }

    public void onEditEmployeeClick() throws IOException {
        showAddOrEditEmployeeWindow();
    }
    
    public void onAddTeamClick() throws IOException {
        showAddTeamWindow();
    }

    private void showAddTeamWindow() throws IOException {
        Stage teamAddWindow = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/managerPanel/AddOrEditTeam.fxml"));
        AddOrEditTeamController controller = new AddOrEditTeamController(this);
        loader.setController(controller);
        controller.addObserver(this);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/register.css");

        teamAddWindow.setTitle("DevMan - Dodawanie zespołu");
        teamAddWindow.setResizable(false);
        teamAddWindow.setScene(scene);
        teamAddWindow.setX(20);
        teamAddWindow.setY(20);
        teamAddWindow.show();
    }

    private void showEditTeamWindow(Team team) throws IOException {
        Stage teamAddWindow = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/managerPanel/AddOrEditTeam.fxml"));
        AddOrEditTeamController controller = new AddOrEditTeamController(this, team);
        loader.setController(controller);
        controller.addObserver(this);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/register.css");

        teamAddWindow.setTitle("DevMan - Edycja zespołu");
        teamAddWindow.setResizable(false);
        teamAddWindow.setScene(scene);
        teamAddWindow.setX(20);
        teamAddWindow.setY(20);
        teamAddWindow.show();
    }

    public void onEditTeamClick() throws IOException {
        showEditTeamWindow(teamsTable.getSelectionModel().getSelectedItem());
    }

    public void editTeam(Team t) {
        teamsTable.getItems().replaceAll(new UnaryOperator<Team>() {
            @Override
            public Team apply(Team team) {
                if(team.getId().equals(t.getId())) {
                    return t;
                }
                else {
                    return team;
                }
            }
        });
    }
    
    public void onTaskAssignButtonClick() throws IOException {
        if (!checkIfTeamSelected()) return;
        Stage taskAssignWindow = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/managerPanel/TaskAssign.fxml"));

        TaskAssignController controller = new TaskAssignController((Team)teamsTable.getSelectionModel().getSelectedItem());
        loader.setController(controller);
        controller.addObserver(this);
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/register.css");

        taskAssignWindow.setTitle("DevMan - Przydział zadań");
        taskAssignWindow.setResizable(false);
        taskAssignWindow.setScene(scene);
        taskAssignWindow.setX(20);
        taskAssignWindow.setY(20);
        taskAssignWindow.show();
    }
    
    public void onEmployeeAssignClick() throws IOException {
        if (!checkIfTeamSelected()) return;
        Stage employeeAssign = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/managerPanel/EmployeeAssign.fxml"));
        
        EmployeeAssignController controller = new EmployeeAssignController((Team)teamsTable.getSelectionModel().getSelectedItem());
        loader.setController(controller);
        controller.addObserver(this);
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/register.css");

        employeeAssign.setTitle("DevMan - Przydział pracowników");
        employeeAssign.setResizable(false);
        employeeAssign.setScene(scene);
        employeeAssign.setX(20);
        employeeAssign.setY(20);
        employeeAssign.show();
    }

    public void onDeleteEmployeeButtonClick() throws Exception {
        for(User employee : employeeTable.getSelectionModel().getSelectedItems()) {
            UserRepository.deleteUser(employee);
        }
        employeeTable.getItems().removeAll(employeeTable.getSelectionModel().getSelectedItems());
    }

    private boolean checkIfTeamSelected() {
        if(teamsTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Błąd!");
            alert.setContentText("Nie wybrano zespołu!");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    public void onTeamsAssignClick() throws IOException {
        Stage teamsAssign = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/managerPanel/TeamsAssign.fxml"));

        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/register.css");

        teamsAssign.setTitle("DevMan - Przydział drużyn");
        teamsAssign.setResizable(false);
        teamsAssign.setScene(scene);
        teamsAssign.setX(20);
        teamsAssign.setY(20);
        teamsAssign.show();
    }

    public void onDeleteTeamButtonClick() throws Exception {
        for(Team team : teamsTable.getSelectionModel().getSelectedItems()) {
            TeamRepository.deleteTeam(team);
        }
        teamsTable.getItems().removeAll(teamsTable.getSelectionModel().getSelectedItems());
        initializeNumbersOnMainPage();
    }
    
    public void onNewProjectButtonClick() throws IOException {
        showAddProjectWindow();
    }

    private void showAddProjectWindow() throws IOException {
        Stage newProject = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/managerPanel/AddOrEditProject.fxml"));

        AddOrEditProjectController controller = new AddOrEditProjectController(this);
        loader.setController(controller);
        Parent root = loader.load();

        controller.addObserver(this);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/register.css");

        newProject.setTitle("DevMan - Dodaj projekt");
        newProject.setResizable(false);
        newProject.setScene(scene);
        newProject.setX(20);
        newProject.setY(20);
        newProject.show();
    }

    private void showEditProjectWindow() throws IOException {
        if(projectsTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Błąd!");
            alert.setContentText("Nie wybrano projektu!");
            alert.showAndWait();
            return;
        }
        Stage newProject = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/managerPanel/AddOrEditProject.fxml"));

        AddOrEditProjectController controller = new AddOrEditProjectController(this, projectsTable.getSelectionModel().getSelectedItem());
        loader.setController(controller);
        Parent root = loader.load();

        controller.addObserver(this);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/register.css");

        newProject.setTitle("DevMan - Edytuj projekt");
        newProject.setResizable(false);
        newProject.setScene(scene);
        newProject.setX(20);
        newProject.setY(20);
        newProject.show();
    }

    public void onEditProjectButtonClick() throws IOException {
        showEditProjectWindow();
    }
    
    public void onNewTaskButtonClick() throws IOException {
        showAddTaskWindow();
    }

    private void showAddTaskWindow() throws IOException {
        Stage newTask = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/managerPanel/NewOrEditTask.fxml"));
        NewOrEditTaskController controller = new NewOrEditTaskController(this);
        loader.setController(controller);
        Parent root = loader.load();

        controller.addObserver(this);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/register.css");

        newTask.setTitle("DevMan - Dodawanie zadania");
        newTask.setResizable(false);
        newTask.setScene(scene);
        newTask.setX(20);
        newTask.setY(20);
        newTask.show();
    }

    private void showEditTaskWindow() throws IOException {
        if(taskTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Błąd!");
            alert.setContentText("Nie wybrano zadania!");
            alert.showAndWait();
            return;
        }
        Stage newTask = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/managerPanel/NewOrEditTask.fxml"));
        NewOrEditTaskController controller = new NewOrEditTaskController(this, taskTable.getSelectionModel().getSelectedItem());
        loader.setController(controller);
        Parent root = loader.load();

        controller.addObserver(this);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/main/register.css");

        newTask.setTitle("DevMan - Edycja zadania");
        newTask.setResizable(false);
        newTask.setScene(scene);
        newTask.setX(20);
        newTask.setY(20);
        newTask.show();
    }

    public void onEditTaskButtonClick() throws IOException {
        showEditTaskWindow();
    }
    public void onArchieveTaskButtonClick() throws Exception {
        for(Task task : taskTable.getSelectionModel().getSelectedItems()) {
            task.setTaskState(Task.TaskState.FINISHED);
            TaskRepository.updateTask(task);
        }
        taskTable.getItems().removeAll(taskTable.getSelectionModel().getSelectedItems());
        initializeNumbersOnMainPage();
    }

    @Override
    public void update(java.util.Observable observable, Object o) {
        try {
            initializeNumbersOnMainPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateProject(Project p) {
        projectsTable.getItems().replaceAll(new UnaryOperator<Project>() {
            @Override
            public Project apply(Project project) {
                if(p.getId().equals(project.getId())) {
                    return p;
                }
                else {
                    return project;
                }
            }
        });
    }

    public void editTask(Task t) {
        taskTable.getItems().replaceAll(new UnaryOperator<Task>() {
            @Override
            public Task apply(Task task) {
                if(t.getId().equals(task.getId())) {
                    return t;
                }
                else {
                    return task;
                }
            }
        });
    }
}
