/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.devman.domain;

import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author jakub
 */

@Entity
@Table(name = "tasks")
public class Task {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @NotEmpty(message = "Naazwa nie może być pusta")
    @NotNull(message = "Nazwa nie może być nullem")
    private String name;
    
    @NotNull(message = "Data rozpoczęcia nie może być nullem")
    private LocalDate startDate;
    
    @NotNull(message = "Data zakończenie nie może być nullem")
    private LocalDate endDate;
    
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
    
    @NotNull(message = "Przewidywany czas nie może być nullem")
    private Integer predictedTime;


    public enum TaskState{IN_PROGRESS,FINISHED}

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Stan zadania nie może być nullem")
    private TaskState taskState;

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
     * @return the endDate
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the team
     */
    public Team getTeam() {
        return team;
    }

    /**
     * @param team the team to set
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * @return the predictedTime
     */
    public Integer getPredictedTime() {
        return predictedTime;
    }

    /**
     * @param predictedTime the predictedTime to set
     */
    public void setPredictedTime(Integer predictedTime) {
        this.predictedTime = predictedTime;
    }

    /**
     * @return the taskStatus
     */
    public TaskState getTaskState() {
        return taskState;
    }

    /**
     * @param taskState the taskState to set
     */
    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
