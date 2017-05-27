package com.mycompany.devman.repositories;

import com.mycompany.devman.BackendSetup;
import com.mycompany.devman.domain.Task;
import com.mycompany.devman.domain.User;
import com.mycompany.devman.domain.WorkTime;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by jakub on 09.05.17.
 */
public class WorkTimeRepository {
    public static WorkTime addNewWorkTime(WorkTime workTime) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        validateEntity(workTime);
        session.save(workTime);
        transaction.commit();
        session.close();
        return workTime;
    }

    private static void validateEntity(WorkTime workTime) throws Exception {
        if(workTime.getDate().isAfter(LocalDate.now())) {
            throw new Exception("Nie można zalogować zadania z przyszłości!");
        }
        Validator validator = BackendSetup.getEntityValidator();
        Set<ConstraintViolation<WorkTime>> leaves = validator.validate(workTime);
        StringBuilder message = new StringBuilder();
        if (leaves.size() > 0) {
            Iterator iterator = leaves.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation<WorkTime> userError = (ConstraintViolation<WorkTime>) iterator.next();
                message.append(", ").append(userError.getMessage());
            }
            throw new Exception(message.toString());
        }
    }

    public static List<WorkTime> findWorkTimeByTask(Task task) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<WorkTime> list = session.createQuery("FROM WorkTime w WHERE w.task=:task").setParameter("task", task).list();
        transaction.commit();
        session.close();
        return list;
    }

    public static Integer calculateReamingWorkHoursToday(User user) throws Exception {
        int totalHours = user.getHoursPerDay();
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<WorkTime> list = session.createQuery("FROM WorkTime w WHERE w.date=:date").setParameter("date", LocalDate.now()).list();
        transaction.commit();
        session.close();
        int usedHours = list.stream().mapToInt(WorkTime::getWorkTime).sum();
        int returnValue = totalHours - usedHours;
        if(returnValue < 0) {
            return 0;
        }
        else {
            return returnValue;
        }
    }

    public static List<WorkTime> findByUser(User user) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<WorkTime> list = session.createQuery("FROM WorkTime w WHERE w.user=:user").setParameter("user", user).list();
        transaction.commit();
        session.close();
        return list;
    }

    public static void updateWorkTime(WorkTime workTime) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        validateEntity(workTime);
        session.update(workTime);
        transaction.commit();
        session.close();
    }
}
