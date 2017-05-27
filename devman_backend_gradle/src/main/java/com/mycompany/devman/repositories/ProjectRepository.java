package com.mycompany.devman.repositories;

import com.mycompany.devman.BackendSetup;
import com.mycompany.devman.domain.Project;
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
public class ProjectRepository {

    public static Project addProject(Project project) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        project.setProjectState(Project.ProjectState.IN_PROGRESS);
        validateEntity(project);
        session.save(project);
        transaction.commit();
        session.close();
        return project;
    }

    public static Project updateProject(Project project) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        validateEntity(project);
        session.update(project);
        transaction.commit();
        session.close();
        return project;
    }

    private static void validateEntity(Project project) throws Exception {
        Validator validator = BackendSetup.getEntityValidator();
        Set<ConstraintViolation<Project>> projects = validator.validate(project);
        StringBuilder message = new StringBuilder();
        if (projects.size() > 0) {
            Iterator iterator = projects.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation<Project> projectError = (ConstraintViolation<Project>) iterator.next();
                message.append(", ").append(projectError.getMessage());
            }
            throw new Exception(message.toString());
        }
        if(project.getStartDate().isAfter(project.getEndDate()))
            throw new Exception("Data rozpoczęcia musi być przed datą zakończenia prjektu.");
    }

    public static Project findById(Long id) throws Exception {
        List projects = null;
        try {
            Session session = BackendSetup.getDatabaseSession();
            Transaction transaction = session.beginTransaction();
            projects = session.createQuery("FROM Project u WHERE u.id=:id")
                    .setParameter("id", id).list();
            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (projects.size() == 1) {
            return (Project) projects.get(0);
        }

        throw new Exception("Nie istniejace zadanie");
    }

    public static void deleteById(Long id) throws Exception {

        try {
            Session session = BackendSetup.getDatabaseSession();
            Transaction transaction = session.beginTransaction();
            Query query = session.createQuery("Delete from projects where id=:id").setParameter("id",id);
            query.executeUpdate();
            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List findByName(String name) {
        List projects = null;
        try {
            Session session = BackendSetup.getDatabaseSession();
            Transaction transaction = session.beginTransaction();
            projects = session.createQuery("FROM projects u WHERE u.name=:name")
                    .setParameter("name", name).list();
            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return projects;
    }
    
    public static List<Project> findProjectsInProgress() throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<Project> projects = session.createQuery("FROM Project p WHERE p.projectState='IN_PROGRESS'").list();
        transaction.commit();
        session.close();
        return projects;
    }

    public static List<Project> findCompletedProjects() throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<Project> projects = session.createQuery("FROM Project p WHERE p.projectState='FINISHED'").list();
        transaction.commit();
        session.close();
        return projects;
    }
    
    public static List<Project> findProjectsInProgressByUser(User user) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<Project> projects = session.createQuery("SELECT DISTINCT team.project FROM User u JOIN u.teams team WHERE u=:user AND team.project.projectState='IN_PROGRESS'").setParameter("user", user).list();
        transaction.commit();
        session.close();
        return projects;
    }



    public static List<Project> findAllProjects() throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<Project> projects = session.createQuery("FROM Project").list();
        transaction.commit();
        session.close();
        return projects;
    }
}
