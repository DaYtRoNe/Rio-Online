/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Properties;

/**
 *
 * @author daytr
 */
public class MySQL {
    
    private static Connection connection;
    private static final String CONFIG_FILE = "config/database.properties";
    
    public static void createConnection() throws Exception {
        if (connection == null) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Properties databaseProperties = loadDatabaseProperties();
            String url = readSetting(databaseProperties, "db.url", "DB_URL", "jdbc:mysql://localhost:3306/rio");
            String username = readSetting(databaseProperties, "db.username", "DB_USERNAME", "root");
            String password = readSetting(databaseProperties, "db.password", "DB_PASSWORD", "");

            connection = DriverManager.getConnection(url, username, password);
        }
    }
    
    public static ResultSet executeSearch(String query) throws Exception {
        createConnection();
        return connection.createStatement().executeQuery(query);
    }
    
    public static Integer executeIUD(String query) throws Exception {
        createConnection();
        return connection.createStatement().executeUpdate(query);
    }

    private static Properties loadDatabaseProperties() throws IOException {
        Properties properties = new Properties();

        try (InputStream inputStream = new FileInputStream(CONFIG_FILE)) {
            properties.load(inputStream);
        } catch (FileNotFoundException exception) {
            return properties;
        }

        return properties;
    }

    private static String readSetting(Properties properties, String propertyName, String environmentName, String defaultValue) {
        String systemProperty = System.getProperty(propertyName);
        if (systemProperty != null && !systemProperty.isBlank()) {
            return systemProperty;
        }

        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }

        String propertyValue = properties.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        return defaultValue;
    }
    
}