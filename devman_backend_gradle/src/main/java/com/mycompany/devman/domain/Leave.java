/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.devman.domain;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author jakub
 */

@Entity
@Table(name = "leaves")
public class Leave {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "employee_id")
    @NotNull(message = "pracownik nie może być nullem")
    private User employee;
    
    @NotNull(message = "status urlopu nie może być nullem")
    @Enumerated(EnumType.STRING)
    private LeaveRequestStatus status;
    
    @NotNull(message = "Data rozpoczęcia nie może być nullem")
    private LocalDate startDate;
    
    @NotNull(message = "ilość dni nie może być nullem")
    @Min(1)
    @Max(14)
    private Integer numberOfDays;

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
     * @return the employee
     */
    public User getEmployee() {
        return employee;
    }

    /**
     * @param employee the employee to set
     */
    public void setEmployee(User employee) {
        this.employee = employee;
    }

    /**
     * @return the startDate
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the numberOfDays
     */
    public Integer getNumberOfDays() {
        return numberOfDays;
    }

    /**
     * @param numberOfDays the numberOfDays to set
     */
    public void setNumberOfDays(Integer numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    /**
     * @return the status
     */
    public LeaveRequestStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(LeaveRequestStatus status) {
        this.status = status;
    }
}
