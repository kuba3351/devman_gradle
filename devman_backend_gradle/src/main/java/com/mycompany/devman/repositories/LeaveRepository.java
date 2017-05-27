/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.devman.repositories;

import com.mycompany.devman.*;
import com.mycompany.devman.domain.AccountType;
import com.mycompany.devman.domain.Leave;
import com.mycompany.devman.domain.LeaveRequestStatus;
import com.mycompany.devman.domain.User;
import java.time.LocalDate;
import java.util.*;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author bloczek
 */
public class LeaveRepository {
    public static Leave addNewLeaveRequest(Leave leave) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        leave.setStatus(LeaveRequestStatus.OCZEKUJE);
        validateEntity(leave);
        Long id = (Long) session.save(leave);
        leave.setId(id);
        transaction.commit();
        session.close();
        return leave;
    }

    private static void validateEntity(Leave leave) throws Exception {
        Validator validator = BackendSetup.getEntityValidator();
        Set<ConstraintViolation<Leave>> leaves = validator.validate(leave);
        StringBuilder message = new StringBuilder();
        if (leaves.size() > 0) {
            Iterator iterator = leaves.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation<User> userError = (ConstraintViolation<User>) iterator.next();
                message.append(", ").append(userError.getMessage());
            }
            throw new Exception(message.toString());
        }
        if(!leave.getEmployee().getAccountType().equals(AccountType.EMPLOYEE)) {
            throw new Exception("Składający wniosek nie jest pracownikiem");
        }
        if(leave.getStartDate().isBefore(LocalDate.now())) {
            throw new Exception("Data rozpoczęcia musi byc z przyszlości.");
        }
    }

    public static Optional<LocalDate> getNextLeaveDate(User user) throws Exception {
        return findLeavesByUser(user).stream().map(Leave::getStartDate).sorted().findFirst();
    }

    public static Integer getReamingLeaveDays(User user) throws Exception {
        int totalLeaveDays = user.getLeaveDaysPerYear();
        List<Leave> leaves = findAcceptedLeavesByUser(user);
        int usedLeaveDays = leaves.stream()
                .filter(leave -> leave.getStartDate()
                        .getYear() == LocalDate.now()
                        .getYear()).mapToInt(Leave::getNumberOfDays).sum();
        int returnValue = totalLeaveDays - usedLeaveDays;
        if(returnValue < 0) {
            return 0;
        }
        else {
            return returnValue;
        }
    }

    public static List<Leave> findLeavesByUser(User user) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<Leave> leaves = session.createQuery("FROM Leave AS l WHERE l.employee=:user").setParameter("user", user).list();
        transaction.commit();
        session.close();
        return leaves;
    }
    public static List<Leave> findAcceptedLeavesByUser(User user) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<Leave> leaves = session.createQuery("FROM Leave AS l WHERE l.employee=:user AND l.status='ZAAKCEPTOWANY'").setParameter("user", user).list();
        transaction.commit();
        session.close();
        return leaves;
    }
    public static List<Leave> findPendingLeavesByManager(User manager) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<Leave> leaves = session.createQuery("FROM Leave AS l WHERE l.employee.manager=:manager AND l.status = 'OCZEKUJE'").setParameter("manager", manager).list();
        transaction.commit();
        session.close();
        return leaves;
    }
    public static void acceptLeaveRequest(Leave leave) throws Exception {
        leave.setStatus(LeaveRequestStatus.ZAAKCEPTOWANY);
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        session.update(leave);
        transaction.commit();
        session.close();
    }
    public static void rejectLeaveRequest(Leave leave) throws Exception {
        leave.setStatus(LeaveRequestStatus.ODRZUCONY);
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        session.update(leave);
        transaction.commit();
        session.close();
    }
}
