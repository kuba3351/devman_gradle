package com.mycompany.devman;

/**
 * Created by jakub on 12.05.17.
 */
public class DatabaseSetup {
    private String host;
    private String port;
    private String name;
    private String login;
    private String password;

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return "jdbc:mysql://"+host+":"+port+"/"+name;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
