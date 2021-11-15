package dbconnect;

import models.Status;

import javax.inject.Singleton;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides database methods
 */
@Singleton
public class DBConnect {
    /**
     * Connect to the sqlite database
     * @return the connection
     */
    public Connection connect() {
        final String url = "jdbc:sqlite:../cms-data.db";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e){
            System.out.println(e.getMessage());
        };
        return connection;
    }

    public DBConnect() {
        final String createApisTable =
                "CREATE TABLE IF NOT EXISTS apis (" +
                "route varchar NOT NULL UNIQUE, " +
                "content varchar, " +
                "_limit int DEFAULT 10, " +
                "type varchar DEFAULT 'COLLECTION', " +
                "_order varchar DEFAULT 'DESC', " +
                "template varchar, " +
                "deleted varchar DEFAULT 'FALSE'" +
                ");";
        final String createEntriesTable =
                "CREATE TABLE IF NOT EXISTS entries (" +
                "parent varchar NOT NULL, " +
                "time int, " +
                "content varchar, " +
                "teaser varchar, " +
                "title varchar, " +
                "name varchar NOT NULL, " +
                "slug varchar NOT NULL UNIQUE, " +
                "deleted varchar DEFAULT 'FALSE'" +
                ");";
        final String createTemplatesTable =
                "CREATE TABLE IF NOT EXISTS templates (" +
                "parent varchar NOT NULL UNIQUE, " +
                "fields varchar, " +
                "teaser varchar," +
                "deleted varchar DEFAULT 'FALSE'" +
                ");";
        final String createUploadedFileArtifactsTable =
                "CREATE TABLE IF NOT EXISTS uploadedFileArtifacts (" +
                        "fileName varchar NOT NULL UNIQUE, " +
                        "originalFile int NOT NULL DEFAULT -1" +
                        ");";
        final String createUploadedFileMetaTable =
                "CREATE TABLE IF NOT EXISTS uploadedFileMeta (" +
                        "fileName varchar NOT NULL UNIQUE, " +
                        "time int, " +
                        "size int, " +
                        "extension varchar, " +
                        "details varchar " +
                        ");";
        final String createUploadedFileMetaFieldsTable =
                "CREATE TABLE IF NOT EXISTS uploadedFileDetailFields (" +
                        "name varchar NOT NULL UNIQUE ,"+
                        "fields varchar" +
                        ");";
        try {
            Connection connection = this.connect();
            Statement statement = connection.createStatement();
            statement.addBatch(createApisTable);
            statement.addBatch(createEntriesTable);
            statement.addBatch(createTemplatesTable);
            statement.addBatch(createUploadedFileArtifactsTable);
            statement.addBatch(createUploadedFileMetaFieldsTable);
            statement.addBatch(createUploadedFileMetaTable);
            statement.executeBatch();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String execute(String sql) {
        Connection connection = null;
        Statement statement = null;
        String result;
        try {
            connection = this.connect();
            statement = connection.createStatement();
            statement.execute(sql);
            result = Status.SUCCESS.toString();
        } catch(SQLException e) {
            result = e.getMessage();
        } finally {
            try{connection.close();}catch(Exception e){}
            try{statement.close();}catch (Exception e){}
        }
        return result;
    }

    public List<Map<String,Object>> getResults(String sql, Map<String,String> requiredColumns) {
        List<Map<String,Object>> results = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = this.connect();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Map<String, Object> result = new HashMap<>();
                for (String column : requiredColumns.keySet()) {
                    if(requiredColumns.get(column)==String.class.getSimpleName()){
                        result.put(column, resultSet.getString(column));
                    } else if(requiredColumns.get(column)==long.class.getSimpleName() ||
                        requiredColumns.get(column)==Long.class.getSimpleName()){
                        result.put(column, resultSet.getLong(column));
                    }
                }
                results.add(result);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try { resultSet.close(); } catch (Exception e) { /* Ignored */ }
            try { statement.close(); } catch (Exception e) { /* Ignored */ }
            try { connection.close(); } catch (Exception e) { /* Ignored */ }
        }
        return results;
    }

    public static Map<String,String> generateRequiredColumns(Object _object) {
        Map<String,String> result = new HashMap<>();
        Field[] fields = _object.getClass().getDeclaredFields();
        for (Field f:fields) {
            f.setAccessible(true);
            result.put(f.getName(),f.getType().getSimpleName());
        }
        return result;
    }

}
