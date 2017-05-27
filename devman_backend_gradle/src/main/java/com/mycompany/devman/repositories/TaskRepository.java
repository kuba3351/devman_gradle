package com.mycompany.devman.repositories;

import com.mycompany.devman.BackendSetup;
import com.mycompany.devman.domain.Project;
import com.mycompany.devman.domain.Task;
import com.mycompany.devman.domain.Team;
import com.mycompany.devman.domain.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by bloczek on 29.03.2017.
 */
public class TaskRepository {

    public static Task addTask(Task task) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        task.setTaskState(Task.TaskState.IN_PROGRESS);
        validateEntity(task);
        session.save(task);
        transaction.commit();
        session.close();
        return task;
    }

    private static void validateEntity(Task task) throws Exception {
        Validator validator = BackendSetup.getEntityValidator();
        Set<ConstraintViolation<Task>> tasks = validator.validate(task);
        StringBuilder message = new StringBuilder();
        if (tasks.size() > 0) {
            Iterator iterator = tasks.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation<Task> taskError = (ConstraintViolation<Task>) iterator.next();
                message.append(", ").append(taskError.getMessage());
            }
            throw new Exception(message.toString());
        }
        if(task.getStartDate().isAfter(task.getEndDate())) {
            throw new Exception("Data rozpoczęcia nie może być po dacie zakończenia.");
        }
    }

    public static Task updateTask(Task task) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        validateEntity(task);
        session.update(task);
        transaction.commit();
        session.close();
        return task;
    }

    public static Task findById(Long id) throws Exception {
        List tasks = null;
        try {
            Session session = BackendSetup.getDatabaseSession();
            Transaction transaction = session.beginTransaction();
            tasks = session.createQuery("FROM Tasks u WHERE u.id=:id")
                    .setParameter("id", id).list();
            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tasks.size() == 1) {
            return (Task) tasks.get(0);
        }

        throw new Exception("Nie istniejace zadanie");
    }

    public static void deleteById(Long id) throws Exception {
        try {
            Session session = BackendSetup.getDatabaseSession();
            Transaction transaction = session.beginTransaction();
            Query query = session.createQuery("Delete from Task where id=:id").setParameter("id",id);
            query.executeUpdate();
            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List findByName(String name) {
        List tasks = null;
        try {
            Session session = BackendSetup.getDatabaseSession();
            Transaction transaction = session.beginTransaction();
            tasks = session.createQuery("FROM tasks u WHERE u.name=:name")
                    .setParameter("name", name).list();
            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public static List findByTeamId(Long id) throws Exception {
        List tasks = null;
        try {
            Session session = BackendSetup.getDatabaseSession();
            Transaction transaction = session.beginTransaction();
            tasks = session.createQuery("FROM Tasks u WHERE u.team_id=:id")
                    .setParameter("id", id).list();
            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
            return tasks;
    }
    
    public static List<Task> findAllTasks() throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<Task> tasks = session.createQuery("FROM Task").list();
        transaction.commit();
        session.close();
        return tasks;
    }
    
    public static List<Task> findTasksByTeam(Team team) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<Task> tasks = session.createQuery("FROM Task t WHERE t.team=:team AND t.taskState='IN_PROGRESS'").setParameter("team", team).list();
        transaction.commit();
        session.close();
        return tasks;
    }
    
    public static List<Task> findTasksByProject(Project project) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<Task> tasks = session.createQuery("FROM Task t WHERE t.team.project=:project AND t.taskState='IN_PROGRESS'").setParameter("project", project).list();
        transaction.commit();
        session.close();
        return tasks;
    }

    public static List<Task> findTasksInProgress() throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<Task> tasks = session.createQuery("FROM Task t WHERE t.taskState='IN_PROGRESS'").list();
        transaction.commit();
        session.close();
        return tasks;
    }

    public static List<Task> findCompletedTasks() throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<Task> tasks = session.createQuery("FROM Task t WHERE t.taskState='FINISHED'").list();
        transaction.commit();
        session.close();
        return tasks;
    }


    
    public static List<Task> findTasksInProgressByUser(User user) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<Task> tasks = session.createQuery("FROM Task t WHERE t.taskState='IN_PROGRESS' AND t.team in (SELECT t FROM User u JOIN u.teams t WHERE u=:user)").setParameter("user", user).list();
        transaction.commit();
        session.close();
        return tasks;
    }

    public static List<Task> findCompletedTasksByUser(User user) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<Task> tasks = session.createQuery("FROM Task t WHERE t.taskState='FINISHED' AND t.team in (SELECT t FROM User u JOIN u.teams t WHERE u=:user)").setParameter("user", user).list();
        transaction.commit();
        session.close();
        return tasks;
    }

    public static List<Task> findCompletedTasksByTeam(Team team) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<Task> tasks = session.createQuery("FROM Task t WHERE t.team=:team AND t.taskState='FINISHED'").setParameter("team", team).list();
        transaction.commit();
        session.close();
        return tasks;
    }

    public static List<Task> findCompletedTasksByProject(Project project) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<Task> tasks = session.createQuery("FROM Task t WHERE t.team.project=:project AND t.taskState='FINISHED'").setParameter("project", project).list();
        transaction.commit();
        session.close();
        return tasks;
    }
}
