package ca.jrvs.apps.jdbc;

import java.sql.SQLException;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnectionManager {
    private final String url;
    private final Properties properties;

    public DatabaseConnectionManager(String host, String databaseNmae, String username, String password ){
        this.url = "jdbc:postgresql://"+host+"/"+databaseNmae;
        this.properties = new Properties();
        this.properties.setProperty("user",username);
        this.properties.setProperty("password",password);
    }

    public Connection getConnection() throws SQLException{
        return DriverManager.getConnection(this.url, this.properties);
    }
}
