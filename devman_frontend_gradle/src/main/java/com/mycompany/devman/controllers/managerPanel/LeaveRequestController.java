
package com.mycompany.devman.controllers.managerPanel;

import com.mycompany.devman.repositories.LeaveRepository;
import com.mycompany.devman.domain.Leave;
import com.mycompany.devman.domain.LeaveRequestStatus;
import com.mycompany.devman.domain.User;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author jakub
 */
public class LeaveRequestController extends Observable implements Initializable {

    @FXML
    private TableView<Leave> leaveTable;
    
    private User currentUser;
    
    public LeaveRequestController(User user) {
        this.currentUser = user;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        leaveTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        TableColumn employee = new TableColumn("Pracownik");
        employee.setMinWidth(150);
        employee.setCellValueFactory(new PropertyValueFactory<Leave, LeaveRequestStatus>("employee"));
 
        TableColumn startDate = new TableColumn("Data rozpoczęcia");
        startDate.setMinWidth(150);
        startDate.setCellValueFactory(
                new PropertyValueFactory<Leave, LocalDate>("startDate"));
 
        TableColumn numberOfDays = new TableColumn("ilość dni");
        numberOfDays.setMinWidth(200);
        numberOfDays.setCellValueFactory(
                new PropertyValueFactory<Leave, Integer>("numberOfDays"));

        try {
            leaveTable.getItems().addAll(LeaveRepository.findPendingLeavesByManager(currentUser));
        } catch (Exception e) {
            e.printStackTrace();
        }
        leaveTable.getColumns().clear();
        leaveTable.getColumns().addAll(employee, startDate, numberOfDays);
    }

    public void onAcceptButtonClick() throws Exception {
        System.out.println("Akceptowanie:"+leaveTable.getSelectionModel().getSelectedItems().size());
        List<Leave> selected = leaveTable.getSelectionModel().getSelectedItems();
        for (Leave leave1 : selected) {
            LeaveRepository.acceptLeaveRequest(leave1);
        }
        leaveTable.getItems().removeAll(leaveTable.getSelectionModel().getSelectedItems());
        setChanged();
        notifyObservers();
    }
    
    public void onRejectButtonClick() throws Exception {
        System.out.println("Odrzucanie:"+leaveTable.getSelectionModel().getSelectedItems().size());
        List<Leave> selected = leaveTable.getSelectionModel().getSelectedItems();
        for (Leave leave1 : selected) {
            LeaveRepository.rejectLeaveRequest(leave1);
        }
        leaveTable.getItems().removeAll(leaveTable.getSelectionModel().getSelectedItems());
        setChanged();
        notifyObservers();
    }
}
