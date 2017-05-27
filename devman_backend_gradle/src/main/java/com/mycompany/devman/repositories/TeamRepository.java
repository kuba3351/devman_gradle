/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.devman.repositories;

import com.mycompany.devman.BackendSetup;
import com.mycompany.devman.domain.Project;
import com.mycompany.devman.domain.Task;
import com.mycompany.devman.domain.Team;
import com.mycompany.devman.domain.User;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author jakub
 */
public class TeamRepository {
    public static Team addTeam(Team team) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        validateEntity(team);
        session.save(team);
        transaction.commit();
        session.close();
        return team;
    }

    private static void validateEntity(Team team) throws Exception {
        Validator validator = BackendSetup.getEntityValidator();
        Set<ConstraintViolation<Team>> teams = validator.validate(team);
        String message = "";
        if (teams.size() > 0) {
            Iterator iterator = teams.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation<Team> teamError = (ConstraintViolation<Team>) iterator.next();
                message += " " + teamError.getMessage();
            }
            throw new Exception(message);
        }
    }

    public static List<Team> findAllTeams() throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<Team> teams = session.createQuery("FROM Team").list();
        transaction.commit();
        session.close();
        return teams;
    }

    public static void deleteTeam(Team team) throws Exception {
        for(Task task : TaskRepository.findAllTasks()) {
            if(task.getTeam() != null && task.getTeam().equals(team)) {
                task.setTeam(null);
                TaskRepository.updateTask(task);
            }
        };
        try {
            Session session = BackendSetup.getDatabaseSession();
            Transaction transaction = session.beginTransaction();
            session.delete(team);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Team updateTeam(Team team) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        validateEntity(team);
        session.update(team);
        transaction.commit();
        session.close();
        return team;
    }
    
    public static List<Team> findTeamsByProjectAndUser(Project project, User user) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<Team> teams = session.createQuery("SELECT t FROM User u JOIN u.teams t WHERE t.project=:project AND u=:user").setParameter("project", project).setParameter("user", user).list();
        transaction.commit();
        session.close();
        return teams;
    }
    
    public static List<Team> findTeamsByUser(User user) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<Team> teams = session.createQuery("SELECT t FROM User u JOIN u.teams t WHERE u=:user").setParameter("user", user).list();
        transaction.commit();
        session.close();
        return teams;
    }

    public static List<Team> findTeamsByProjrct(Project project) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<Team> teams = session.createQuery("FROM Team t WHERE t.project=:project").setParameter("project", project).list();
        transaction.commit();
        session.close();
        return teams;
    }
}
