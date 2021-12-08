package dbconnect;

import models.SQLNotGettable;
import models.Status;

import javax.inject.Singleton;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

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
                "template varchar, " +
                "deleted varchar DEFAULT 'FALSE'" +
                ");";
        final String createEntriesTable =
                "CREATE TABLE IF NOT EXISTS entries (" +
                "parent int NOT NULL, " +
                "time int, " +
                "content varchar, " +
                "teaser varchar, " +
                "title varchar, " +
                "name varchar NOT NULL, " +
                "deleted varchar DEFAULT 'FALSE', " +
                "UNIQUE(parent, name)" +
                ");";
        final String createTemplatesTable =
                "CREATE TABLE IF NOT EXISTS templates (" +
                "parent int NOT NULL UNIQUE, " +
                "content varchar, " +
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
            try{statement.close();}catch (Exception e){}
            try{connection.close();}catch(Exception e){}
        }
        return result;
    }

    public String executeBatch(List<String> sqls) {
        Connection connection = null;
        Statement statement = null;
        String result;
        try {
            connection = this.connect();
            statement = connection.createStatement();
            for (String sql : sqls) {
                statement.addBatch(sql);
            }
            statement.executeBatch();
            result = Status.SUCCESS.toString();
        } catch(SQLException e) {
            result = e.getMessage();
        } finally {
            try{ statement.close(); }catch (Exception ignored){}
            try{ connection.close(); }catch(Exception ignored){}
        }
        return result;
    }

    public List<Map<String,Object>> getResults(String sql, Map<String,String> requiredColumns) {
        List<Map<String,Object>> results = new ArrayList<>();
        ResultSet resultSet = null;
        Connection connection = null;
        Statement statement = null;
        try {
            connection = this.connect();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Map<String, Object> result = new HashMap<>();
                for (String column : requiredColumns.keySet()) {
                    if (requiredColumns.get(column).equals(long.class.getSimpleName()) ||
                            requiredColumns.get(column).equals(Long.class.getSimpleName())) {
                        result.put(column, resultSet.getLong(column));
                    } else if (requiredColumns.get(column).equals(Integer.class.getSimpleName())) {
                        result.put(column, resultSet.getInt(column));
                    } else {
                        result.put(column, resultSet.getString(column));
                    }
                }
                results.add(result);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {resultSet.close();} catch (Exception ignored) {}
            try{statement.close();}catch (Exception ignored){}
            try{connection.close();}catch(Exception ignored){}
        }
        return results;
    }

    public static Map<String,String> generateRequiredColumns(Class<?> cls) {
        Map<String,String> result = new HashMap<>();
        Field[] fields = cls.getDeclaredFields();
        for (Field f:fields) {
            f.setAccessible(true);
            if(!f.isAnnotationPresent(SQLNotGettable.class)){
                result.put(f.getName(),f.getType().getSimpleName());
            }
        }
        return result;
    }

}
