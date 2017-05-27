/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.devman.domain;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author jakub
 */

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private Long id;
    
    @NotNull(message = "Login nie może być nullem")
    @NotEmpty(message = "Login nie może być pusty!")
    private String login;
    
    @NotNull(message = "Hasło nie może być nullem")
    @Size(min = 6, message = "hasło musi mieć co najmniej 6 znaków")
    private String password;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Typ konta nie może być nullem")
    private AccountType accountType;
    
    @NotNull(message = "Imię nie może być nullem")
    @Size(min = 3, message = "Imię musi mieć co najmniej 3 litery")
    private String name;
    
    @NotNull(message = "Nazwisko nie może być nullem")
    @Size(min = 3, message = "Nazwisko musi mieć co najmniej 3 litery")
    private String lastName;
    
    @Email(message = "Adres e-mail musi przynajmniej stwarzać pozory")
    @NotNull(message = "e-mail nie może być nullem")
    private String email;
    
    @NotNull(message = "Pesel nie może być nullem")
    @Size(min = 11, max = 11, message = "pesel musi mieć 11 znaków")
    private String pesel;
    
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;
    
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Team> teams;

    public enum userState{ACTIVE,INACTIVE;};

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Stan użytkownika nie może być nieokreślony")
    private userState userState;

    private Integer hoursPerDay;

    private Integer leaveDaysPerYear;

    public Integer getHoursPerDay() {
        return hoursPerDay;
    }

    public void setHoursPerDay(Integer hoursPerDay) {
        this.hoursPerDay = hoursPerDay;
    }

    public Integer getLeaveDaysPerYear() {
        return leaveDaysPerYear;
    }

    public void setLeaveDaysPerYear(Integer leaveDaysPerYear) {
        this.leaveDaysPerYear = leaveDaysPerYear;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the login
     */
    public String getLogin() {
        return login;
    }

    /**
     * @param login the login to set
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the accountType
     */
    public AccountType getAccountType() {
        return accountType;
    }

    /**
     * @param accountType the accountType to set
     */
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the pesel
     */
    public String getPesel() {
        return pesel;
    }

    /**
     * @param pesel the pesel to set
     */
    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    /**
     * @return the manager
     */
    public User getManager() {
        return manager;
    }

    /**
     * @param manager the manager to set
     */
    public void setManager(User manager) {
        this.manager = manager;
    }
    
    @Override
    public String toString() {
        return getName()+" "+getLastName();
    }

    /**
     * @return the teams
     */
    public List<Team> getTeams() {
        return teams;
    }

    /**
     * @return the user's state
     */
    public userState getUserState() {
        return userState;
    }

    /**
     * @param userState the state to set
     */
    public void setUserState(User.userState userState) {
        this.userState = userState;
    }

    /**
     * @param teams the teams to set

     */
    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
}
