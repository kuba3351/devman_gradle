/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.devman.repositories;

import com.mycompany.devman.BackendSetup;
import com.mycompany.devman.domain.AccountType;
import com.mycompany.devman.domain.Project;
import com.mycompany.devman.domain.Team;
import com.mycompany.devman.domain.User;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * @author jakub
 */
public class UserRepository {

    public static String resetPassword(User user) throws Exception {
        StringBuilder newPassword = generateNewPassword(user);

        sendNewPasswordByEmail(user, newPassword);

        return newPassword.toString();
    }

    private static void sendNewPasswordByEmail(User user, StringBuilder newPassword) throws Exception {
        Message message = new MimeMessage(BackendSetup.getMailSession());
        message.setFrom(new InternetAddress("devmanmailer@gmail.com"));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(user.getEmail()));
        message.setSubject("Resetowanie hasła");
        message.setText("Witaj "+user.getName()+","
                + "\n\nSystem otrzymał żądanie zresrtowania hasła na twoim koncie."
                + "\n\nDo formularza należy wpisać następujące hasło:"+newPassword.toString()
                +"\n\nWiadomość wygenerowana automatycznie.");

        Transport.send(message);
    }

    private static StringBuilder generateNewPassword(User user) {
        Random generator = new Random();
        StringBuilder newPassword = new StringBuilder();
        int shuffle = generator.nextInt(10);
        int peselchar = generator.nextInt(10) + 1;
        int emailchar = generator.nextInt(4) + 1;
        int passwordchar = generator.nextInt(5) + 1;
        newPassword.append(user.getPesel().charAt(peselchar) + shuffle);
        newPassword.append(user.getEmail().charAt(emailchar) + shuffle);
        newPassword.append(user.getPassword().charAt(passwordchar) + shuffle);
        newPassword.append(generator.nextInt(100));
        newPassword.append(generator.nextInt(90));
        newPassword.append(generator.nextInt(80));

        return newPassword;
    }

    public static User addUserToDatabase(User user) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        if(user.getAccountType().equals(AccountType.EMPLOYEE)) {
            user.setHoursPerDay(8);
            user.setLeaveDaysPerYear(21);
        }
        validateEntity(user);
        Long id = (Long) session.save(user);
        user.setId(id);
        transaction.commit();
        session.close();
        return user;
    }

    private static void validateEntity(User user) throws Exception {
        Validator validator = BackendSetup.getEntityValidator();
        Set<ConstraintViolation<User>> users = validator.validate(user);
        String message = "";
        if (users.size() > 0) {
            Iterator iterator = users.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation<User> userError = (ConstraintViolation<User>) iterator.next();
                message += " " + userError.getMessage();
            }
            throw new Exception(message);
        }
    }

    public static User findByLoginAndPassword(String login, String password) throws Exception {
            Session session = BackendSetup.getDatabaseSession();
            Transaction transaction = session.beginTransaction();
            List<User> users = session.createQuery("FROM User u WHERE u.login=:login AND u.password=:password")
                    .setParameter("login", login).setParameter("password", password).list();
            transaction.commit();
            session.close();
            if(users.size() == 0) {
                throw new Exception("Błędny login lub hasło");
            }
            User user = (User) users.get(0);
            if(user.getUserState().equals(User.userState.INACTIVE)) {
                throw new Exception("Użytkownik jest nieaktywny");
            }
            return (User) users.get(0);
    }

    public static User findByEmailAndPesel(String email, String pesel) throws Exception {
        List users = null;
        try {
            Session session = BackendSetup.getDatabaseSession();
            Transaction transaction = session.beginTransaction();
            users = session.createQuery("FROM User u WHERE u.email=:email AND u.pesel=:pesel")
                    .setParameter("email", email).setParameter("pesel", pesel).list();
            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (users.size() == 1) {
            return (User) users.get(0);
        }

        throw new Exception("Zły email lub pesel");
    }

    public static void deleteUser(User user) throws Exception {

        try {
            Session session = BackendSetup.getDatabaseSession();
            Transaction transaction = session.beginTransaction();
            session.delete(user);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List findManagers() throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List users = session.createQuery("FROM User u WHERE u.accountType='MANAGER'").list();
        transaction.commit();
        session.close();
        return users;
    }

    public static List<User> findEmployees() throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<User> users = session.createQuery("FROM User u WHERE u.accountType='EMPLOYEE'").list();
        transaction.commit();
        session.close();
        return users;
    }

    public static User updateUser(User user) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        validateEntity(user);
        session.update(user);
        transaction.commit();
        session.close();
        return user;
    }

    public static List<User> findUsersByTeam(Team team) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<User> users = session.createQuery("SELECT u FROM User u JOIN u.teams t WHERE t=:team").setParameter("team", team).list();
        transaction.commit();
        session.close();
        return users;
    }

    public static List<User> findUsersByProject(Project project) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<User> users = session.createQuery("SELECT u FROM User u JOIN u.teams t WHERE t.project=:project").setParameter("project", project).list();
        transaction.commit();
        session.close();
        return users;
    }

    public static List<User> findUsersByManager(User manager) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<User> users = session.createQuery("SELECT u FROM User u WHERE u.manager=:manager").setParameter("manager", manager).list();
        transaction.commit();
        session.close();
        return users;
    }

    public static List<User> findAnotherUsersInTeam(User user) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<User> users = session.createQuery("SELECT u FROM User u JOIN u.teams t WHERE t in (SELECT t FROM User u JOIN u.teams t WHERE u=:user)").setParameter("user", user).list();
        transaction.commit();
        session.close();
        return users;
    }

    public static List<User> findInactiveUsers() throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List users = session.createQuery("FROM User u WHERE u.userState='INACTIVE'").list();
        transaction.commit();
        session.close();
        return users;
    }
}
