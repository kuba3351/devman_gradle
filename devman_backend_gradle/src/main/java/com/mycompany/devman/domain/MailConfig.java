package com.mycompany.devman.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by jakub on 15.05.17.
 */
@Entity
public class MailConfig {

    @Id
    @GeneratedValue
    private Long id;
    private String smtpHost;
    private String smtpPort;
    private String smtpLogin;
    private String smtpPassword;
    private Boolean mailConfigSkipped;
    private Boolean smtpAuthorization;
    private Boolean smtpSecured;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getMailConfigSkipped() {
        return mailConfigSkipped;
    }

    public void setMailConfigSkipped(Boolean mailConfigSkipped) {
        this.mailConfigSkipped = mailConfigSkipped;
    }

    public Boolean getSmtpAuthorization() {
        return smtpAuthorization;
    }

    public void setSmtpAuthorization(Boolean smtpAuthorization) {
        this.smtpAuthorization = smtpAuthorization;
    }

    public Boolean getSmtpSecured() {
        return smtpSecured;
    }

    public void setSmtpSecured(Boolean smtpSecured) {
        this.smtpSecured = smtpSecured;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getSmtpLogin() {
        return smtpLogin;
    }

    public void setSmtpLogin(String smtpLogin) {
        this.smtpLogin = smtpLogin;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }
}
